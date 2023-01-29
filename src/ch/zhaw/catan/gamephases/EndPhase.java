package ch.zhaw.catan.gamephases;

import ch.zhaw.catan.Config;
import ch.zhaw.catan.SiedlerGame;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextTerminal;

import java.util.HashMap;
import java.util.Map;

/**
 * The end game phase is the final phase of the game. Here the players have the possibility to display their win points,
 * or they can quit the application.
 *
 * @author Louie Wolf
 * @version 07.12.2022
 */
public class EndPhase extends GamePhase{
    private static final String HAS_WON_ASCII_ART = """
             _    _             _____\s
            | |  | |    /\\     / ____|
            | |__| |   /  \\   | (___ \s
            |  __  |  / /\\ \\   \\___ \\\s
            | |  | | / ____ \\  ____) |
            |_|  |_|/_/    \\_\\|_____/\s
            __          __  ____   _   _\s
            \\ \\        / / / __ \\ | \\ | |
             \\ \\  /\\  / / | |  | ||  \\| |
              \\ \\/  \\/ /  | |  | || . ` |
               \\  /\\  /   | |__| || |\\  |
                \\/  \\/     \\____/ |_| \\_|
                                     \s""";

    public enum Actions {
        SHOW_POINTS,
        QUIT
    }

    /**
     * Constructs a new end game phase.
     *
     * @param textIO specifies the text input/output.
     * @param textTerminal specifies in which terminal the text output is displayed.
     * @param siedlerGame specifies the game this phase is part of.
     */
    public EndPhase(TextIO textIO, TextTerminal<?> textTerminal, SiedlerGame siedlerGame) {
        super(textIO, textTerminal, siedlerGame);
    }

    /**
     * If there is a winner, a victory message will be issued.
     * Then executes the method of the abstract class.
     */
    @Override
    public void onStart(){
        textTerminal.println("The game has ended!");
        tryPrintWinner();

        super.onStart();
    }

    /**
     * Gives players the option to display their points or quit the game.
     * Then executes the method of the abstract class.
     */
    @Override
    public void onUpdate(){
        boolean endGame = false;
        while (!endGame) {
            switch (getEnumValue(textIO, EndPhase.Actions.class, "What would you like to do")) {
                case SHOW_POINTS -> showPoints();
                case QUIT -> endGame = true;
                default -> throw new IllegalStateException("Internal error found - Command not implemented.");
            }
        }

        super.onUpdate();
    }

    @Override
    public void onEnd(){
        textTerminal.println("Shutting down...");
        textIO.dispose();
    }

    /**
     * Displays the final win points of all players.
     */
    private void showPoints(){
        HashMap<Config.Faction,Integer> scores = siedlerGame.getScoreboard();
        for (Map.Entry<Config.Faction,Integer> entry: scores.entrySet()){
            textTerminal.println("Player: (" + entry.getKey().toString() + ") scored " + entry.getValue() + " points.");
        }
    }

    /**
     * Checks if there is a winner. If there is one, a victory message is displayed.
     */
    private void tryPrintWinner(){
        Config.Faction winner = siedlerGame.getWinner();
        if (winner == null){
            return;
        }

        printWinnerSymbol(winner);
    }

    /**
     * Displays the winner's victory message.
     *
     * @param winner specifies the winner faction.
     * @throws IllegalArgumentException if winner parameter is null.
     */
    private void printWinnerSymbol(Config.Faction winner){
        if (winner == null){
            throw new IllegalArgumentException("Winner parameter must not be null!");
        }

        String winnerString;
        switch (winner){
            case RED -> winnerString = """
                     _____   ______  _____ \s
                    |  __ \\ |  ____||  __ \\\s
                    | |__) || |__   | |  | |
                    |  _  / |  __|  | |  | |
                    | | \\ \\ | |____ | |__| |
                    |_|  \\_\\|______||_____/\s""";
            case BLUE -> winnerString = """
                     ____   _       _    _  ______\s
                    |  _ \\ | |     | |  | ||  ____|
                    | |_) || |     | |  | || |__  \s
                    |  _ < | |     | |  | ||  __| \s
                    | |_) || |____ | |__| || |____\s
                    |____/ |______| \\____/ |______|""";
            case GREEN -> winnerString = """
                      _____  _____   ______  ______  _   _\s
                     / ____||  __ \\ |  ____||  ____|| \\ | |
                    | |  __ | |__) || |__   | |__   |  \\| |
                    | | |_ ||  _  / |  __|  |  __|  | . ` |
                    | |__| || | \\ \\ | |____ | |____ | |\\  |
                     \\_____||_|  \\_\\|______||______||_| \\_|""";
            case YELLOW -> winnerString = """
                    __     __ ______  _       _        ____  __          __
                    \\ \\   / /|  ____|| |     | |      / __ \\ \\ \\        / /
                     \\ \\_/ / | |__   | |     | |     | |  | | \\ \\  /\\  / /\s
                      \\   /  |  __|  | |     | |     | |  | |  \\ \\/  \\/ / \s
                       | |   | |____ | |____ | |____ | |__| |   \\  /\\  /  \s
                       |_|   |______||______||______| \\____/     \\/  \\/   \s""";
            default -> winnerString = winner.toString();
        }

        textTerminal.println(winnerString);
        textTerminal.println(HAS_WON_ASCII_ART);
    }
}
