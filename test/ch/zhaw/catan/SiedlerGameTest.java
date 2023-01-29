package ch.zhaw.catan;

import ch.zhaw.catan.games.ThreePlayerStandard;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;


/***
 * This class Tests the constructor and the public methods (except simple getter and setter methods) of
 * the {@link SiedlerGame} class.
 *
 * @author Zwahlen Nico, Besart Morina
 * @version 09.12.2022
 */
class SiedlerGameTest {
    private final static int DEFAULT_WINPOINTS = 5;
    private final static int DEFAULT_NUMBER_OF_PLAYERS = 3;

    private final static List<Point> INVALID_CORNER_POSITIONS = List.of(
            new Point(1, 1), new Point(-1, -1), new Point(5, 1), new Point(15, 1), new Point(9, 8)
    );

    private final static List<Tuple<Point, Point>> INVALID_EDGE_START_END_POINTS = List.of(
            new Tuple(new Point(3, 1), new Point(3, 3)), new Tuple(new Point(14, 1), new Point(14, 3))
    );

    /**
     * Tests if SiedlerGame is initialized with the correct amount of winpoints, when given valid winpoint parameters.
     *
     * Equivalence class: Constructor: valid amount of winpoints entered
     * Type of Test:      Positive
     * Initial state:     None
     * Input:             Valid winpoint numbers (Config.MIN_WIN_POINTS, 10, Config.MAX_WIN_POINTS)
     * Expected Output:   The same amount of winpoints as was given to the constructor
     */
    @ParameterizedTest
    @ValueSource(ints = {Config.MIN_WIN_POINTS, 10, Config.MAX_WIN_POINTS})
    void siedlerGamerConstructorValidWinpoints(int winpoints) {
        SiedlerGame model = new SiedlerGame(winpoints, DEFAULT_NUMBER_OF_PLAYERS);
        assertEquals(winpoints, model.getRequiredWinPoints(),
                "Winpoints different from parameter in constructor");
    }

    /**
     * Tests if an IllegalArgumentException is thrown for invalid winpoint parameters.
     *
     * Equivalence class: Constructor: invalid amount of winpoints entered
     * Type of Test:      Negative
     * Initial state:     None
     * Input:             Invalid winpoint numbers ((Config.MIN_WIN_POINTS - 1), (Config.MAX_WIN_POINTS + 1))
     * Expected Output:   The same amount of winpoints as given to the constructor
     */
    @ParameterizedTest
    @ValueSource(ints = {(Config.MIN_WIN_POINTS - 1), 0, (Config.MAX_WIN_POINTS + 1)})
    void siedlerGamerConstructorInvalidWinpoints(int winpoints) {
        assertThrows(IllegalArgumentException.class, () -> new SiedlerGame(winpoints, DEFAULT_NUMBER_OF_PLAYERS));
    }

