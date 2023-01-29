package ch.zhaw.catan;

import ch.zhaw.catan.Config.Faction;
import ch.zhaw.catan.Config.Resource;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * This class performs all actions related to modifying the game state.
 * <p>
 * @author Zwahlen Nico, Michel Fäh
 * @version 27.11.2022
 */
public class SiedlerGame {
    static final int FOUR_TO_ONE_TRADE_OFFER = 4;
    static final int FOUR_TO_ONE_TRADE_WANT = 1;
    private final int requiredWinPoints;
    private final Player[] players;
    private Player currentPlayer;
    private int currentPlayerIndex;
    private final SiedlerBoard board;
    private final CardBank bank;

    /**
     * Constructs a SiedlerGame game state object.
     *
     * @param winPoints the number of points required to win the game
     * @param numberOfPlayers the number of players
     * @throws IllegalArgumentException if the winPoints parameter is lower than the min win points or players
     * is not in required range.
     */
    public SiedlerGame(int winPoints, int numberOfPlayers) {
        if(winPoints < Config.MIN_WIN_POINTS || winPoints > Config.MAX_WIN_POINTS ||
                numberOfPlayers < Config.MIN_NUMBER_OF_PLAYERS || numberOfPlayers > Config.MAX_NUMBER_OF_PLAYERS){
            throw new IllegalArgumentException("WinPoints or numberOfPlayers was not in valid range!");
        }
        this.requiredWinPoints = winPoints;
        players = createPlayers(numberOfPlayers);
        currentPlayerIndex = 0;
        currentPlayer = getCurrentPlayer();
        bank = new CardBank(new HashMap<>(Config.INITIAL_RESOURCE_CARDS_BANK));
        board = new SiedlerBoard(bank);
    }

    /**
     * Switches to the next player in the defined sequence of players.
     */
    public void switchToNextPlayer() {
        currentPlayerIndex = ((currentPlayerIndex + 1) % (players.length));
        currentPlayer = getCurrentPlayer();
    }

    /**
     * Switches to the previous player in the defined sequence of players.
     */
    public void switchToPreviousPlayer() {
        currentPlayerIndex = ((currentPlayerIndex + (players.length - 1)) % (players.length));
        currentPlayer = getCurrentPlayer();
    }

    /**
     * Returns the {@link Faction}s of the active players.
     *
     * <p>The order of the player's factions in the list must
     * correspond to the oder in which they play.
     * Hence, the player that sets the first settlement must be
     * at position 0 in the list etc.
     * </p><p>
     * <strong>Important note:</strong> The list must contain the
     * factions of active players only.</p>
     *
     * @return the list with player's factions.
     */
    public List<Faction> getPlayerFactions() {
        List<Config.Faction> factions = new ArrayList<>();
        for (Player player : players) {
            factions.add(player.getFaction());
        }
        return factions;
    }

    /**
     * Returns the {@link Faction} of the current player.
     *
     * @return the faction of the current player.
     */
    public Faction getCurrentPlayerFaction() {
        return currentPlayer.getFaction();
    }

    /**
     * Returns how many resource cards of the specified type
     * the current player owns.
     *
     * @param resource specifies the resource type.
     * @return the number of resource cards of this type.
     * @throws IllegalArgumentException if resources parameter is null.
     */
    public int getCurrentPlayerResourceStock(Resource resource) {
        if(resource == null){
            throw new IllegalArgumentException("Resources parameter must not be null!");
        }
        return currentPlayer.getResources().get(resource);
    }

