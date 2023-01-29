package ch.zhaw.catan;

import ch.zhaw.catan.games.ThreePlayerStandard;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains some basic tests for the {@link SiedlerGame} class
 * <p></p>
 * <p>DO NOT MODIFY THIS CLASS</p>
 *
 * @author tebe
 */
class SiedlerGameTestBasic {
    private final static int DEFAULT_WINPOINTS = 5;
    private final static int DEFAULT_NUMBER_OF_PLAYERS = 3;

    /**
     * Tests whether the functionality for switching to the next/previous player
     * works as expected for different numbers of players.
     *
     * @param numberOfPlayers the number of players
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void requirementPlayerSwitching(int numberOfPlayers) {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, numberOfPlayers);
        assertEquals(numberOfPlayers, model.getPlayerFactions().size(),
                "Wrong number of players returned by getPlayers()");
        //Switching forward
        for (int i = 0; i < numberOfPlayers; i++) {
            assertEquals(Config.Faction.values()[i], model.getCurrentPlayerFaction(),
                    "Player order does not match order of Faction.values()");
            model.switchToNextPlayer();
        }
        assertEquals(Config.Faction.values()[0], model.getCurrentPlayerFaction(),
                "Player wrap-around from last player to first player did not work.");
        //Switching backward
        for (int i = numberOfPlayers - 1; i >= 0; i--) {
            model.switchToPreviousPlayer();
            assertEquals(Config.Faction.values()[i], model.getCurrentPlayerFaction(),
                    "Switching players in reverse order does not work as expected.");
        }
    }

    /**
     * Tests whether the game board meets the required layout/land placement.
     */
    @Test
    void requirementLandPlacementTest() {
        SiedlerGame model = new SiedlerGame(DEFAULT_WINPOINTS, DEFAULT_NUMBER_OF_PLAYERS);
        assertTrue(Config.getStandardLandPlacement().size() == model.getBoard().getFields().size(),
                "Check if explicit init must be done (violates spec): "
                        + "modify initializeSiedlerGame accordingly.");
        for (Map.Entry<Point, Config.Land> e : Config.getStandardLandPlacement().entrySet()) {
            assertEquals(e.getValue(), model.getBoard().getField(e.getKey()),
                    "Land placement does not match default placement.");
        }
    }