    /**
     * Tests if SiedlerGame is initialized with the correct amount of players when given valid number of players parameter.
     *
     * Equivalence class: Constructor: valid amount of players entered
     * Type of Test:      Positive
     * Initial state:     None
     * Input:             Valid player numbers (Config.MIN_NUMBER_OF_PLAYERS, 3, Config.MAX_NUMBER_OF_PLAYERS)
     * Expected Output:   The same amount of players as given to the constructor
     */
    @ParameterizedTest
    @ValueSource(ints = {Config.MIN_NUMBER_OF_PLAYERS, 3, Config.MAX_NUMBER_OF_PLAYERS})
    void siedlerGamerConstructorValidPlayernumber(int playernumber) {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, playernumber);
        assertEquals(playernumber, model.getPlayerAmount(),
                "Amount of players different from parameter in constructor");
    }

    /**
     * Tests if a IllegalArgumentException is thrown for invalid number of players parameters.
     *
     * Equivalence class: Constructor: invalid amount of players entered
     * Type of Test:      Negative
     * Initial state:     None
     * Input:             Invalid player numbers ((Config.MIN_NUMBER_OF_PLAYERS - 1), (Config.MAX_NUMBER_OF_PLAYERS + 1))
     * Expected Output:   The same amount of players as given to the constructor
     */
    @ParameterizedTest
    @ValueSource(ints = {(Config.MIN_NUMBER_OF_PLAYERS - 1), (Config.MAX_NUMBER_OF_PLAYERS + 1)})
    void siedlerGamerConstructorInvalidPlayernumber(int playerNumber) {
        assertThrows(IllegalArgumentException.class, () -> new SiedlerGame(DEFAULT_WINPOINTS, playerNumber));
    }

    /**
     * Tests if getPlayerAmount returns the correct amount of Players for all allowed player numbers (2,3,4).
     *
     * Equivalence class: getPlayerAmount
     * Type of Test:      Positive
     * Initial state:     SiedlerGame with 2/3/4 players
     * Input:             Amount of players
     * Expected Output:   The same amount of players as was entered
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void getPlayerAmount(int numberOfPlayers) {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, numberOfPlayers);
        assertEquals(numberOfPlayers, model.getPlayerAmount(),
                "Wrong number of players returned by getPlayerAmount");
    }

    /**
     * Tests if switching to next player works.
     *
     * Equivalence class: switchToNextPlayer
     * Type of Test:      Positive
     * Initial state:     SiedlerGame with default amount of players
     * Input:             None
     * Expected Output:   That the generated player indexes match the expected index
     */
    @Test
    void switchToNextPlayer() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        for (int i = 0; i < DEFAULT_NUMBER_OF_PLAYERS; i++) {
            assertEquals(i, model.getCurrentPlayerIndex(),
                    "Switch to next player doesn't work");
            model.switchToNextPlayer();
        }
        assertEquals(0, model.getCurrentPlayerIndex(),
                "After last player doesn't switch to first player");
    }

    /**
     * Tests if switching to previous player works.
     *
     * Equivalence class: switchToPreviousPlayer
     * Type of Test:      Positive
     * Initial state:     SiedlerGame with default amount of players
     * Input:             None
     * Expected Output:   That the generated player indexes match the expected index
     */
    @Test
    void switchToPreviousPlayer() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        model.switchToPreviousPlayer();
        for (int i = DEFAULT_NUMBER_OF_PLAYERS - 1; i >= 0; i--) {
            assertEquals(i, model.getCurrentPlayerIndex(),
                    "Switch to previous player doesn't work");
            model.switchToPreviousPlayer();
        }
        assertEquals(DEFAULT_NUMBER_OF_PLAYERS - 1, model.getCurrentPlayerIndex(),
                "After first player doesn't switch to last player");

    }

    /**
     * Tests if getPlayerFactions works and gives out the factions in the correct order.
     *
     * Equivalence class: getPlayerFactions
     * Type of Test:      Positive
     * Initial state:     SiedlerGame with max number of players
     * Input:             None
     * Expected Output:   That the generated player factions match the factions from Config
     */
    @Test
    void getPlayerFactions() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, Config.MAX_NUMBER_OF_PLAYERS);
        for (int i = 0; i < Config.Faction.values().length; i++) {
            assertEquals(Config.Faction.values()[i], model.getPlayerFactions().get(i),
                    "Player factions don't match or have different order from Config.Factions");
        }
    }

    /**
     * Tests if switching to next player works.
     *
     * Equivalence class: getCurrentPlayerFaction
     * Type of Test:      Positive
     * Initial state:     SiedlerGame with max number of players
     * Input:             None
     * Expected Output:   That the factions given by getCurrentPlayerFaction match the expected factions
     */
    @Test
    void getCurrentPlayerFaction() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, Config.MAX_NUMBER_OF_PLAYERS);
        for (int i = 0; i < Config.MAX_NUMBER_OF_PLAYERS; i++) {
            assertEquals(Config.Faction.values()[i], model.getCurrentPlayerFaction(),
                    "getCurrentPlayerFaction doesn't work or the players don't have the same factions as in Config");
            model.switchToNextPlayer();
        }
    }

    /**
     * Tests if getCurrentPlayerResourceStock returns the expected Resources for all resources of all players.
     *
     * Equivalence class: getCurrentPlayerResourceStock for valid parameters
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhase()
     * Input:             All existing resources
     * Expected Output:   That the resources given by getCurrentPlayerStock match with
     *                    ThreePlayerStandard.INITIAL_PLAYER_CARD_STOCK
     */
    @Test
    void getCurrentPlayerResourceStockValidParamter() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        for (int i = 0; i < model.getPlayerAmount(); i++) {
            Map<Config.Resource, Integer> expectedResources = ThreePlayerStandard.INITIAL_PLAYER_CARD_STOCK.get(Config.Faction.values()[i]);
            for (Config.Resource resource : Config.Resource.values()) {
                assertEquals(expectedResources.get(resource), model.getCurrentPlayerResourceStock(resource),
                        "Wrong result for player" + i);
            }
            model.switchToNextPlayer();
        }
    }

    /**
     * Tests if getCurrentPlayerResourceStock throws IllegalArgumentException if given null as parameter.
     *
     * Equivalence class: getCurrentPlayerResourceStock for invalid parameter
     * Type of Test:      Negative
     * Initial state:     new SiedlerGame
     * Input:             None
     * Expected Output:   That a IllegalArgumentException is thrown.
     */
    @Test
    void getCurrentPlayerResourceStockInvalidParameter() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        assertThrows(IllegalArgumentException.class, () -> model.getCurrentPlayerResourceStock(null));
    }

    /**
     * This method tests wether the winpoints and the settlement stock of the players match their expected values, after
     * placing settlements.
     *
     * @param model the SiedlerGame instance that is tested
     * @param numberOfPlacedSettlements the number of settlements that should have been placed in the test
     * @param winpointsBefore the number of winpoints the player had before building the settlements
     * @param settlementsBefore the number of settlements the player had in stock before building the settlements
     * @return true if the winpoints of the model match the expected amount of winpoints and the players stock of
     *         settlements matches the expected amount of settlements left
     */
    private boolean testWinpointsAndSettlementStockOfPlayer(SiedlerGame model, int numberOfPlacedSettlements,
                                                            int winpointsBefore, int settlementsBefore) {
        Config.Structure structure = Config.Structure.SETTLEMENT;
        int stockExpected = settlementsBefore - numberOfPlacedSettlements;
        int stockActual = model.getCurrentPlayer().getStructureStock(structure);
        if (stockExpected == stockActual && winpointsBefore + numberOfPlacedSettlements == model.getCurrentPlayer().getWinPoints()) {
            return true;
        }
        return false;
    }

    /**
     * Tests if placeInitialSettlement works correctly for the first settlement of the first player.
     *
     * Equivalence class: Place first initial settlement
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame
     * Input:             First settlement position from first player of ThreePayerStandard
     * Expected Output:   That the settlement is placed in the expected position
     */
    @Test
    void placeFirstInitialSettlement() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        int winpointsBefore = model.getCurrentPlayer().getWinPoints();
        int settlementsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);

        Tuple<Point, Point> positions = ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(Config.Faction.values()[0]);
        Point position = positions.first;

        model.placeInitialSettlement(position, false);
        SiedlerBoard modelBoard = model.getBoard();

        assertEquals(Config.Faction.values()[0], modelBoard.getCorner(position).getOwner().getFaction(),
                "First initial settlement not placed correctly");

        assertTrue(testWinpointsAndSettlementStockOfPlayer(model, 1, winpointsBefore, settlementsBefore),
                "Either incorrect number of winpoints or number of structures left");
    }

    /**
     * Tests if placeInitialSettlement works correctly for the second settlement of the first player.
     *
     * Equivalence class: Place second initial settlement
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame
     * Input:             Second settlement position from first player of ThreePayerStandard
     * Expected Output:   That the settlement is placed in the expected position
     */
    @Test
    void placeSecondInitialSettlement() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        int winpointsBefore = model.getCurrentPlayer().getWinPoints();
        int settlementsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);

        Tuple<Point, Point> positions = ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(Config.Faction.values()[0]);
        Point position = positions.second;

        model.placeInitialSettlement(position, true);
        SiedlerBoard modelBoard = model.getBoard();

        assertEquals(Config.Faction.values()[0], modelBoard.getCorner(position).getOwner().getFaction(),
                "Second initial settlement not placed correctly");

        Map<Config.Resource, Integer> modelResources = model.getCurrentPlayer().getResources();
        Map<Config.Resource, Integer> expectedResources = ThreePlayerStandard.INITIAL_PLAYER_CARD_STOCK.get(Config.Faction.values()[0]);
        assertEquals(expectedResources, modelResources, "Incorrect payout of resources");

        assertTrue(testWinpointsAndSettlementStockOfPlayer(model, 1, winpointsBefore, settlementsBefore),
                "Either incorrect number of winpoints or number of structures left");
    }

    /**
     * Tests if the resources given to the players through placeInitialSettlement are also taken away from the bank.
     *
     * Equivalence class: PlaceInitialSettlement check Bank
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhase
     * Input:             None
     * Expected Output:   That the Bank has the expected amount of resources left after the setup phase
     */
    @Test
    void placeInitialSettlementCheckBank() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        int woolInBank = model.getBank().getResources().get(Config.Resource.WOOL);
        int woolAvailable = ThreePlayerStandard.RESOURCE_CARDS_IN_BANK_AFTER_STARTUP_PHASE.get(Config.Resource.WOOL);
        assertEquals(woolAvailable, woolInBank);

    }

    /**
     * Tests if placeInitialSettlement returns false for invalid position parameters.
     *
     * Equivalence class: Place initial settlement with invalid position
     * Type of Test:      Negative
     * Initial state:     new SiedlerGame
     * Input:             Invalid positions from Config.INVALID_CORNER_POSITIONS
     * Expected Output:   That the method returns false
     */
    @Test
    void placeInitialSettlementInvalidPosition() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        int winpointsBefore = model.getCurrentPlayer().getWinPoints();
        int settlementsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);
        for (Point position : INVALID_CORNER_POSITIONS) {
            assertFalse(model.placeInitialSettlement(position, false),
                    "True has been returned for invalid position");
        }
        assertTrue(testWinpointsAndSettlementStockOfPlayer(model, 0, winpointsBefore, settlementsBefore),
                "Either incorrect number of winpoints or number of structures left");
    }

    /**
     * Tests if placeInitialSettlement returns false if position is to close to other settlement.
     *
     * Equivalence class: Place initial settlement to close to other settlement
     * Type of Test:      Negative
     * Initial state:     new SiedlerGame
     * Input:             Positions that is to close to other settlement
     * Expected Output:   That the method returns false
     */
    @Test
    void placeInitialSettlementToCloseToOtherSettlement() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        int settlementsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);
        int winpointsBefore = model.getCurrentPlayer().getWinPoints();
        model.placeInitialSettlement(new Point(4, 4), false);

        assertFalse(model.placeInitialSettlement(new Point(4, 6), false),
                "True has been returned even though another settlement is to close");
        assertTrue(testWinpointsAndSettlementStockOfPlayer(model, 1, winpointsBefore, settlementsBefore),
                "Either incorrect number of winpoints or number of structures left");
    }

    /**
     *
     * Equivalence class: Place initial settlement on top of another settlement
     * Type of Test:      Negative
     * Initial state:     new SiedlerGame
     * Input:             Positions that already taken by another settlement
     * Expected Output:   That the method returns false
     */
    @Test
    void placeInitialSettlementAtopOtherSettlement() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        int settlementsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);
        int winpointsBefore = model.getCurrentPlayer().getWinPoints();
        model.placeInitialSettlement(new Point(4, 4), false);

        assertFalse(model.placeInitialSettlement(new Point(4, 4), false),
                "True has been returned even though another settlement is already in this position");
        assertTrue(testWinpointsAndSettlementStockOfPlayer(model, 1, winpointsBefore, settlementsBefore),
                "Either incorrect number of winpoints or number of structures left");
    }

    /**
     * Tests if placeInitialSettlement throws IllegalArgumentException for null as parameter.
     *
     * Equivalence class: Place initial settlement with null as position.
     * Type of Test:      Negative
     * Initial state:     new SiedlerGame
     * Input:             Null
     * Expected Output:   That the method throws IllegalArgumentException
     */
    @Test
    void placeInitialSettlementParameterNull() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        assertThrows(IllegalArgumentException.class, () -> model.placeInitialSettlement(null, false),
                "PlaceInitialSettlement didn't throw exception for position == null");
    }

    /**
     * This method tests wether the winpoints and the settlement stock of the players match their expected values, after
     * placing settlements.
     *
     * @param model the SiedlerGame instance that is tested
     * @param numberOfPlacedRoads the number of roads that should have been placed in the test
     * @param roadsBefore the number of roads the player had in stock before building them
     * @return true if the actual number of roads in stock matches the expected one
     */
    private boolean testRoadStockOfPlayer(SiedlerGame model, int numberOfPlacedRoads, int roadsBefore) {
        Config.Structure structure = Config.Structure.ROAD;
        int stockExpected = roadsBefore - numberOfPlacedRoads;
        int stockActual = model.getCurrentPlayer().getStructureStock(structure);
        if (stockExpected == stockActual) {
            return true;
        }
        return false;
    }

    /**
     * Tests if placeInitialRoad works correctly for the first player and if the order of start and end points matter
     * for the road placement.
     *
     * Equivalence class: Place first initial road with settlement
     * Type of Test:      Positive
     * Initial state:     New SiedlerGame
     * Input:             First settlement and road positions of first player from ThreePlayerStandard
     * Expected Output:   That the road is placed in the expected position
     */
    @Test
    void placeInitialRoadValidPositionsWithSettlement() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        int roadsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.ROAD);
        Tuple<Point, Point> settlements = ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(Config.Faction.values()[0]);
        Point startPointA = settlements.first;
        Point startPointB = settlements.second;
        Tuple<Point, Point> positions = ThreePlayerStandard.INITIAL_ROAD_ENDPOINTS.get(Config.Faction.values()[0]);
        Point endPointA = positions.first;
        Point endPointB = positions.second;

        model.placeInitialSettlement(startPointA, false);
        model.placeInitialSettlement(startPointB, false);
        assertTrue(model.placeInitialRoad(startPointA, endPointA),
                "Road wasn't placed even though it should");
        assertTrue(model.placeInitialRoad(startPointB, endPointB),
                "Road wasn't placed because different order of start and endpoint");
        SiedlerBoard modelBoard = model.getBoard();
        Config.Faction modelFaction = modelBoard.getEdge(startPointA, endPointA).getOwner().getFaction();

        assertEquals(Config.Faction.values()[0], modelFaction,
                "First initial road not placed correctly");
        assertEquals(Config.Faction.values()[0], modelBoard.getEdge(endPointA, startPointA).getOwner().getFaction(),
                "Order of start and end points did matter");
        assertTrue(testRoadStockOfPlayer(model, 2, roadsBefore),
                "Player doesn't have the correct amount of roads left");
    }

    /**
     * Tests if placeInitialRoad doesn't build the road when there is no settlement connected
     *
     * Equivalence class: Place first initial road without settlement connected
     * Type of Test:      Positive
     * Initial state:     New SiedlerGame
     * Input:             First road positions of first player from ThreePlayerStandard
     * Expected Output:   That the road isn't placed
     */
    @Test
    void placeInitialRoadValidPositionsNoSettlements() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        int roadsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.ROAD);
        Tuple<Point, Point> settlements = ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(Config.Faction.values()[0]);
        Point startPoint = settlements.first;
        Tuple<Point, Point> positions = ThreePlayerStandard.INITIAL_ROAD_ENDPOINTS.get(Config.Faction.values()[0]);
        Point endPoint = positions.first;

        assertFalse(model.placeInitialRoad(startPoint, endPoint));
        assertTrue(testRoadStockOfPlayer(model, 0, roadsBefore),
                "Player doesn't have the correct amount of roads left");
    }

    /**
     * Tests if placeInitialRoad returns false for invalid start and endpoint parameters
     *
     * Equivalence class: Place initial road invalid parameters
     * Type of Test:      Negative
     * Initial state:     New SiedlerGame
     * Input:             Invalid start and end points as parameters (Config.INVALID_EDGE_START_END_POINTS)
     * Expected Output:   That the method returns false
     */
    @Test
    void placeInitialRoadInvalidPositions() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        int roadsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.ROAD);
        for (Tuple<Point, Point> points : INVALID_EDGE_START_END_POINTS) {
            assertFalse(model.placeInitialRoad(points.first, points.second),
                    "PlaceInitialRoad didn't return false for invalid road placement");
        }
        assertTrue(testRoadStockOfPlayer(model, 0, roadsBefore),
                "Player doesn't have the correct amount of roads left");
    }

    /**
     * Tests if placeInitialRoad returns false for invalid start and endpoint parameters
     *
     * Equivalence class: Place initial road into water with a settlement at start
     * Type of Test:      Negative
     * Initial state:     New SiedlerGame
     * Input:             Startpoint at settlement on shore and endpoint in water
     * Expected Output:   That the method returns false
     */
    @Test
    void placeInitialRoadIntoWater() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        int roadsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.ROAD);
        //Endpoint in water with settlement at start
        model.placeInitialSettlement(new Point(4,4), false);
        assertFalse(model.placeInitialRoad(new Point(4,4), new Point(3,3)),
                "PlaceInitialRoad didn't return false for endpoint in water");
        //Endpoint in water with road at start
        model.placeInitialRoad(new Point(4,4), new Point(5,3));
        assertFalse(model.placeInitialRoad(new Point(5,3), new Point(5,1)),
                "PlaceInitialRoad didn't return false for for endpoint in water");
        assertTrue(testRoadStockOfPlayer(model, 1, roadsBefore),
                "Player doesn't have the correct amount of roads left");
    }

    /**
     * Tests if placeInitialRoad throws IllegalArgumentException for null as parameter.
     *
     * Equivalence class: Place initial road with null as start and end point.
     * Type of Test:      Negative
     * Initial state:     new SiedlerGame
     * Input:             Null
     * Expected Output:   That the method throws IllegalArgumentException
     */
    @Test
    void placeInitialRoadParameterNull() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        assertThrows(IllegalArgumentException.class, () -> model.placeInitialRoad(null, new Point(4, 4)),
                "PlaceInitialRoad didn't throw exception for roadStart == null");
        assertThrows(IllegalArgumentException.class, () -> model.placeInitialRoad(new Point(10, 4), null),
                "PlaceInitialRoad didn't throw exception for roadEnd == null");
        assertThrows(IllegalArgumentException.class, () -> model.placeInitialRoad(null, null),
                "PlaceInitialRoad didn't throw exception for roadStart & roadEnd == null");
    }

    /**
     * Tests if throwDice works correctly for all valid numbers except seven.
     *
     * Equivalence class: ThrowDice valid numbers
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhase
     * Input:             Valid dice numbers except 7 (2, 3, 4, 5, 6, 8, 9, 10, 11, 12)
     * Expected Output:   That the generated resource map matches the one from ThreePlayerStandard
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 8, 9, 10, 11, 12})
    void throwDiceAllNumbersExceptSeven(int diceValue) {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        Map<Config.Faction, List<Config.Resource>> expected = ThreePlayerStandard.INITIAL_DICE_THROW_PAYOUT.get(diceValue);
        Map<Config.Faction, List<Config.Resource>> actual = model.throwDice(diceValue);
        assertEquals(expected, actual);
    }

    /**
     * Tests if throwDice works correctly for all valid numbers except seven.
     *
     * Equivalence class: ThrowDice valid numbers
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhase
     * Input:             Valid dice numbers except 7 (2, 3, 4, 5, 6, 8, 9, 10, 11, 12)
     * Expected Output:   That the generated resource map matches the one from ThreePlayerStandard
     */
    @Test
    void throwDiceThiefOnField() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        //Position of a pasture field with dice number 12 with a settlement of faction rr:
        Point position = new Point(10, 14);
        model.placeThiefAndStealCard(position);
        Map<Config.Resource, Integer> expected = new HashMap<>(model.getCurrentPlayer().getResources());
        model.throwDice(12);
        assertEquals(expected, model.getCurrentPlayer().getResources(),
                "The player received the wool, even though the thief was on that field");
    }

    /**
     * Tests if throwDice works correctly for the number seven if the player has a total of 8 resources
     *
     * Equivalence class: ThrowDice number 7, even amount of resources over 7
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame with 8 resources added to the first player
     * Input:             Number seven
     * Expected Output:   That the player owned resource map now only has 4 resources remaining
     */
    @Test
    void throwDiceNumberSevenEvenResourcesOver() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        Map<Config.Resource, Integer> resourcesBefore = Map.of(Config.Resource.GRAIN, 2, Config.Resource.WOOL, 2,
                Config.Resource.BRICK, 2, Config.Resource.ORE, 1, Config.Resource.LUMBER, 1);
        int numberOfResources = 8;
        int expectedToRemain = 4;
        int expectedInBank = numberOfResources - expectedToRemain;

        model.getCurrentPlayer().addResources(resourcesBefore);
        model.throwDice(7);
        assertEquals(expectedToRemain, remainingWithPlayer(model), "Player didn't drop the right amount of resources");
        assertEquals(expectedInBank, addedToBank(model), "Bank didn't receive the right amount of resources");
    }

    /**
     * Tests if throwDice works correctly for the number seven if the player has a total of 9 resources
     *
     * Equivalence class: ThrowDice number 7, uneven amount of resources over 7
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame with 9 resources added to the first player
     * Input:             Number seven
     * Expected Output:   That the player owned resource map now only has 5 resources remaining
     */
    @Test
    void throwDiceNumberSevenUnevenResourcesOver() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        Map<Config.Resource, Integer> resourcesBefore = Map.of(Config.Resource.GRAIN, 2, Config.Resource.WOOL, 2,
                Config.Resource.BRICK, 2, Config.Resource.ORE, 2, Config.Resource.LUMBER, 1);
        int numberOfResources = 9;
        int expectedToRemain = 5;
        int expectedInBank = numberOfResources - expectedToRemain;

        model.getCurrentPlayer().addResources(resourcesBefore);
        model.throwDice(7);
        assertEquals(expectedToRemain, remainingWithPlayer(model), "Player didn't drop the right amount of resources");
        assertEquals(expectedInBank, addedToBank(model), "Bank didn't receive the right amount of resources");
    }

    /**
     * Tests if throwDice works correctly for the number seven if the player has a total of 7 resources
     *
     * Equivalence class: ThrowDice number 7, player has exactly 7 resources
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame with 7 resources added to the first player
     * Input:             Number seven
     * Expected Output:   That the player owned resource map still has 7 resources remaining
     */
    @Test
    void throwDiceNumberSevenSevenResources() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        Map<Config.Resource, Integer> resourcesBefore = Map.of(Config.Resource.GRAIN, 2, Config.Resource.WOOL, 2,
                Config.Resource.BRICK, 1, Config.Resource.ORE, 1, Config.Resource.LUMBER, 1);
        int numberOfResources = 7;
        int expectedToRemain = 7;
        int expectedInBank = numberOfResources - expectedToRemain;

        model.getCurrentPlayer().addResources(resourcesBefore);
        model.throwDice(7);
        assertEquals(expectedToRemain, remainingWithPlayer(model), "Player didn't drop the right amount of resources");
        assertEquals(expectedInBank, addedToBank(model), "Bank didn't receive the right amount of resources");
    }

    /**
     * Tests if throwDice works correctly for the number seven if the player has a total of 6 resources
     *
     * Equivalence class: ThrowDice number 7, player has less than 7 resources
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame with 6 resources added to the first player
     * Input:             Number seven
     * Expected Output:   That the player owned resource map still has 6 resources remaining
     */
    @Test
    void throwDiceNumberSevenResourcesUnder() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        Map<Config.Resource, Integer> resourcesBefore = Map.of(Config.Resource.GRAIN, 2, Config.Resource.WOOL, 1,
                Config.Resource.BRICK, 1, Config.Resource.ORE, 1, Config.Resource.LUMBER, 1);
        int numberOfResources = 6;
        int expectedToRemain = 6;
        int expectedInBank = numberOfResources - expectedToRemain;

        model.getCurrentPlayer().addResources(resourcesBefore);
        model.throwDice(7);
        assertEquals(expectedToRemain, remainingWithPlayer(model), "Player didn't drop the right amount of resources");
        assertEquals(expectedInBank, addedToBank(model), "Bank didn't receive the right amount of resources");
    }

    /**
     * Tests if throwDice works correctly for two situations:
     * 1) When bank has to pay out two gravel to one faction but only has one gravel.
     * 2) When bank has to pay out one lumber each to two different factions but only has one lumber.
     *
     * Equivalence class: ThrowDice not enough resources in bank
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame with bank only having one grain and lumber.
     * Input:             None
     * Expected Output:   1) That the faction gets the one gravel from the bank.
     *                    2) That neither faction gets the lumber since there isn't enough of it.
     */
    @Test
    void throwDiceBankNotEnoughResourcesTwoSettlements() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);

        //Empties bank and fills it with 1 grain and lumber each.
        prepareBankTwoResources(model, 1);

        //Places settlements around a forest and a field.
        placeTwoSettlementsAroundTwoFields(model);

        Map<Config.Faction, List<Config.Resource>> payout = model.throwDice(10);

        assertEquals(1, model.getCurrentPlayerResourceStock(Config.Resource.GRAIN),
                "Player1 didn't receive the grain he was supposed to.");
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.LUMBER),
                "Player1 received lumber he wasn't supposed to.");
        assertTrue(payout.get(Config.Faction.values()[0]).size() == 1,
                "Wrong Payout was returned");

        model.switchToNextPlayer();
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.LUMBER),
                "Player2 received lumber he wasn't supposed to.");
        assertTrue(payout.get(Config.Faction.values()[1]).isEmpty(),
                "Wrong Payout was returned");

        Map<Config.Resource, Integer> expected = Config.createEmptyResourceMap();
        expected.put(Config.Resource.LUMBER, 1);
        assertEquals(expected, model.getBank().getResources(),
                "The bank doesn't have the expected amount of resources left");
    }

    /**
     * This method places two settlements of faction rr around a fields field and one settlements of faction rr as well
     * as one settlement of faction bb around a forest field
     *
     * @param model the instance of SiedlerGame that is being tested
     */
    private void placeTwoSettlementsAroundTwoFields(SiedlerGame model) {
        //Places two settlements of faction rr around a fields field with dice number 10.
        Point grain1 = new Point(3, 13);
        Point grain2 = new Point(4, 16);
        model.placeInitialSettlement(grain1, false);
        model.placeInitialSettlement(grain2, false);

        //Places one settlement of faction rr and one settlement of faction bb around a forest field with dice number 10.
        Point lumber1 = new Point(10, 6);
        Point lumber2 = new Point(11, 9);
        model.placeInitialSettlement(lumber1, false);
        model.switchToNextPlayer();
        model.placeInitialSettlement(lumber2, false);

        model.switchToPreviousPlayer();
    }

    /**
     * This method prepares the bank to contain nothing but the set amount of grain and lumber
     *
     * @param model the instance of SiedlerGame that is tested
     * @param amount the amount of grain and lumber to be put in the bank
     */
    private void prepareBankTwoResources(SiedlerGame model, int amount) {
        model.getBank().tryRemoveResources(Config.INITIAL_RESOURCE_CARDS_BANK);
        Map<Config.Resource, Integer> resources = Config.createEmptyResourceMap();
        resources.put(Config.Resource.GRAIN, amount);
        resources.put(Config.Resource.LUMBER, amount);
        model.getBank().addResources(resources);
    }

    /**
     * Tests if throwDice works correctly for three situations:
     * 1) When bank has to pay out three gravel to one faction.
     * 2) When bank has to pay out two lumber to one faction and one lumber to another faction.
     * 3) When bank has to pay out one wool each to three factions.
     *
     * Equivalence class: ThrowDice not enough resources in bank
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame with bank only having 1/ 2 grain, lumber and wool.
     * Input:             None
     * Expected Output:   1) That the faction gets the all the gravel the bank has.
     *                    2) That neither faction gets the lumber since there isn't enough of it.
     *                    3) That no faction gets the wool since there isn't enough of it.
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void throwDiceBankNotEnoughResourcesThreeSettlementsNotEnoughResourceInBank(int amount) {
        SiedlerGame model = new SiedlerGame(Config.MAX_WIN_POINTS, 4);

        //Empties bank and fills it with "amount" grain, wool and lumber each.
        prepareBankThreeResources(model, amount);

        //Places settlements around a pasture, a forest and a field.
        placeThreeSettlementsAroundThreeFields(model);

        Map<Config.Faction, List<Config.Resource>> payoutA = model.throwDice(10);
        Map<Config.Faction, List<Config.Resource>> payoutB = model.throwDice(3);

        assertTrue(payoutA.get(Config.Faction.values()[0]).size() == amount,
                "Wrong Payout was returned");
        assertTrue(payoutB.get(Config.Faction.values()[0]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutA.get(Config.Faction.values()[1]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutB.get(Config.Faction.values()[1]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutA.get(Config.Faction.values()[2]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutB.get(Config.Faction.values()[2]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutA.get(Config.Faction.values()[3]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutB.get(Config.Faction.values()[3]).size() == 0,
                "Wrong Payout was returned");

        assertEquals(amount, model.getCurrentPlayerResourceStock(Config.Resource.GRAIN),
                "Player1 didn't receive the grain he was supposed to.");
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.LUMBER),
                "Player1 received lumber he wasn't supposed to.");
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player1 received wool he wasn't supposed to.");
        model.switchToNextPlayer();
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.LUMBER),
                "Player2 received lumber he wasn't supposed to.");
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player2 received wool he wasn't supposed to.");
        model.switchToNextPlayer();
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player3 received wool he wasn't supposed to.");
        model.switchToNextPlayer();
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player4 received wool he wasn't supposed to.");

        Map<Config.Resource, Integer> expected = Config.createEmptyResourceMap();
        expected.put(Config.Resource.LUMBER, amount);
        expected.put(Config.Resource.WOOL, amount);
        assertEquals(expected, model.getBank().getResources(),
                "The bank doesn't have the expected amount of resources left");
    }

    /**
     * Tests if throwDice works correctly for three situations:
     * 1) When bank has to pay out three gravel to one faction but only has two gravel.
     * 2) When bank has to pay out two lumber to one faction and one lumber to another faction but only has two lumber.
     * 3) When bank has to pay out one wool each to three faction but only has two wool.
     *
     * Equivalence class: ThrowDice not enough resources in bank
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame with bank only having three grain, lumber and wool.
     * Input:             None
     * Expected Output:   1) That the faction gets the one gravel from the bank.
     *                    2) That neither faction gets the lumber since there isn't enough of it.
     *                    3) That no faction gets the wool since there isn't enough of it.
     */
    @Test
    void throwDiceBankNotEnoughResourcesTwoSettlementsOneCityThreeResourceInBank() {
        SiedlerGame model = new SiedlerGame(Config.MAX_WIN_POINTS, 4);

        //Empties bank and fills it with 3 grain, 3 wool and 3 lumber.
        prepareBankThreeResources(model, 3);

        //Places settlements around a pasture, a forest and a field.
        placeThreeSettlementsAroundThreeFields(model);

        //Upgrades one of the settlements around each field to a city.
        upgradeOneSettlementEachToCity(model);

        Map<Config.Faction, List<Config.Resource>> payoutA = model.throwDice(10);
        Map<Config.Faction, List<Config.Resource>> payoutB = model.throwDice(3);

        assertTrue(payoutA.get(Config.Faction.values()[0]).size() == 3,
                "Wrong Payout was returned");
        assertTrue(payoutB.get(Config.Faction.values()[0]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutA.get(Config.Faction.values()[1]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutB.get(Config.Faction.values()[1]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutA.get(Config.Faction.values()[2]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutB.get(Config.Faction.values()[2]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutA.get(Config.Faction.values()[3]).size() == 0,
                "Wrong Payout was returned");
        assertTrue(payoutB.get(Config.Faction.values()[3]).size() == 0,
                "Wrong Payout was returned");

        assertEquals(3, model.getCurrentPlayerResourceStock(Config.Resource.GRAIN),
                "Player1 didn't receive the grain he was supposed to.");
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.LUMBER),
                "Player1 received lumber he wasn't supposed to.");
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player1 received wool he wasn't supposed to.");
        model.switchToNextPlayer();
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.LUMBER),
                "Player2 received lumber he wasn't supposed to.");
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player2 received wool he wasn't supposed to.");
        model.switchToNextPlayer();
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player3 received wool he wasn't supposed to.");
        model.switchToNextPlayer();
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player4 received wool he wasn't supposed to.");

        Map<Config.Resource, Integer> expected = Config.createEmptyResourceMap();
        expected.put(Config.Resource.LUMBER, 3);
        expected.put(Config.Resource.WOOL, 3);
        assertEquals(expected, model.getBank().getResources(),
                "The bank doesn't have the expected amount of resources left");
    }

    /**
     * Tests if throwDice works correctly for a mix of settlements and cities when bank has enough resources to pay.
     *
     * Equivalence class: ThrowDice enough resources in bank
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame with bank only having four grain, lumber and wool.
     * Input:             None
     * Expected Output:   That the factions get all resources according to the settlements/ cities they have.
     */
    @Test
    void throwDiceBankEnoughResourcesTwoSettlementsOneCity() {
        SiedlerGame model = new SiedlerGame(Config.MAX_WIN_POINTS, 4);

        //Empties bank and fills it with 4 grain, wool and lumber each.
        prepareBankThreeResources(model, 4);

        //Places settlements around a pasture, a forest and a field.
        placeThreeSettlementsAroundThreeFields(model);

        //Upgrades one of the settlements around each field to a city.
        upgradeOneSettlementEachToCity(model);

        model.throwDice(10);
        model.throwDice(3);

        assertEquals(4, model.getCurrentPlayerResourceStock(Config.Resource.GRAIN),
                "Player1 didn't receive the grain he was supposed to.");
        assertEquals(2, model.getCurrentPlayerResourceStock(Config.Resource.LUMBER),
                "Player1 received lumber he wasn't supposed to.");
        assertEquals(0, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player1 received wool he wasn't supposed to.");
        model.switchToNextPlayer();
        assertEquals(2, model.getCurrentPlayerResourceStock(Config.Resource.LUMBER),
                "Player2 received lumber he wasn't supposed to.");
        assertEquals(1, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player2 received wool he wasn't supposed to.");
        model.switchToNextPlayer();
        assertEquals(2, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player3 received wool he wasn't supposed to.");
        model.switchToNextPlayer();
        assertEquals(1, model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player4 received wool he wasn't supposed to.");

        Map<Config.Resource, Integer> expected = Config.createEmptyResourceMap();
        assertEquals(expected, model.getBank().getResources(),
                "The bank doesn't have the expected amount of resources left");
    }

    /**
     * This method places 3 settlements of faction rr around a fields field, 2 settlements of faction rr and one
     * settlement of faction bb around a forest and 1 settlement each of factions bb, gg and yy around a pasture.
     *
     * @param model the instance of SiedlerGame that is being tested
     */
    private void placeThreeSettlementsAroundThreeFields(SiedlerGame model) {
        //Places three settlements of faction rr around a fields field with dice number 10.
        Point grain1 = new Point(3, 13);
        Point grain2 = new Point(4, 16);
        Point grain3 = new Point(5, 13);
        model.placeInitialSettlement(grain1, false);
        model.placeInitialSettlement(grain2, false);
        model.placeInitialSettlement(grain3, false);

        //Places two settlement of faction rr and one settlement of faction bb around a forest field with dice number 10.
        Point lumber1 = new Point(10, 6);
        Point lumber2 = new Point(11, 9);
        Point lumber3 = new Point(9, 9);
        model.placeInitialSettlement(lumber1, false);
        model.placeInitialSettlement(lumber2, false);
        model.switchToNextPlayer();
        model.placeInitialSettlement(lumber3, false);


        //Places one settlement of faction bb, one of faction gg and one of faction yy around a pasture field with dice number 3.
        Point wool1 = new Point(7, 3);
        Point wool2 = new Point(8, 6);
        Point wool3 = new Point(6, 6);
        model.placeInitialSettlement(wool1, false);
        model.switchToNextPlayer();
        model.placeInitialSettlement(wool2, false);
        model.switchToNextPlayer();
        model.placeInitialSettlement(wool3, false);

        model.switchToNextPlayer();
    }

    /**
     * This method upgrades one settlement of each field (placed in method placeThreeSettlementsAroundThreeFields) to
     * a city, at no cost to the players
     *
     * @param model the instance of SiedlerGame that is being tested
     */
    private void upgradeOneSettlementEachToCity(SiedlerGame model) {
        Map<Config.Resource, Integer> costOfCity = Config.Structure.CITY.getCostsAsMap();
        model.getCurrentPlayer().addResources(costOfCity);
        model.buildCity(new Point(3, 13));
        model.switchToNextPlayer();
        model.getCurrentPlayer().addResources(costOfCity);
        model.buildCity(new Point(9, 9));
        model.switchToNextPlayer();
        model.getCurrentPlayer().addResources(costOfCity);
        model.buildCity(new Point(8, 6));
        model.switchToPreviousPlayer();
        model.switchToPreviousPlayer();

        model.getBank().tryRemoveResources(costOfCity);
        model.getBank().tryRemoveResources(costOfCity);
        model.getBank().tryRemoveResources(costOfCity);
    }

    /**
     * This method prepares the bank to contain nothing but the set amount of grain, lumber and wool
     *
     * @param model the instance of SiedlerGame that is tested
     * @param amount the amount of grain, lumber and wool to be put in the bank
     */
    private void prepareBankThreeResources(SiedlerGame model, int amount) {
        model.getBank().tryRemoveResources(Config.INITIAL_RESOURCE_CARDS_BANK);
        Map<Config.Resource, Integer> resources = Config.createEmptyResourceMap();
        resources.put(Config.Resource.GRAIN, amount);
        resources.put(Config.Resource.LUMBER, amount);
        resources.put(Config.Resource.WOOL, amount);
        model.getBank().addResources(resources);
    }

    /**
     * Tests if throwDice throws IllegalArgumentException when given invalid parameter numbers
     *
     * Equivalence class: ThrowDice invalid numbers
     * Type of Test:      Negative
     * Initial state:     new SiedlerGame
     * Input:             Invalid numbers (-1, 0, 1, 13)
     * Expected Output:   IllegalArgumentException
     */
    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1, 13})
    void throwDiceInvalidNumbers(int diceValue) {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        assertThrows(IllegalArgumentException.class, () -> model.throwDice(diceValue));
    }

    /**
     * This method gives the amount of all resources the current player has.
     *
     * @param model the instance of SiedlerGame that is being tested
     * @return the amount of all resources the current player has
     */
    private int remainingWithPlayer(SiedlerGame model) {
        int actual = 0;
        for (int value : model.getCurrentPlayer().getResources().values()) {
            actual += value;
        }
        return actual;
    }

    /**
     * This method gives the difference in the amount of resources the bank had after construction and in the current
     * state of model.
     *
     * @param model the instance of SiedlerGame that is bein tested
     * @return the difference in current resources to resources after construction
     */
    private int addedToBank(SiedlerGame model) {
        int bankBefore = 0;
        for (int value : Config.INITIAL_RESOURCE_CARDS_BANK.values()) {
            bankBefore += value;
        }
        int bankAfter = 0;
        for (int value : model.getBank().getResources().values()) {
            bankAfter += value;
        }
        return bankAfter - bankBefore;
    }

    /**
     * Tests if buildSettlement works when there is a road to the building position
     *
     * Equivalence class: BuildSettlement valid position with road to it
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank
     * Input:             Valid position with road to it
     * Expected Output:   That true is returned
     */
    @Test
    void buildSettlementValidPosition() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        int winpointsBefore = model.getCurrentPlayer().getWinPoints();
        int settlementsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);
        Map<Config.Resource, Integer> before = new HashMap<>(model.getCurrentPlayer().getResources());
        assertTrue(model.buildRoad(new Point(9, 15), new Point(9, 13)));
        assertTrue(model.buildSettlement(new Point(9, 13)),
                "Settlement wasn't placed even though there was a road connection");
        //add cost of settlement
        model.getCurrentPlayer().addResource(Config.Resource.LUMBER);
        model.getCurrentPlayer().addResource(Config.Resource.WOOL);
        model.getCurrentPlayer().addResource(Config.Resource.BRICK);
        model.getCurrentPlayer().addResource(Config.Resource.GRAIN);
        //add cost of road
        model.getCurrentPlayer().addResource(Config.Resource.LUMBER);
        model.getCurrentPlayer().addResource(Config.Resource.BRICK);
        assertEquals(before, model.getCurrentPlayer().getResources(),
                "Player didn't pay the expected amount of resources for the road and settlement");
        assertTrue(testWinpointsAndSettlementStockOfPlayer(model, 1, winpointsBefore, settlementsBefore),
                "Either incorrect number of winpoints or number of structures left");
    }

    /**
     * Tests if buildSettlement returns false when there is no road to the building position
     *
     * Equivalence class: BuildSettlement invalid position with no road to it
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank
     * Input:             Invalid position with no road to it
     * Expected Output:   That false is returned
     */
    @Test
    void buildSettlementInvalidPosition() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        int winpointsBefore = model.getCurrentPlayer().getWinPoints();
        int settlementsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);
        Map<Config.Resource, Integer> before = new HashMap<>(model.getCurrentPlayer().getResources());
        assertFalse(model.buildSettlement(new Point(9, 13)),
                "Settlement was placed even though there was no road connection");
        assertEquals(before, model.getCurrentPlayer().getResources(),
                "Player payed for a settlement that wasn't built");
        assertTrue(testWinpointsAndSettlementStockOfPlayer(model, 0, winpointsBefore, settlementsBefore),
                "Either incorrect number of winpoints or number of structures left");
    }

    /**
     * Tests if buildSettlement throws IllegalArgumentException for null as parameter.
     *
     * Equivalence class: Build settlement with null as position.
     * Type of Test:      Negative
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank
     * Input:             Null
     * Expected Output:   That the method throws IllegalArgumentException
     */
    @Test
    void buildSettlementParameterNull() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        assertThrows(IllegalArgumentException.class, () -> model.buildSettlement(null),
                "BuildSettlement didn't throw exception for position == null");
    }

    /**
     * Tests if buildSettlement returns false when the player doesn't have enough resources, if player pays resources
     * and if bank receives the resources
     *
     * Equivalence class: BuildSettlement player not enough resources
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame
     * Input:             Valid position with road to it
     * Expected Output:   That false is returned
     */
    @Test
    void buildSettlementNotEnoughResources() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        int winpointsBefore = model.getCurrentPlayer().getWinPoints();
        int settlementsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);
        Map<Config.Resource, Integer> resourcesStart = Map.of(Config.Resource.GRAIN, 1, Config.Resource.WOOL, 1,
                Config.Resource.BRICK, 1, Config.Resource.ORE, 0, Config.Resource.LUMBER, 1);
        model.getCurrentPlayer().addResources(resourcesStart);
        model.buildRoad(new Point(9, 15), new Point(9, 13));

        Map<Config.Resource, Integer> playerBefore = new HashMap<>(model.getCurrentPlayer().getResources());
        Map<Config.Resource, Integer> bankBefore = new HashMap<>(model.getBank().getResources());
        assertFalse(model.buildSettlement(new Point(9, 13)),
                "Settlement was placed even though there were not enough resources");
        assertEquals(playerBefore, model.getCurrentPlayer().getResources(),
                "Player payed for a settlement that wasn't built");
        assertEquals(bankBefore, model.getBank().getResources(),
                "Bank received resources for a settlement that wasn't built");
        assertTrue(testWinpointsAndSettlementStockOfPlayer(model, 0, winpointsBefore, settlementsBefore),
                "Either incorrect number of winpoints or number of structures left");
    }

    /**
     * Tests if buildRoad returns true and if player pays resources.
     *
     * Equivalence class: BuildSettlement player not enough resources
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame
     * Input:             Valid position with road to it
     * Expected Output:   That false is returned
     */
    @Test
    void buildRoadValidStartAndEndPoints() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        int roadsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.ROAD);
        Map<Config.Resource, Integer> playerBefore = new HashMap<>(model.getCurrentPlayer().getResources());
        Map<Config.Resource, Integer> bankBefore = new HashMap<>(model.getBank().getResources());
        assertTrue(model.buildRoad(new Point(6, 6), new Point(6, 4)));
        assertTrue(model.buildRoad(new Point(6, 4), new Point(7, 3)));

        Map<Config.Resource, Integer> costOfTwoRoads = Map.of(Config.Resource.GRAIN, 0, Config.Resource.WOOL, 0,
                Config.Resource.BRICK, 2, Config.Resource.ORE, 0, Config.Resource.LUMBER, 2);
        model.getCurrentPlayer().addResources(costOfTwoRoads);

        assertEquals(playerBefore, model.getCurrentPlayer().getResources(),
                "Player didn't pay the right amount of resources");
        assertEquals(bankBefore, model.getBank().getResources(),
                "Bank received resources for a settlement that wasn't built");
        assertTrue(testRoadStockOfPlayer(model, 2, roadsBefore),
                "Player doesn't have the right amount of roads left");
    }

    /**
     * Tests if buildRoad returns false when there is no road or settlement at the starting position
     *
     * Equivalence class: BuildRoad invalid start and end points
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank
     * Input:             Config.INVALID_EDGE_START_END_POINTS
     * Expected Output:   That false is returned and that the player didn't pay
     */
    @Test
    void buildRoadInvalidPosition() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        int roadsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.ROAD);
        Map<Config.Resource, Integer> before = new HashMap<>(model.getCurrentPlayer().getResources());
        for (Tuple<Point, Point> points : INVALID_EDGE_START_END_POINTS) {
            assertFalse(model.buildRoad(points.first, points.second),
                    "Road was built even though the start and endpoints weren't valid");
        }
        assertEquals(before, model.getCurrentPlayer().getResources(),
                "Player payed for a road that wasn't built");
        assertTrue(testRoadStockOfPlayer(model, 0, roadsBefore),
                "Player doesn't have the right amount of roads left");
    }

    /**
     * Tests if buildRoad returns false when there is a settlement or road at the start but the endpoint is in water
     *
     * Equivalence class: BuildRoad end point in water
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseSomeRoads
     * Input:
     * Expected Output:   That false is returned and that the player didn't pay
     */
    @Test
    void buildRoadIntoWater() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseSomeRoads(DEFAULT_WINPOINTS);
        model.switchToNextPlayer();
        model.switchToNextPlayer();
        model.getCurrentPlayer().addResources(Config.Structure.ROAD.getCostsAsMap());
        int roadsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.ROAD);
        Map<Config.Resource, Integer> before = new HashMap<>(model.getCurrentPlayer().getResources());
        //Start at settlement, end in water
        assertFalse(model.buildRoad(new Point(2,12), new Point(1,13)),
                "Road was built even though the endpoint was in water");
        //Start at road, end in water
        assertFalse(model.buildRoad(new Point(3,7), new Point(2,6)),
                "Road was built even though the endpoint was in water");

        assertEquals(before, model.getCurrentPlayer().getResources(),
                "Player payed for a road that wasn't built");
        assertTrue(testRoadStockOfPlayer(model, 0, roadsBefore),
                "Player doesn't have the right amount of roads left");
    }

    /**
     * Tests if buildRoad returns false if the road connection is blocked by another factions settlement
     *
     * Equivalence class: BuildRoad through another factions settlement
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank with road added leading to a
     *                    settlement of faction bb
     * Input:             Start and End points, that are cut off from the rest of the road by the settlement
     * of faction bb
     * Expected Output:   That false is returned and that the player didn't pay the resources
     */
    @Test
    void buildRoadThroughOthersSettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        model.placeInitialRoad(new Point(10, 16), new Point(11, 15));
        model.placeInitialRoad(new Point(11, 15), new Point(11, 13));
        int roadsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.ROAD);
        Map<Config.Resource, Integer> before = new HashMap<>(model.getCurrentPlayer().getResources());

        assertFalse(model.buildRoad(new Point(11, 13), new Point(10, 12)),
                "The road was built even though it should have been blocked by another factions settlement");
        assertEquals(before, model.getCurrentPlayer().getResources(),
                "Player payed for a road that wasn't built");
        assertTrue(testRoadStockOfPlayer(model, 0, roadsBefore),
                "Player doesn't have the right amount of roads left");
    }

    /**
     * Tests if buildCity throws a IllegalArgumentException if it's parameter is null.
     *
     * Equivalence class: BuildCity parameter null
     * Type of Test:      Negative
     * Initial state:     ThreePlayerStandard.getAfterSetupPhase
     * Input:             Null
     * Expected Output:   That a IllegalArgumentException is thrown
     */
    @Test
    void buildRoadParameterNull() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        assertThrows(IllegalArgumentException.class, () -> model.buildRoad(null, null),
                "buildRoad didn't throw exception for parameter null");
    }

    /**
     * This method tests wether the winpoints and the cities and settlement stock of the players match their expected
     * values, after placing cities.
     *
     * @param model the SiedlerGame instance that is tested
     * @param numberOfPlacedCities the number of cities that should have been placed in the test
     * @param winpointsBefore the number of winpoints the player had before building the cities
     * @param settlementsBefore the number of settlements the player had in stock before building the settlements
     * @return true if the winpoints of the model match the expected amount of winpoints and the players stock of
     *         cities and settlements match the expected amount
     */
    private boolean testWinpointsAndCityStockOfPlayer(SiedlerGame model, int numberOfPlacedCities, int winpointsBefore,
                                                      int settlementsBefore) {
        Config.Structure structure = Config.Structure.CITY;
        int expectedCities = structure.getStockPerPlayer() - numberOfPlacedCities;
        int actualCities = model.getCurrentPlayer().getStructureStock(structure);
        int expectedSettlements = settlementsBefore + numberOfPlacedCities;
        int actualSettlelements = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);
        if (expectedCities == actualCities && expectedSettlements == actualSettlelements &&
                winpointsBefore + numberOfPlacedCities == model.getCurrentPlayer().getWinPoints()) {
            return true;
        }
        return false;
    }

    /**
     * Tests if buildCity works when there is a settlement of the correct faction at the building position
     *
     * Equivalence class: BuildCity with settlement of correct faction
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank
     * Input:             Valid position with settlement position
     * Expected Output:   That true is returned
     */
    @Test
    void buildCityValidPosition() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        int winpointsBefore = model.getCurrentPlayer().getWinPoints();
        int settlementsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);
        Map<Config.Resource, Integer> playerBefore = new HashMap<>(model.getCurrentPlayer().getResources());
        Map<Config.Resource, Integer> bankBefore = new HashMap<>(model.getBank().getResources());
        assertTrue(model.buildCity(new Point(10, 16)),
                "The settlement was not upgraded to a city even though the position and resources were right.");

        Map<Config.Resource, Integer> costOfCity = Config.Structure.CITY.getCostsAsMap();
        model.getCurrentPlayer().addResources(costOfCity);
        model.getBank().tryRemoveResources(costOfCity);
        assertEquals(playerBefore, model.getCurrentPlayer().getResources(),
                "Player didn't pay the right amount of resources");
        assertEquals(bankBefore, model.getBank().getResources(),
                "Bank didn't receive resources for the built city");
        assertTrue(testWinpointsAndCityStockOfPlayer(model, 1, winpointsBefore, settlementsBefore),
                "Either incorrect number of winpoints or number of structures left");
    }

    /**
     * Tests if buildCity works when there is no settlement at the building position
     *
     * Equivalence class: BuildCity without settlement at building position
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank
     * Input:             Position without settlement
     * Expected Output:   That false is returned
     */
    @Test
    void buildCityInvalidPositionNoSettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        int winpointsBefore = model.getCurrentPlayer().getWinPoints();
        int settlementsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);

        //position 8,16 is a corner without settlement
        assertFalse(model.buildCity(new Point(8, 16)),
                "The corner was upgraded to a city even though there wasn't a settlement.");
        assertTrue(testWinpointsAndCityStockOfPlayer(model, 0, winpointsBefore, settlementsBefore),
                "Either incorrect number of winpoints or number of structures left");
    }

    /**
     * Tests if buildCity works when there is a settlement of the wrong faction at the building position
     *
     * Equivalence class: BuildCity with settlement of wrong faction
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank
     * Input:             Position with settlement of wrong faction
     * Expected Output:   That false is returned
     */
    @Test
    void buildCityInvalidPositionWrongFaction() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        int winpointsBefore = model.getCurrentPlayer().getWinPoints();
        int settlementsBefore = model.getCurrentPlayer().getStructureStock(Config.Structure.SETTLEMENT);

        //position 11,13 is a corner with a settlement of faction bb
        assertFalse(model.buildCity(new Point(11, 13)),
                "The corner was upgraded to a city even though the settlement was of the wrong faction.");
        assertTrue(testWinpointsAndCityStockOfPlayer(model, 0, winpointsBefore, settlementsBefore),
                "Either incorrect number of winpoints or number of structures left");
    }

    /**
     * Tests if buildCity works when the player doesn't have enough resources.
     *
     * Equivalence class: BuildCity valid position without resource
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhase
     * Input:             Valid position with settlement
     * Expected Output:   That false is returned
     */
    @Test
    void buildCityNotEnoughResources() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        assertFalse(model.buildCity(new Point(10, 16)),
                "The settlement was upgraded to a city even though there weren't enough resources");
        //position 10,16 is a corner with a settlement of faction rr
    }

    /**
     * Tests if buildCity throws a IllegalArgumentException if it's parameter is null.
     *
     * Equivalence class: BuildCity parameter null
     * Type of Test:      Negative
     * Initial state:     ThreePlayerStandard.getAfterSetupPhase
     * Input:             Null
     * Expected Output:   That a IllegalArgumentException is thrown
     */
    @Test
    void buildCityParameterNull() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        assertThrows(IllegalArgumentException.class, () -> model.buildCity(null),
                "buildCity didn't throw exception for position == null");
    }

    /**
     * Tests if trade with bank works when the bank and the player have enough resource to trade.
     *
     * Equivalence class: tradeWithBankFourToOne player and bank with enough resources
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank
     * Input:             None
     * Expected Output:   That the player pays 4 wool and receives 1 lumber and that the bank pays 1 lumber and
     *                    receives 4 wool
     */
    @Test
    void tradeWithBankFourToOneEnoughResources() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        Map<Config.Resource, Integer> playerAtBeginning = new HashMap<>(model.getCurrentPlayer().getResources());
        Map<Config.Resource, Integer> bankAtBeginning = new HashMap<>(model.getBank().getResources());
        int cardsOffered = model.FOUR_TO_ONE_TRADE_OFFER;
        int cardsReceived = model.FOUR_TO_ONE_TRADE_WANT;

        model.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.LUMBER);
        assertEquals(playerAtBeginning.get(Config.Resource.WOOL) - cardsOffered,
                model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player didn't remove traded resources");
        assertEquals(playerAtBeginning.get(Config.Resource.LUMBER) + cardsReceived,
                model.getCurrentPlayerResourceStock(Config.Resource.LUMBER),
                "Player didn't receive traded resources");
        assertEquals(bankAtBeginning.get(Config.Resource.WOOL) + cardsOffered,
                model.getBank().getResource(Config.Resource.WOOL),
                "Bank didn't remove traded resources");
        assertEquals(bankAtBeginning.get(Config.Resource.LUMBER) - cardsReceived,
                model.getBank().getResource(Config.Resource.LUMBER),
                "Bank didn't receive traded resources");
    }

    /**
     * Test if tradeFourToOneWithBank rejects the trade if the player doesn't have enough resources.
     *
     * Equivalence class: tradeWithBankFourToOne player not enough resources
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhase
     * Input:             None
     * Expected Output:   That the method returns false and player and bank resources don't change
     */
    @Test
    void tradeWithBankFourToOnePlayerNotEnoughResources() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        Map<Config.Resource, Integer> playerAtBeginning = new HashMap<>(model.getCurrentPlayer().getResources());
        Map<Config.Resource, Integer> bankAtBeginning = new HashMap<>(model.getBank().getResources());

        assertFalse(model.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.LUMBER),
                "The trade took place even though the player didn't have enough resources");
        assertEquals(playerAtBeginning.get(Config.Resource.WOOL), model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player removed resources even though the trade didn't take place");
        assertEquals(playerAtBeginning.get(Config.Resource.LUMBER), model.getCurrentPlayerResourceStock(Config.Resource.LUMBER),
                "Player received resources even though the trade didn't take place");
        assertEquals(bankAtBeginning.get(Config.Resource.WOOL), model.getBank().getResource(Config.Resource.WOOL),
                "Bank removed resources even though the trade didn't take place");
        assertEquals(bankAtBeginning.get(Config.Resource.LUMBER), model.getBank().getResource(Config.Resource.LUMBER),
                "Bank received resources even though the trade didn't take place");
    }

    /**
     * Test if tradeFourToOneWithBank rejects the trade if the bank doesn't have enough resources.
     *
     * Equivalence class: tradeWithBankFourToOne Bank with insufficient funds
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame, added 4 wool to player and emptied bank
     * Input:             None
     * Expected Output:   That the method returns false and player and bank resources don't change
     */
    @Test
    void tradeWithBankFourToOneBankNotEnoughResources() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        model.getCurrentPlayer().addResources(Map.of(Config.Resource.GRAIN, 0, Config.Resource.WOOL, 4,
                Config.Resource.BRICK, 0, Config.Resource.ORE, 0, Config.Resource.LUMBER, 0));
        model.getBank().tryRemoveResources(Config.INITIAL_RESOURCE_CARDS_BANK);
        Map<Config.Resource, Integer> playerAtBeginning = new HashMap<>(model.getCurrentPlayer().getResources());
        Map<Config.Resource, Integer> bankAtBeginning = new HashMap<>(model.getBank().getResources());

        assertFalse(model.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.LUMBER),
                "The trade took place even though the player didn't have enough resources");
        assertEquals(playerAtBeginning.get(Config.Resource.WOOL), model.getCurrentPlayerResourceStock(Config.Resource.WOOL),
                "Player removed resources even though the trade didn't take place");
        assertEquals(playerAtBeginning.get(Config.Resource.LUMBER), model.getCurrentPlayerResourceStock(Config.Resource.LUMBER),
                "Player received resources even though the trade didn't take place");
        assertEquals(bankAtBeginning.get(Config.Resource.WOOL), model.getBank().getResource(Config.Resource.WOOL),
                "Bank removed resources even though the trade didn't take place");
        assertEquals(bankAtBeginning.get(Config.Resource.LUMBER), model.getBank().getResource(Config.Resource.LUMBER),
                "Bank received resources even though the trade didn't take place");
    }

    /**
     * Test if tradeFourToOneWithBank throws IllegalArgumentException when given a parameter that is null
     *
     * Equivalence class: tradeWithBankFourToOne parameter null
     * Type of Test:      Negative
     * Initial state:     new SiedlerGame
     * Input:             Null
     * Expected Output:   That the method throws an IllegalArgumentException if either parameter is null
     */
    @Test
    void tradeWithBankFourToOneParameterNull() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        assertThrows(IllegalArgumentException.class, () -> model.tradeWithBankFourToOne(null, Config.Resource.GRAIN),
                "tradeWithBankFourToOne didn't throw exception for first parameter == null");
        assertThrows(IllegalArgumentException.class, () -> model.tradeWithBankFourToOne(Config.Resource.GRAIN, null),
                "tradeWithBankFourToOne didn't throw exception for second parameter == null");
        assertThrows(IllegalArgumentException.class, () -> model.tradeWithBankFourToOne(null, null),
                "tradeWithBankFourToOne didn't throw exception for both parameters == null");
    }

    /**
     * Test if getWinner gives back the correct faction
     *
     * Equivalence class: getWinner someone won
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame, faction 2 is given required amount of winpoints
     * Input:             Null
     * Expected Output:   That the method returns faction 2
     */
    @Test
    void getWinnerSomeoneWon() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        model.switchToNextPlayer();
        model.getCurrentPlayer().addWinPoints(DEFAULT_WINPOINTS);
        assertEquals(model.getPlayerFactions().get(1), model.getWinner(),
                "GetWinner didn't give the winner");
    }

    /**
     * Test if getWinner gives back null if no one has reached the required winpoints
     *
     * Equivalence class: getWinner no one won
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame
     * Input:             Null
     * Expected Output:   That the method returns null
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    void getWinnerNoOneWon(int points) {
        SiedlerGame model = new SiedlerGame(5, DEFAULT_NUMBER_OF_PLAYERS);
        model.switchToNextPlayer();
        model.getCurrentPlayer().addWinPoints(points);
        assertNull(model.getWinner(),
                "GetWinner returned a faction even though no one has reached the required winpoints");
    }

    /**
     * Test if getScoreboard returns the correct win points for each faction
     *
     * Equivalence class: getScoreboard
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame, with different amount of points added to the factions
     * Input:             None
     * Expected Output:   That the method returns the correct Map of win points
     */
    @Test
    void getScoreboard() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, 3);
        for (int i = 0; i < 3; i++) {
            model.getCurrentPlayer().addWinPoints(i);
            model.switchToNextPlayer();
        }
        Map<Config.Faction, Integer> actual = model.getScoreboard();
        Map<Config.Faction, Integer> expected = Map.of(Config.Faction.RED, 0, Config.Faction.BLUE, 1,
                Config.Faction.GREEN, 2);

        assertEquals(expected, actual,
                "getScoreboard didn't return the expected scores");
    }

    /**
     * Test if placeThiefAndStealCard throws IllegalArgumentException when given the parameter null
     *
     * Equivalence class: placeThiefAndStealCard parameter null
     * Type of Test:      Negative
     * Initial state:     new SiedlerGame
     * Input:             Null
     * Expected Output:   That the method throws an IllegalArgumentException
     */
    @Test
    void placeThiefAndStealCardParameterNull() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        assertThrows(IllegalArgumentException.class, () -> model.placeThiefAndStealCard(null),
                "placeThiefAndStealCard didn't throw exception for parameter == null");
    }

    /**
     * Test if placeThiefAndStealCard is correctly placed in the selected field and if it steals a resource card from
     * a player owning a settlement around that field
     *
     * Equivalence class: placeThiefAndStealCard valid position
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank
     * Input:             (10, 14): a pasture field with settlements of factions rr and bb
     * Expected Output:   That the thief is placed in the chosen position and that a resources has been stolen from
     *                    either rr or bb
     */
    @Test
    void placeThiefAndStealCardValidPosition() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        Map<Config.Resource, Integer> bankBeginning = new HashMap<>(model.getBank().getResources());

        int expectedFaction1 = model.getCurrentPlayer().getResources().values().stream().mapToInt(Integer::intValue).sum();
        model.switchToNextPlayer();
        int expectedFaction2 = model.getCurrentPlayer().getResources().values().stream().mapToInt(Integer::intValue).sum();
        model.switchToNextPlayer();

        assertTrue(model.placeThiefAndStealCard(new Point(10, 14)),
                "PlaceThiefAndStealCard returned false even though the chosen field is valid");
        model.switchToPreviousPlayer();
        int actualFaction1 = model.getCurrentPlayer().getResources().values().stream().mapToInt(Integer::intValue).sum();
        model.switchToPreviousPlayer();
        int actualFaction2 = model.getCurrentPlayer().getResources().values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(expectedFaction1 + expectedFaction2 - 1, actualFaction1 + actualFaction2,
                "PlaceThiefAndStealCard hasn't stolen the right amount of resources from the players");
        assertEquals(bankBeginning, model.getBank().getResources(),
                "PlaceThiefAndStealCard hasn't added the stolen resources to the bank");
    }

    /**
     * Test if placeThiefAndStealCard recognises the invalid input and returns false
     *
     * Equivalence class: placeThiefAndStealCard invalid position
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank
     * Input:             (13, 11): a water field
     * Expected Output:   That false is returned
     */
    @Test
    void placeThiefAndStealCardInvalidPosition() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        assertFalse(model.placeThiefAndStealCard(new Point(13, 11)),
                "PlaceThiefAndStealCard returned true even though the chosen field isn't valid");

    }

    /**
     * Test if placeThiefAndStealCard accidentally puts the player resources in the negative if he doesn't have resources
     *
     * Equivalence class: placeThiefAndStealCard player doesn't have resources
     * Type of Test:      Positive
     * Initial state:     new SiedlerGame
     * Input:             (3, 11): a forest field with a settlement of faction gg
     * Expected Output:   That the thief is placed in the chosen position but doesn't remove resources from the player
     */
    @Test
    void placeThiefAndStealCardPlayerNoResources() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        assertTrue(model.placeThiefAndStealCard(new Point(3, 11)),
                "PlaceThiefAndStealCard returned false even though the chosen field is valid");
        model.switchToNextPlayer();
        model.switchToNextPlayer();
        assertEquals(Config.createEmptyResourceMap(), model.getCurrentPlayer().getResources(),
                "PlaceThiefAndStealCard stole resources from a player that didn't have any");
    }

    /**
     * Tests if throwDice returns the expected Map of resources per faction
     *
     * Equivalence class: throw dice many times, one resource remaining
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhase
     * Input:             None
     * Expected Output:   That faction rr and bb get 1 wool each until bank only has 1 wool remaining
     */
    @Test
    void throwDiceUntilEmptyOneRemaining() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);

        //Test payout while enough wool in bank
        while(model.getBank().getResource(Config.Resource.WOOL) >= 2){
            Map<Config.Faction, List<Config.Resource>> payout = model.throwDice(12);
            assertEquals(1, payout.get(Config.Faction.RED).size());
            assertEquals(1, payout.get(Config.Faction.BLUE).size());
            assertEquals(0, payout.get(Config.Faction.GREEN).size());
        }

        int woolLeftInBankBefore = model.getBank().getResource(Config.Resource.WOOL);

        //Test payout while not enough wool in bank
        Map<Config.Faction, List<Config.Resource>> payout = model.throwDice(12);
        assertEquals(0, payout.get(Config.Faction.RED).size());
        assertEquals(0, payout.get(Config.Faction.BLUE).size());
        assertEquals(0, payout.get(Config.Faction.GREEN).size());

        //Test if the wool is left untouched in bank
        int woolLeftInBankAfter = model.getBank().getResource(Config.Resource.WOOL);
        assertEquals(woolLeftInBankBefore, woolLeftInBankAfter);
    }

    /**
     * Tests if throwDice returns the expected Map of resources per faction
     *
     * Equivalence class: throw dice many times, zero resource remaining
     * Type of Test:      Positive
     * Initial state:     ThreePlayerStandard.getAfterSetupPhase
     * Input:             None
     * Expected Output:   That faction rr and bb get 1 wool each until bank has 0 wool remaining
     */
    @Test
    void throwDiceUntilEmptyZeroRemaining() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);

        //add one extra wool to bank
        Map<Config.Resource, Integer> oneWool = Config.createEmptyResourceMap();
        oneWool.put(Config.Resource.WOOL, 1);
        model.getBank().addResources(oneWool);

        //Test payout while enough wool in bank
        while(model.getBank().getResource(Config.Resource.WOOL) >= 2){
            Map<Config.Faction, List<Config.Resource>> payout = model.throwDice(12);
            assertEquals(1, payout.get(Config.Faction.RED).size());
            assertEquals(1, payout.get(Config.Faction.BLUE).size());
            assertEquals(0, payout.get(Config.Faction.GREEN).size());
        }

        int woolLeftInBankBefore = model.getBank().getResource(Config.Resource.WOOL);

        //Test payout while not enough wool in bank
        Map<Config.Faction, List<Config.Resource>> payout = model.throwDice(12);
        assertEquals(0, payout.get(Config.Faction.RED).size());
        assertEquals(0, payout.get(Config.Faction.BLUE).size());
        assertEquals(0, payout.get(Config.Faction.GREEN).size());

        //Test if the wool is left untouched in bank
        int woolLeftInBankAfter = model.getBank().getResource(Config.Resource.WOOL);
        assertEquals(woolLeftInBankBefore, woolLeftInBankAfter);
    }
}