    /**
     * Places a settlement in the founder's phase (phase II) of the game.
     *
     * <p>The placement does not cost any resource cards. If payout is
     * set to true, for each adjacent resource-producing field, a resource card of the
     * type of the resource produced by the field is taken from the bank (if available) and added to
     * the players' stock of resource cards.</p>
     *
     * @param position the position of the settlement.
     * @param payout if true, the player gets one resource card per adjacent resource-producing field.
     * @return true, if the placement was successful.
     * @throws IllegalArgumentException if the position parameter is null.
     */
    public boolean placeInitialSettlement(Point position, boolean payout) {
        if(position == null){
            throw new IllegalArgumentException("Position parameter must not be null");
        }
        if (!doesSettlementMeetRequirements(position, false)) {
            return false;
        }
        Settlement newSettlement = new Settlement(currentPlayer);
        board.setCorner(position, newSettlement);
        currentPlayer.removeStructure(Config.Structure.SETTLEMENT);
        currentPlayer.addWinPoints(newSettlement.getPayoutFactor());

        if (payout) {
            HashMap<Config.Resource, Integer> resources = Config.createEmptyResourceMap();
            List<Config.Land> lands = board.getLandsForCorner(position);
            for (Config.Land land: lands) {
                if (resources.containsKey(land.getResource())) {
                    resources.put(land.getResource(), resources.get(land.getResource()) + 1);
                }
            }
            currentPlayer.addResources(resources);
            bank.tryRemoveResources(resources);
        }
        return true;
    }

    /**
     * Places a road in the founder's phase (phase II) of the game.
     * The placement does not cost any resource cards.
     *
     * @param roadStart specifies the position of the start of the initial road.
     * @param roadEnd specifies the position of the end of the initial road.
     * @return true, if the placement was successful, false otherwise.
     * @throws IllegalArgumentException if the roadStart or roadEnd parameter is null.
     */
    public boolean placeInitialRoad(Point roadStart, Point roadEnd) {
        if(roadStart == null || roadEnd == null){
            throw new IllegalArgumentException("RoadStart and RoadEnd parameter must not be null");
        }
        if (!doesRoadMeetRequirements(roadStart, roadEnd, false)) {
            return false;
        }

        currentPlayer.removeStructure(Config.Structure.ROAD);
        board.setEdge(roadStart, roadEnd, new Road(currentPlayer));
        return true;
    }

    /**
     * This method takes care of actions depending on the dice throw result.
     * <p>
     * A key action is the payout of the resource cards to the players
     * according to the payout rules of the game. This includes the
     * "negative payout" in case a 7 is thrown and a player has more than
     * {@link Config#MAX_CARDS_IN_HAND_NO_DROP} resource cards.
     * </p><p>
     * If a player does not get resource cards, the list for this players'
     * {@link Faction} is <b>an empty list (not null)</b>!.
     * </p><p>
     * The payout rules of the game take into account factors such as, the number
     * of resource cards currently available in the bank, settlement types
     * (settlement or city), and the number of players that should get resource
     * cards of a certain type (relevant if there are not enough left in the bank).
     * </p>
     *
     * @param dicethrow specifies the resource cards that have been distributed to the players.
     * @return the resource cards added to the stock of the different players.
     * @throws IllegalArgumentException if the dicethrow parameter is not in valid range.
     */
    public Map<Faction, List<Resource>> throwDice(int dicethrow) {
        if(dicethrow < Config.MIN_DICE_VALUE || dicethrow > Config.MAX_DICE_VALUE){
            throw new IllegalArgumentException("DiceThrow parameter is not in valid range!");
        }
        Map<Config.Faction, HashMap<Config.Resource, Integer>> factionResourcePayout;

        if (dicethrow == Config.DROP_CARDS_DICE_VALUE) {
            factionResourcePayout = Config.createEmptyResourcePerFactionMap(getPlayerFactions());
            for (Player player : players) {
                bank.addResources(player.dropHalfResources());
            }
        } else {
            factionResourcePayout = board.getResourcesToPayPerFaction(dicethrow, getPlayerFactions());
            for (Player player : players) {
                Map<Config.Resource, Integer> playerResourcePayout = factionResourcePayout.get(player.getFaction());
                bank.tryRemoveResources(playerResourcePayout);
                player.addResources(playerResourcePayout);
            }
        }

        Map<Faction, List<Resource>> resourcesPerFactionList = new HashMap<>();
        for (Config.Faction faction : factionResourcePayout.keySet()) {
            resourcesPerFactionList.put(faction, translateFromMapToList(factionResourcePayout.get(faction)));
        }
        return resourcesPerFactionList;
    }