    /**
     * Tests whether the {@link ThreePlayerStandard#getAfterSetupPhase(int)}} game board is not empty (returns
     * an object) at positions where settlements and roads have been placed.
     */
    @Test
    void requirementSettlementAndRoadPositionsOccupiedThreePlayerStandard() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        assertEquals(DEFAULT_NUMBER_OF_PLAYERS, model.getPlayerFactions().size());
        for (Config.Faction f : model.getPlayerFactions()) {
            assertTrue(model.getBoard().getCorner(ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(f).first) != null);
            assertTrue(model.getBoard().getCorner(ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(f).second) != null);
            assertTrue(model.getBoard().getEdge(ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(f).first, ThreePlayerStandard.INITIAL_ROAD_ENDPOINTS.get(f).first) != null);
            assertTrue(model.getBoard().getEdge(ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(f).second, ThreePlayerStandard.INITIAL_ROAD_ENDPOINTS.get(f).second) != null);
        }
    }

    /**
     * Checks that the resource card payout for different dice values matches
     * the expected payout for the game state {@link ThreePlayerStandard#getAfterSetupPhase(int)}}.
     * <p>
     * Note, that for the test to work, the {@link Map} returned by {@link SiedlerGame#throwDice(int)}
     * must contain a {@link List} with resource cards (empty {@link List}, if the player gets none)
     * for each of the players.
     * </p>
     * @param diceValue the dice value
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 8, 9, 10, 11, 12})
    void requirementDiceThrowResourcePayoutThreePlayerStandardTest(int diceValue) {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        Map<Config.Faction, List<Config.Resource>> expected = ThreePlayerStandard.INITIAL_DICE_THROW_PAYOUT.get(diceValue);
        Map<Config.Faction, List<Config.Resource>> actual = model.throwDice(diceValue);
        assertEquals(expected, actual);
    }

    /**
     * Tests whether the resource card stock of the players matches the expected stock
     * for the game state {@link ThreePlayerStandard#getAfterSetupPhase(int)}}.
     */
    @Test
    void requirementPlayerResourceCardStockAfterSetupPhase() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        assertPlayerResourceCardStockEquals(model, ThreePlayerStandard.INITIAL_PLAYER_CARD_STOCK);
    }

    /**
     * Tests whether the resource card stock of the players matches the expected stock
     * for the game state {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}}.
     */
    @Test
    void requirementPlayerResourceCardStockAfterSetupPhaseAlmostEmptyBank() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        assertPlayerResourceCardStockEquals(model, ThreePlayerStandard.BANK_ALMOST_EMPTY_RESOURCE_CARD_STOCK);
    }

    /**
     * Tests whether the resource card stock of the players matches the expected stock
     * for the game state {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}}.
     */
    @Test
    void requirementPlayerResourceCardStockPlayerOneReadyToBuildFifthSettlement() {
        SiedlerGame model = ThreePlayerStandard.getPlayerOneReadyToBuildFifthSettlement(DEFAULT_WINPOINTS);
        assertPlayerResourceCardStockEquals(model, ThreePlayerStandard.PLAYER_ONE_READY_TO_BUILD_FIFTH_SETTLEMENT_RESOURCE_CARD_STOCK);
    }

    /**
     * Throws each dice value except 7 once and tests whether the resource
     * card stock of the players matches the expected stock.
     */
    @Test
    void requirementDiceThrowPlayerResourceCardStockUpdateTest() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        for(int i : List.of(2, 3, 4, 5, 6, 8, 9, 10, 11, 12)) {
            model.throwDice(i);
        }
        Map<Config.Faction, Map<Config.Resource, Integer>> expected = Map.of(
                Config.Faction.values()[0], Map.of(Config.Resource.GRAIN, 1, Config.Resource.WOOL, 2,
                        Config.Resource.BRICK, 2, Config.Resource.ORE, 1, Config.Resource.LUMBER, 1),
                Config.Faction.values()[1],
                Map.of(Config.Resource.GRAIN, 1, Config.Resource.WOOL, 5, Config.Resource.BRICK, 0,
                        Config.Resource.ORE, 0, Config.Resource.LUMBER, 0),
                Config.Faction.values()[2],
                Map.of(Config.Resource.GRAIN, 0, Config.Resource.WOOL, 0, Config.Resource.BRICK, 2,
                        Config.Resource.ORE, 0, Config.Resource.LUMBER, 1));

        assertPlayerResourceCardStockEquals(model, expected);
    }

    private void assertPlayerResourceCardStockEquals(SiedlerGame model, Map<Config.Faction, Map<Config.Resource, Integer>> expected) {
        for (int i = 0; i < expected.keySet().size(); i++) {
            Config.Faction f = model.getCurrentPlayerFaction();
            for (Config.Resource r : Config.Resource.values()) {
                assertEquals(expected.get(f).get(r), model.getCurrentPlayerResourceStock(r),
                        "Resource card stock of player " + i + " [faction " + f + "] for resource type " + r + " does not match.");
            }
            model.switchToNextPlayer();
        }
    }

    /**
     * Tests whether player one can build two roads starting in game state
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}.
     */
    @Test
    void requirementBuildRoad() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        assertTrue(model.buildRoad(new Point(6, 6), new Point(6, 4)));
        assertTrue(model.buildRoad(new Point(6, 4), new Point(7, 3)));
    }

    /**
     * Tests whether player one can build a road and a settlement starting in game state
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}.
     */
    @Test
    void requirementBuildSettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        assertTrue(model.buildRoad(new Point(9, 15), new Point(9, 13)));
        assertTrue(model.buildSettlement(new Point(9, 13)));
    }

    /**
     * Tests whether payout with multiple settlements of the same player at one field works
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}.
     */
    @Test
    void requirementTwoSettlementsSamePlayerSameFieldResourceCardPayout() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        for(int diceValue : List.of(2, 6, 6, 11)){
          model.throwDice(diceValue);
        }
        assertTrue(model.buildRoad(new Point(6, 6), new Point(7, 7)));
        assertTrue(model.buildSettlement(new Point(7, 7)));
        assertEquals(List.of(Config.Resource.ORE, Config.Resource.ORE), model.throwDice(4).get(model.getCurrentPlayerFaction()));
    }

    /**
     * Tests whether player one can build a city starting in game state
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}.
     */
    @Test
    void requirementBuildCity() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        assertTrue(model.buildCity(new Point(10, 16)));
    }

    /**
     * Tests whether player two can trade in resources with the bank and has the
     * correct number of resource cards afterwards. The test starts from game state
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}.
     */
    @Test
    void requirementCanTradeFourToOneWithBank() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(DEFAULT_WINPOINTS);
        model.switchToNextPlayer();

        Map<Config.Resource, Integer> expectedResourceCards = ThreePlayerStandard.BANK_ALMOST_EMPTY_RESOURCE_CARD_STOCK.get(model.getCurrentPlayerFaction());
        assertEquals(expectedResourceCards.get(Config.Resource.WOOL), model.getCurrentPlayerResourceStock(Config.Resource.WOOL));
        assertEquals(expectedResourceCards.get(Config.Resource.LUMBER), model.getCurrentPlayerResourceStock(Config.Resource.LUMBER));

        model.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.LUMBER);

        int cardsOffered = 4;
        int cardsReceived = 1;
        assertEquals(expectedResourceCards.get(Config.Resource.WOOL)-cardsOffered, model.getCurrentPlayerResourceStock(Config.Resource.WOOL));
        assertEquals(expectedResourceCards.get(Config.Resource.LUMBER)+cardsReceived, model.getCurrentPlayerResourceStock(Config.Resource.LUMBER));
    }

    /***
     * This test is not actually a test and should be removed. However,
     * we leave it in for you to have a quick and easy way to look at the
     * game board produced by {@link ThreePlayerStandard#getAfterSetupPhase(int)},
     * augmented by annotations, which you won't need since we do not ask for
     * more advanced trading functionality using harbours.
     */
    @Disabled
    @Test
    void print(){
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(DEFAULT_WINPOINTS);
        model.getBoard().addFieldAnnotation(new Point(6, 8),new Point(6, 6), "N ");
        model.getBoard().addFieldAnnotation(new Point(6, 8),new Point(5, 7), "NE");
        model.getBoard().addFieldAnnotation(new Point(6, 8),new Point(5, 9), "SE");
        model.getBoard().addFieldAnnotation(new Point(6, 8),new Point(6, 10), "S ");
        model.getBoard().addFieldAnnotation(new Point(6, 8),new Point(7, 7), "NW");
        model.getBoard().addFieldAnnotation(new Point(6, 8),new Point(7, 9), "SW");
        System.out.println(new SiedlerBoardTextView(model.getBoard()));
    }
}
