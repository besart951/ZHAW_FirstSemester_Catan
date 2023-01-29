package ch.zhaw.catan.gamephases;

import ch.zhaw.catan.SiedlerGame;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.awt.Point;

/**
 * In the initial game phase all players can place one settlement and one road each. This happens once in
 * ascending order and once in descending order. In the descending order, resources are additionally
 * distributed to the players, depending on which fields are adjacent to the settlement.
 *
 * @author Louie Wolf
 * @version 07.12.2022
 */
public class InitialPhase extends GamePhase {
    /**
     * Constructs a new end game phase.
     *
     * @param textIO specifies the text input/output.
     * @param textTerminal specifies in which terminal the text output is displayed.
     * @param siedlerGame specifies the game this phase is part of.
     */
    public InitialPhase(TextIO textIO, TextTerminal<?> textTerminal, SiedlerGame siedlerGame){
        super(textIO, textTerminal, siedlerGame);
    }

    @Override
    public void onStart() {
        textTerminal.println("The game begins! All players can set their initial buildings now...");

        super.onStart();
    }

    /**
     * Gives players the ability to place their initial buildings in ascending and descending order,
     * with only descending order giving resources to the player. Then executes the method of the abstract class.
     */
    @Override
    protected void onUpdate() {
        int playerAmount = siedlerGame.getPlayerAmount();

        boolean playerGetsPayout = false;
        for (int i = 0; i < playerAmount; i++){
            placeInitialBuildings(playerGetsPayout);

            if (i != playerAmount - 1){ //last player
                siedlerGame.switchToNextPlayer();
            }

            currentFactionName = siedlerGame.getCurrentPlayerFaction().toString();
        }

        playerGetsPayout = true;
        for (int i = playerAmount; i > 0; i--){
            placeInitialBuildings(playerGetsPayout);

            if (i != 1){ //first player
                siedlerGame.switchToPreviousPlayer();
            }

            currentFactionName = siedlerGame.getCurrentPlayerFaction().toString();
        }

        super.onUpdate();
    }

    @Override
    protected void onEnd() {
        textTerminal.println("All initial buildings have been set!");

        super.onEnd();
    }

    /**
     * First asks the player for an input to place an initial settlement and then an initial road. Repeat the process
     * until both structures have been successfully placed.
     *
     * @param playerGetsPayout specifies whether the player should get resources after placing his settlement or not.
     */
    private void placeInitialBuildings(boolean playerGetsPayout){
        textTerminal.println(siedlerGame.getBoard().toString());

        boolean settlementPlaced = false;
        while (!settlementPlaced){
            settlementPlaced = placeInitialSettlement(playerGetsPayout);
            if (!settlementPlaced) {
                textTerminal.println("Please enter valid coordinates. Only corners are valid");
            }
        }

        textTerminal.println(siedlerGame.getBoard().toString());
        boolean roadPlaced = false;
        while (!roadPlaced) {
            roadPlaced = placeInitialRoad();
            if (!roadPlaced) {
                textTerminal.println("Please enter valid coordinates.");
            }
        }

        textTerminal.println(siedlerGame.getBoard().toString());
    }

    /**
     * Asks the player for input to place an initial settlement.
     *
     * @param playerGetsPayout specifies whether the player should get resources after placing his settlement or not.
     * @return true if the settlement was successfully build, false otherwise.
     */
    private boolean placeInitialSettlement(boolean playerGetsPayout){
        String message = "(" + currentFactionName + ") where do you want to place your initial settlement?";
        Point initialSettlementPoint = getCoordinateInput(message);

        return siedlerGame.placeInitialSettlement(initialSettlementPoint,playerGetsPayout);
    }

    /**
     * Asks the player for input to place an initial road.
     *
     * @return true if the road was successfully build, false otherwise.
     */
    private boolean placeInitialRoad(){
        Point roadStart = getCoordinateInput("(" + currentFactionName + ") where do you want to place the start " +
                "point of your initial road?");
        Point roadEnd = getCoordinateInput("(" + currentFactionName + ") where do you want to place the end " +
                "point of your initial road?");

        return siedlerGame.placeInitialRoad(roadStart, roadEnd);
    }
}
