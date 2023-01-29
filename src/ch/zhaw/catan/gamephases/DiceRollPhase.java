package ch.zhaw.catan.gamephases;

import ch.zhaw.catan.Config;
import ch.zhaw.catan.SiedlerGame;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.awt.Point;
import java.util.Random;

/**
 * In the dice roll phase, the player rolls a random number between 2 and 12, whereupon resources of the respective
 * resource fields are distributed to the players who own an adjacent settlement. If 7 is rolled, the special case
 * occurs that all players with more than 7 resources have to give away half of their resources. In addition, the
 * player who rolled 7 can place the thief in another location and steal a random resource if another player's
 * settlement is on that space.
 *
 * @author Louie Wolf
 * @version 02.12.2022
 */
public class DiceRollPhase extends GamePhase{
    private static final String THIEF_ASCII_ART = """
    ⠀⠀⠀⠀⢀⡀⢀⡀⠀⠀⠀
    ⠀⣠⣶⣿⣿⣿⣿⣷⣄⠀
    ⢰⣿⣿⣿⣿⣿⣿⣿⣿⣆
    ⣾⣿⣿⣿⣿⣿⣿⣿⣿⣿
    ⣠⣴⣶⣦⣤⣤⣴⣶⣦⣄
    ⢿⣧⣤⣼⣿⣿⣧⣤⣼⡿
    ⠀⠀⠉⠁⠀⠀⠈⠉⠀⠀""";

    /**
     * Constructs a new DiceRoll game phase.
     *
     * @param textIO specifies the text input/output.
     * @param textTerminal specifies in which terminal the text output is displayed.
     * @param siedlerGame specifies the game this phase is part of.
     */
    public DiceRollPhase(TextIO textIO, TextTerminal<?> textTerminal, SiedlerGame siedlerGame){
        super(textIO, textTerminal, siedlerGame);
    }

    @Override
    public void onStart() {
        textTerminal.println("(" + currentFactionName + ") rolls the dice...");

        super.onStart();
    }

    /**
     * Generates a random number within the dice value range, displays it to the players, and distributes the
     * resources of the associated resource square. If it is a 7, the player whose turn it is can place the thief
     * to steal a card and everyone must give up half their resources. Then executes the method of the abstract class.
     */
    @Override
    protected void onUpdate() {
        Random random = new Random();
        int randomDiceNumber = random.nextInt(Config.MIN_DICE_VALUE, Config.MAX_DICE_VALUE + 1);

        siedlerGame.throwDice(randomDiceNumber);
        textTerminal.println("(" + currentFactionName + ") rolled a " + randomDiceNumber);

        if (randomDiceNumber == Config.DROP_CARDS_DICE_VALUE){
            textTerminal.println(THIEF_ASCII_ART);

            boolean thiefPlacementOk = false;
            while (!thiefPlacementOk) {
                Point thiefPosition = getCoordinateInput("Where should the thief be placed?");

                thiefPlacementOk = siedlerGame.placeThiefAndStealCard(thiefPosition);
                if (!thiefPlacementOk) {
                    textTerminal.println("Please choose a valid field position!");
                }
            }
        }

        super.onUpdate();
    }

    @Override
    protected void onEnd() {
        textTerminal.println("(" + currentFactionName + ") ended his dice throw phase.");

        super.onEnd();
    }
}
