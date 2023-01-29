package ch.zhaw.catan.gamephases;

import ch.zhaw.catan.Config;
import ch.zhaw.catan.SiedlerGame;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.awt.Point;

/**
 * This abstract plan of a game phase implements the basic functionality of the game phases. Each game phase consists
 * of a start method, which is called once and can be used for setup or phase preparation. An update method, which
 * contains the main part of the game phase logic e.g a loop that lasts until the player wants to move to the next
 * phase. And an end method, which is called when the phase is exited and moves to the next one.
 *
 * @author Louie Wolf
 * @version 07.12.2022
 */
public abstract class GamePhase {
    protected TextIO textIO;
    protected TextTerminal<?> textTerminal;
    protected SiedlerGame siedlerGame;
    protected GamePhase nextPhase;
    protected String currentFactionName;

    /**
     * Default initialization for all game phases.
     *
     * @param textIO specifies the text input/output.
     * @param textTerminal specifies in which terminal the output is displayed.
     * @param siedlerGame specifies the game this phase is part of.
     * @throws IllegalArgumentException if one of the parameters is null.
     */
    public GamePhase(TextIO textIO, TextTerminal<?> textTerminal, SiedlerGame siedlerGame){
        if (textIO == null || textTerminal == null || siedlerGame == null){
            throw new IllegalArgumentException("No parameter of a game phase may be zero.");
        }

        this.textIO = textIO;
        this.textTerminal = textTerminal;
        this.siedlerGame = siedlerGame;
    }

    /**
     * Initializes the next phase which will be executed after exiting this phase.
     *
     * @param nextPhase specifies the next game phase.
     * @throws IllegalArgumentException if the parameter is null.
     */
    final public void initializeNextPhase(GamePhase nextPhase){
        if (nextPhase == null){
            throw new IllegalArgumentException("The next phase cannot be zero.");
        }

        this.nextPhase = nextPhase;
    }

    /**
     * This method is the entry point of this game phase. It is called once in the beginning and switches
     * to onUpdate after being finished.
     */
    public void onStart(){
        currentFactionName = siedlerGame.getCurrentPlayerFaction().toString();
        onUpdate();
    }

    /**
     * This method contains the behaviour of the game phase. After being finished it switches to the onEnd method.
     */
    protected void onUpdate(){
        onEnd();
    }

    /**
     * This method is the exit of this game phase. It is called once at the end and executes the onStart method
     * of the next game phase.
     */
    protected void onEnd(){
        nextPhase.onStart();
    }

    /**
     * This method allows the player to select one of several options. The options are defined by the passed enum.
     *
     * @param textIO specifies where the text is printed.
     * @param options specifies the enum which contains the different options to choose from.
     * @param <T> specifies the class where the enum is defined.
     * @return the players chosen enum value.
     * @throws IllegalArgumentException if one of the parameters is null.
     */
    protected static <T extends Enum<T>> T getEnumValue(TextIO textIO, Class<T> options, String message) {
        if (textIO == null || options == null || message == null){
            throw new IllegalArgumentException("TextIO, options and message must not be null!");
        }
        return textIO.newEnumInputReader(options).read(message);
    }

    /**
     * Method to generate a two-dimensional (x,y) point from user input. The entered coordinate must be within the
     * settler board coordinate span.
     *
     * @param message specifies the message that is displayed to the player when he is asked to enter the coordinates.
     * @return the generated point.
     * @throws IllegalArgumentException if the message is null.
     */
    protected Point getCoordinateInput(String message) {
        if (message == null){
            throw new IllegalArgumentException("Message must not be null!");
        }
        textTerminal.println(message);
        int x = textIO.newIntInputReader().
                withMinVal(Config.MIN_COORDINATE_VALUE).withMaxVal(Config.MAX_X_COORDINATE_VALUE).
                read("X coordinate:");
        int y = textIO.newIntInputReader()
                .withMinVal(Config.MIN_COORDINATE_VALUE).withMaxVal(Config.MAX_Y_COORDINATE_VALUE)
                .read("Y coordinate:");
        return new Point(x, y);
    }
}