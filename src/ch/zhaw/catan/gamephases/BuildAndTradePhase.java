package ch.zhaw.catan.gamephases;

import ch.zhaw.catan.Config;
import ch.zhaw.catan.Player;
import ch.zhaw.catan.SiedlerGame;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.awt.Point;
import java.util.Map;

/**
 * In the Build and Trade Phase, the player can choose between different actions. The selection includes all
 * values of the included Actions Enum. For each choice there is an associated method.
 *
 * @author Louie Wolf
 * @version 07.12.2022
 */
public class BuildAndTradePhase extends GamePhase {
    private final EndPhase endPhase;
    private boolean roundEnd;

    //Actions the player can choose from
    public enum Actions {
        SHOW_FIELD,
        SHOW_RESOURCES,
        BUILD_ROAD,
        BUILD_SETTLEMENT,
        BUILD_CITY,
        TRADE,
        END_ROUND,
        QUIT
    }

    /**
     * Constructs a new BuildAndTrade game phase.
     *
     * @param textIO specifies the text input/output.
     * @param textTerminal specifies in which terminal the text output is displayed.
     * @param siedlerGame specifies the game this phase is part of.
     * @param endPhase specifies the end game phase into which the program switches when the player wants to quit.
     * @throws IllegalArgumentException if endPhase is null.
     */
    public BuildAndTradePhase(TextIO textIO, TextTerminal<?> textTerminal, SiedlerGame siedlerGame, EndPhase endPhase){
        super(textIO, textTerminal, siedlerGame);
        if (endPhase == null){
            throw new IllegalArgumentException("End phase must not be null!");
        }
        this.roundEnd = false;
        this.endPhase = endPhase;
    }

    @Override
    public void onStart() {
        textTerminal.println("(" + currentFactionName + ") is now able to build or trade...");
        roundEnd = false;

        super.onStart();
    }

    @Override
    protected void onUpdate() {
        while (!roundEnd) {
            switch (getEnumValue(textIO, BuildAndTradePhase.Actions.class, "(" + currentFactionName + ") what would you like to do?")) {
                case SHOW_FIELD -> showField();
                case END_ROUND -> endRound();
                case BUILD_ROAD -> buildRoad();
                case BUILD_SETTLEMENT -> buildSettlement();
                case BUILD_CITY -> buildCity();
                case TRADE -> trade();
                case SHOW_RESOURCES -> showResources();
                case QUIT -> quit();
                default -> throw new IllegalStateException("Internal error found - Command not implemented.");
            }
        }

        super.onUpdate();
    }

    @Override
    protected void onEnd() {
        textTerminal.println("(" + currentFactionName + ") ended his turn!");

        super.onEnd();
    }

    /**
     * Displays the current state of the board.
     */
    private void showField(){
        textTerminal.println(siedlerGame.getBoard().toString());
    }

    /**
     * Ask the player for coordinates to build a road. If the input is valid and the player has the required
     * resources, the road will be built, otherwise he will be informed that the road building failed.
     */
    private void buildRoad(){
        Point roadStart = getCoordinateInput("(" + currentFactionName + ") Where do you want to build " +
                "the start of your road?");
        Point roadEnd = getCoordinateInput("(" + currentFactionName + ") Where do you want to build " +
                "the end of your road?");

        if (!siedlerGame.buildRoad(roadStart, roadEnd)){
            textTerminal.println("The road could not be built!");
            return;
        }
        textTerminal.println("The road was successfully built!");
    }

    /**
     * Asks the player for coordinates to build a settlement. If the input is valid and the player has the
     * required resources, the settlement will be built, otherwise he will be informed that the building of
     * the settlement failed. It also checks if the player has enough points to win the game.
     */
    private void buildSettlement(){
        Point settlementPoint = getCoordinateInput("(" + currentFactionName + ") Where do you want to build " +
                "your settlement?");

        if (!siedlerGame.buildSettlement(settlementPoint)){
            textTerminal.println("The settlement could not be built!");
            return;
        }
        textTerminal.println("The settlement was successfully built!");

        checkIfWon();
    }

    /**
     * Asks the player for coordinates to build a city. If the input is valid and the player has the required
     * resources, the city will be built, otherwise he will be informed that the building of the settlement failed.
     * It also checks if the player has enough points to win the game.
     */
    private void buildCity(){
        Point cityPoint = getCoordinateInput("(" + currentFactionName + ") Where do you want to build " +
                "your city?");

        if (!siedlerGame.buildCity(cityPoint)){
            textTerminal.println("The city could not be built!");
            return;
        }
        textTerminal.println("The city was successfully built!");

        checkIfWon();
    }

    /**
     * Ask the player which resource he wants to receive and which he wants to exchange for it. If he has enough
     * resources, the trade is made, otherwise he is informed that the trade was not made.
     */
    private void trade(){
        Config.Resource want = selectResource("What resource do you want?");
        Config.Resource offer = selectResource("What resource do you want to offer?");

        if (!siedlerGame.tradeWithBankFourToOne(offer,want)){
            textTerminal.println("Either you or the bank does not have enough resources!");
            return;
        }
        textTerminal.println("You have successfully traded resource: " + offer.toString() + " for resource " + want.toString());
    }

    /**
     * Asks the player to select a resource, which is then returned as an enum value.
     *
     * @param message specifies the message that will be sent to the player.
     * @return the selected enum value.
     * @throws IllegalArgumentException if message parameter is null.
     */
    private Config.Resource selectResource(String message){
        if (message == null){
            throw new IllegalArgumentException("Message parameter must not be null!");
        }
        Config.Resource selectedResource;

        switch (getEnumValue(textIO, Config.Resource.class, message)) {
            case LUMBER -> selectedResource = Config.Resource.LUMBER;
            case ORE -> selectedResource = Config.Resource.ORE;
            case GRAIN -> selectedResource = Config.Resource.GRAIN;
            case BRICK -> selectedResource = Config.Resource.BRICK;
            case WOOL -> selectedResource = Config.Resource.WOOL;
            default -> throw new IllegalStateException("Internal error found - Command not implemented.");
        }

        return selectedResource;
    }

    /**
     * Displays the amount of resources of the current player.
     */
    private void showResources() {
        Player player = siedlerGame.getCurrentPlayer();
        StringBuilder builder = new StringBuilder(64);
        builder.append("(").append(currentFactionName).append(") Resources:\n");

        Map<Config.Resource, Integer> playerResources = player.getResources();
        for (Map.Entry<Config.Resource, Integer> resource: playerResources.entrySet()) {
            String symbol = switch (resource.getKey()) {
                case LUMBER -> "🌳";
                case ORE -> "🏔";
                case GRAIN -> "\uD83C\uDF3E";
                case BRICK -> "🧱";
                case WOOL -> "🐑";
            };

            builder.append("\t");
            builder.append(resource.getKey().toString());
            builder.append(": ");
            builder.append(resource.getValue());
            builder.append(' ');
            builder.append(symbol);
            builder.append('\n');
        }

        textTerminal.println(builder.toString());;
    }

    /**
     * Ends the current player's turn and moves to the next one.
     */
    private void endRound(){
        roundEnd = true;
        siedlerGame.switchToNextPlayer();
    }

    /**
     * Sets the end phase as the next phase and ends the player's turn.
     */
    private void quit(){
        roundEnd = true;
        nextPhase = endPhase;
    }

    /**
     * Checks if a player has won the game and quits it if yes.
     */
    private void checkIfWon(){
        if (siedlerGame.getWinner() == null){
            return;
        }

        quit();
    }
}
