package ch.zhaw.catan;

import ch.zhaw.catan.gamephases.GamePhase;
import ch.zhaw.catan.gamephases.InitialPhase;
import ch.zhaw.catan.gamephases.DiceRollPhase;
import ch.zhaw.catan.gamephases.BuildAndTradePhase;
import ch.zhaw.catan.gamephases.EndPhase;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;

/**
 * This class provides all functionality needed to start a new game of Catan.
 *
 * @author Louie Wolf
 * @version 25.11.2022
 */
public class Program {
    GamePhase startPhase;

    /**
     * Starts the program by setting it up and executing the onStart method of the start game phase.
     */
    public void run(){
        setup();
        startPhase.onStart();
    }

    private void setup(){
        TextIO textIO = TextIoFactory.getTextIO();
        TextTerminal<?> textTerminal = textIO.getTextTerminal();

        textTerminal.println(
                """
                          _____ _          _ _                                 _____      _             \s
                         / ____(_)        | | |                               / ____|    | |            \s
                        | (___  _  ___  __| | | ___ _ __  __   _____  _ __   | |     __ _| |_ __ _ _ __ \s
                         \\___ \\| |/ _ \\/ _` | |/ _ \\ '__| \\ \\ / / _ \\| '_ \\  | |    / _` | __/ _` | '_ \\\s
                         ____) | |  __/ (_| | |  __/ |     \\ V / (_) | | | | | |___| (_| | || (_| | | | |
                        |_____/|_|\\___|\\__,_|_|\\___|_|      \\_/ \\___/|_| |_|  \\_____\\__,_|\\__\\__,_|_| |_|
                                                                                                        \s
                                                                                                        \s""".indent(1)
        );

        int winPoints = textIO.newIntInputReader()
                .withMinVal(Config.MIN_WIN_POINTS)
                .withMaxVal(Config.MAX_WIN_POINTS)
                .read("What do you want to set your win points to?");

        int numberOfPlayers = textIO.newIntInputReader()
                .withMinVal(Config.MIN_NUMBER_OF_PLAYERS)
                .withMaxVal(Config.MAX_NUMBER_OF_PLAYERS)
                .read("With how many players do you want to play?");

        SiedlerGame siedlerGame = new SiedlerGame(winPoints, numberOfPlayers);

        InitialPhase initialPhase = new InitialPhase(textIO, textTerminal, siedlerGame);
        EndPhase endPhase = new EndPhase(textIO, textTerminal, siedlerGame);
        DiceRollPhase diceRollPhase = new DiceRollPhase(textIO, textTerminal, siedlerGame);
        BuildAndTradePhase buildAndTradePhase = new BuildAndTradePhase(textIO, textTerminal, siedlerGame, endPhase);

        initialPhase.initializeNextPhase(diceRollPhase);
        diceRollPhase.initializeNextPhase(buildAndTradePhase);
        buildAndTradePhase.initializeNextPhase(diceRollPhase);

        startPhase = initialPhase;
    }

    public static void main(String[] args) {
        Program program = new Program();
        program.run();
    }
}