    /**
     * Builds a settlement at the specified position on the board.
     *
     * <p>The settlement can be built if:
     * <ul>
     * <li> the player possesses the required resource cards</li>
     * <li> a settlement to place on the board</li>
     * <li> the specified position meets the build rules for settlements</li>
     * </ul>
     *
     * @param position specifies the position of the settlement.
     * @return true, if the placement was successful, otherwise false.
     * @throws IllegalArgumentException if the position parameter is null.
     */
    public boolean buildSettlement(Point position) {
        if(position == null){
            throw new IllegalArgumentException("Position parameter must not be null");
        }
        if (!doesSettlementMeetRequirements(position, true)) {
            return false;
        }

        Map<Resource, Integer> settlementCosts = Config.Structure.SETTLEMENT.getCostsAsMap();
        bank.addResources(settlementCosts);
        currentPlayer.tryRemoveResources(settlementCosts);
        currentPlayer.removeStructure(Config.Structure.SETTLEMENT);
        Settlement newSettlement = new Settlement(currentPlayer);
        board.setCorner(position, newSettlement);
        currentPlayer.addWinPoints(newSettlement.getPayoutFactor());
        return true;
    }

    /**
     * Builds a city at the specified position on the board.
     *
     * <p>The city can be built if:
     * <ul>
     * <li> the player possesses the required resource cards</li>
     * <li> a city to place on the board</li>
     * <li> the specified position meets the build rules for cities</li>
     * </ul>
     *
     * @param position specifies the position of the city.
     * @return true, if the placement was built successfully, false otherwise.
     * @throws IllegalArgumentException if the position parameter is null.
     */
    public boolean buildCity(Point position) {
        if (position == null) {
            throw new IllegalArgumentException("Position parameter must not be null!");
        }

        boolean playerHasStructure = currentPlayer.checkIfStructureAvailable(Config.Structure.CITY);
        if (!playerHasStructure){
            return false;
        }

        Map<Resource, Integer> cityCosts = Config.Structure.CITY.getCostsAsMap();
        if (!currentPlayer.checkIfEnoughOfAllResources(cityCosts)) {
            return false;
        }

        try {
            Settlement corner = board.getCorner(position);
            if (corner == null || !corner.isOwnedBy(currentPlayer)) {
                return false;
            }

            // Corner is already city
            if (corner instanceof City) {
                return false;
            }

            bank.addResources(cityCosts);
            currentPlayer.removeWinPoints(corner.getWinPointAmount());
            currentPlayer.removeStructure(Config.Structure.CITY);
            currentPlayer.addStructure(Config.Structure.SETTLEMENT);
            currentPlayer.tryRemoveResources(cityCosts);
            board.setCorner(position, City.fromSettlement(corner));
            currentPlayer.addWinPoints(board.getCorner(position).getWinPointAmount());
            return true;

        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Builds a road at the specified position on the board.
     *
     * <p>The road can be built if:
     * <ul>
     * <li> the player possesses the required resource cards</li>
     * <li> a road to place on the board</li>
     * <li> the specified position meets the build rules for roads</li>
     * </ul>
     *
     * @param roadStart the position of the start of the road
     * @param roadEnd the position of the end of the road
     * @return true, if the placement was successful, false otherwise.
     * @throws IllegalArgumentException if the roadStart or roadEnd parameter was null.
     */
    public boolean buildRoad(Point roadStart, Point roadEnd) {
        if(roadStart == null || roadEnd == null){
            throw new IllegalArgumentException("RoadStart and RoadEnd parameter must not be null!");
        }

        if (!doesRoadMeetRequirements(roadStart, roadEnd, true)) {
            return false;
        }
        currentPlayer.tryRemoveResources(Config.Structure.ROAD.getCostsAsMap());
        currentPlayer.removeStructure(Config.Structure.ROAD);
        board.setEdge(roadStart, roadEnd, new Road(currentPlayer));
        return true;
    }

    /**
     * <p>Trades in {@link #FOUR_TO_ONE_TRADE_OFFER} resource cards of the
     * offered type for {@link #FOUR_TO_ONE_TRADE_WANT} resource cards of the wanted type.
     * </p><p>
     * The trade only works when bank and player possess the resource cards
     * for the trade before the trade is executed.
     * </p>
     *
     * @param offer specifies the offered type.
     * @param want specifies the wanted type.
     * @return true, if the trade was successful, false otherwise.
     * @throws IllegalArgumentException if the offer or want parameter is null.
     */
    public boolean tradeWithBankFourToOne(Resource offer, Resource want) {
        if(offer == null || want == null){
            throw new IllegalArgumentException("Offer and want parameter must not be null!");
        }
        if (currentPlayer.getResource(offer) < FOUR_TO_ONE_TRADE_OFFER || bank.getResource(want) < FOUR_TO_ONE_TRADE_WANT) {
            return false;
        }
        Map<Config.Resource, Integer> offerMap = Config.createEmptyResourceMap();
        offerMap.put(offer, FOUR_TO_ONE_TRADE_OFFER);
        Map<Config.Resource, Integer> wantMap = Config.createEmptyResourceMap();
        wantMap.put(want, FOUR_TO_ONE_TRADE_WANT);

        bank.addResources(offerMap);
        bank.tryRemoveResources(wantMap);
        currentPlayer.addResources(wantMap);
        currentPlayer.tryRemoveResources(offerMap);
        return true;
    }

    /**
     * Returns the winner of the game, if any.
     *
     * @return the winner of the game or null, if there is no winner (yet)
     */
    public Faction getWinner() {
        for (Player player : players) {
            if (player.getWinPoints() >= requiredWinPoints) {
                return player.getFaction();
            }
        }
        return null;
    }

    /**
     * Generates a scoreboard containing the current win points of all players and returns it.
     *
     * @return HashMap with the player's faction as key and the win points as value.
     */
    public HashMap<Faction, Integer> getScoreboard(){
        HashMap<Faction, Integer> scores = new HashMap<>();
        for (Player player: players){
            scores.put(player.getFaction(), player.getWinPoints());
        }
        return scores;
    }


    /**
     * Places the thief on the specified field and steals a random resource card (if
     * the player has such cards) from a random player with a settlement at that
     * field (if there is a settlement) and adds it to the resource cards of the
     * current player.
     *
     * @param field the field on which to place the thief
     * @return false, if the specified field is not a field or the thief cannot be
     * placed there (e.g., on water)
     * @throws IllegalArgumentException if the field parameter is null.
     */
    public boolean placeThiefAndStealCard(Point field) {
        if(field == null) {
            throw new IllegalArgumentException("Field parameter must not be null");
        }
        boolean validField = board.setThiefPosition(field);
        if (!validField) {
            return false;
        }

        List<Settlement> cornerStructures = board.getCornersOfField(field);

        // Remove structures which are owned by the current player
        Iterator<Settlement> cornerIter = cornerStructures.iterator();
        while (cornerIter.hasNext()) {
            if (cornerIter.next().isOwnedBy(currentPlayer)) {
                cornerIter.remove();
            }
        }

        if (!cornerStructures.isEmpty()) {
            int targetStructure = new Random().nextInt(cornerStructures.size());
            Settlement targetSettlement = cornerStructures.get(targetStructure);
            Resource stolenResource = targetSettlement.getOwner().stealRandomResource();
            if (stolenResource != null) {
                currentPlayer.addResource(stolenResource);
                System.out.format("THIEF: (%s) <==stolen from== (%s) %s\n",
                        currentPlayer.getFaction(),
                        targetSettlement.getOwner().getFaction(),
                        stolenResource);
            } else {
                System.out.println("THIEF: could not steal anything");
            }
        }
        return true;
    }

    public int getPlayerAmount() {
        return players.length;
    }

    public Player getCurrentPlayer() {
        return players[currentPlayerIndex];
    }

    public int getCurrentPlayerIndex(){
        return currentPlayerIndex;
    }

    public SiedlerBoard getBoard() {
        return board;
    }

    public CardBank getBank(){
        return bank;
    }

    public int getRequiredWinPoints(){
        return requiredWinPoints;
    }

    /**
     * Converts a map with resources into a list.
     *
     * @param resourceMap specifies the map to be converted.
     * @return the generated list.
     * @throws IllegalArgumentException if the resourceMap parameter is null.
     */
    private List<Config.Resource> translateFromMapToList(Map<Config.Resource, Integer> resourceMap) {
        if (resourceMap == null){
            throw new IllegalArgumentException("ResourceMap parameter must not be null!");
        }
        List<Config.Resource> resourceList = new ArrayList<>();
        for (Config.Resource resource : resourceMap.keySet()) {
            int amount = resourceMap.get(resource);
            for (int i = 0; i < amount; i++) {
                resourceList.add(resource);
            }
        }
        return resourceList;
    }

    /**
     * Checks if the current player owns a street that borders on a corner.
     *
     * @param corner specifies the position of the corner to check.
     * @return true if there is a road, owned by the player, false otherwise.
     * @throws IllegalArgumentException if the corner parameter is null.
     */
    private boolean doesPlayerHaveRoadToCorner(Point corner) {
        if (corner == null){
            throw new IllegalArgumentException("Corner parameter must not be null!");
        }
        List<Road> edges = board.getAdjacentEdges(corner);
        boolean hasRoadToCorner = false;
        for (PlayerOwnedStructure edge : edges) {
            if (edge.isOwnedBy(currentPlayer)) {
                hasRoadToCorner = true;
                break;
            }
        }
        return hasRoadToCorner;
    }

    /**
     * Checks if a corner is in the land.
     *
     * @param corner specifies the position of the corner to check.
     * @return true if it is on land, false if it is on water or desert.
     * @throws IllegalArgumentException if the corner parameter is null.
     */
    private boolean isCornerAdjacentToLand(Point corner) {
        if (corner == null){
            throw new IllegalArgumentException("Corner parameter must not be null!");
        }
        boolean isAdjacentToLand = false;
        List<Field> fields = board.getFields(corner);
        for (Field field: fields) {
            // Resource field is null if field is water or dessert
            Config.Resource fieldResource = field.getLand().getResource();
            if (fieldResource != null) {
                isAdjacentToLand = true;
                break;
            }
        }
        return isAdjacentToLand;
    }

    /**
     * Checks whether a position for a settlement meets the requirements of the game rules.
     *
     * @param position specifies the position to check.
     * @param enableNonInitialRequirements specifies if the initial requirements have to be met.
     * @return true if the position does meet the requirements, false if not.
     * @throws IllegalArgumentException if the position parameter is null.
     */
    private boolean doesSettlementMeetRequirements(Point position, boolean enableNonInitialRequirements) {
        if (position == null){
            throw new IllegalArgumentException("Position parameter must not be null!");
        }
        try {
            PlayerOwnedStructure corner = board.getCorner(position);
            // Corner is already used
            if (corner != null) {
                return false;
            }

        } catch (IllegalArgumentException ex) {
            return false;
        }

        if (!isCornerAdjacentToLand(position)) {
            return false;
        }

        boolean hasRoadToCorner = doesPlayerHaveRoadToCorner(position);
        if (enableNonInitialRequirements && !hasRoadToCorner) {
            return false;
        }

        // All the adjacent corners cannot have a settlement/city on them
        List<Settlement> neighborCorners = board.getNeighboursOfCorner(position);
        if (neighborCorners.size() > 0) {
            return false;
        }

        boolean playerHasStructure = currentPlayer.checkIfStructureAvailable(Config.Structure.SETTLEMENT);
        if (!playerHasStructure){
            return false;
        }

        Map<Resource, Integer> requiredResources = Config.Structure.SETTLEMENT.getCostsAsMap();
        boolean playerHasResources = currentPlayer.checkIfEnoughOfAllResources(requiredResources);
        if (enableNonInitialRequirements && !playerHasResources) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether a position for a road meets the requirements of the game rules.
     *
     * @param start specifies the start position to check.
     * @param end specifies the end position to check.
     * @param enableNonInitialRequirements specifies if the initial requirements have to be met.
     * @return true if the position does meet the requirements, false if not.
     * @throws IllegalArgumentException if the start or end parameter is null.
     */
    private boolean doesRoadMeetRequirements(Point start, Point end, boolean enableNonInitialRequirements) {
        if (start == null || end == null){
            throw new IllegalArgumentException("Start and end parameter must not be null!");
        }
        if (!board.hasEdge(start, end)) {
            return false;
        }

        if (!isCornerOnWater(start) || !isCornerOnWater(end)) {
            return false;
        }

        Settlement startStructure = board.getCorner(start);
        Settlement endStructure = board.getCorner(end);
        if (!areCornersExclusivelyOwnedByCurrentPlayer(startStructure, endStructure)) {
            return false;
        }

        if (startStructure == null && endStructure == null) {
            boolean roadToStart = doesPlayerHaveRoadToCorner(start);
            boolean roadToEnd = doesPlayerHaveRoadToCorner(end);
            if (!roadToStart && !roadToEnd) {
                return false;
            }
        }

        boolean playerHasStructure = currentPlayer.checkIfStructureAvailable(Config.Structure.ROAD);
        if (!playerHasStructure){
            return false;
        }

        Map<Resource, Integer> requiredResources = Config.Structure.ROAD.getCostsAsMap();
        boolean playerHasResources = currentPlayer.checkIfEnoughOfAllResources(requiredResources);
        if (enableNonInitialRequirements && !playerHasResources) {
            return false;
        }

        return true;
    }

    private boolean areCornersExclusivelyOwnedByCurrentPlayer(Settlement firstStructure, Settlement secondStructure) {
        return (firstStructure == null || firstStructure.isOwnedBy(currentPlayer)) &&
                (secondStructure == null || secondStructure.isOwnedBy(currentPlayer));
    }

    /**
     * Checks if a corner is on water.
     *
     * @param corner specifies the corner to check.
     * @return true if the corner is exclusively surrounded by water, false otherwise.
     * @throws IllegalArgumentException if corner parameter is null.
     */
    private boolean isCornerOnWater(Point corner) {
        if (corner == null){
            throw new IllegalArgumentException("Corner parameter must not be null!");
        }

        List<Field> cornerFields = board.getFields(corner);
        for (Field field: cornerFields) {
            if (field.getLand() != Config.Land.WATER) {
                return true;
            }
        }

        return false;
    }

    /**
     * Generates as many players as specified.
     *
     * @param numberOfPlayers specifies the required number of players.
     * @return an array with the created players.
     * @throws IllegalArgumentException if numberOfPlayers is negative.
     */
    private Player[] createPlayers(int numberOfPlayers) {
        if (numberOfPlayers < 0){
            throw new IllegalArgumentException("Cant create a negative number of players!");
        }
        Player[] players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = new Player(Config.Faction.values()[i], Config.createEmptyResourceMap(), Config.getStructuresPerPlayerAtStart());
        }
        return players;
    }
}
