package ch.zhaw.catan;

import ch.zhaw.hexboard.HexBoardTextView;
import java.util.Scanner;

/**
 * Converts the SiedlerBoard into a displayable text representation.
 *
 * @author Michel Fäh
 * @version 08.12.2022
 */
public class SiedlerBoardTextView extends HexBoardTextView<Field, Settlement, Road, String> {
    private static final int COORDINATE_COLUMN_SIZE = 8;
    private static final int Y_COORDINATE_PATTERN_REPETITION = 10;

    /**
     * Constructs a new SiederBoardTextView.
     *
     * @param board specifies the board which contains the fields and their content.
     */
    public SiedlerBoardTextView(SiedlerBoard board) {
        super(board);
    }

    /**
     * Converts the board into its text representation.
     *
     * @return the board as a string representation.
     */
    @Override
    public String toString() {
        String hexboardText = super.toString();
        StringBuilder output = new StringBuilder(hexboardText.length());

        int boardTextRowSize = hexboardText.indexOf('\n');
        int boardColumCount = (boardTextRowSize / COORDINATE_COLUMN_SIZE) - 1;
        generateXCoordinateRow(output, boardColumCount);
        generateHexboardWithYCoordinates(output, hexboardText);
        return output.toString();
    }

    /**
     * Adds a x coordinate header to the StringBuilder.
     *
     * @param builder specifies the StringBuilder where the coordinate header will be added.
     * @param coordinateCount specifies how many columns of x coordinates should be added.
     * @throws IllegalArgumentException if builder parameter is null or coordinateCount is not in range.
     */
    private void generateXCoordinateRow(StringBuilder builder, int coordinateCount) {
        if (builder == null) {
            throw new IllegalArgumentException("Provided empty StringBuilder");
        }
        if (coordinateCount < 1) {
            throw new IllegalArgumentException("Invalid coordinate count. Must be more than 1");
        }
        String leftPadding = "        ";
        String xSeparator = "|       ";

        builder.append(leftPadding);

        // First add row with x coordinates
        for (int x = 0; x < coordinateCount; x++) {
            builder.append(String.format("%-8d", x));
        }
        builder.append('\n');

        // Add empty row with line separators
        builder.append(leftPadding);
        builder.append(xSeparator.repeat(coordinateCount));
        builder.append("\n\n");
    }

    /**
     * Takes an existing hexboard text and adds y coordinates to the correct text rows.
     *
     * @param builder specifies the StringBuilder where the new content will be appended to.
     * @param hexboardText specifies the hexboard text representation.
     * @throws IllegalArgumentException if builder or hexboardText parameter is null.
     */
    private void generateHexboardWithYCoordinates(StringBuilder builder, String hexboardText) {
        if (builder == null || hexboardText == null) {
            throw new IllegalArgumentException("Builder and hexboardText parameter must not be null!");
        }
        Scanner scanner = new Scanner(hexboardText);

        int rowIndex = 0;
        int yCoordinate = 0;
        while(scanner.hasNextLine()) {
            String hexboardLine = scanner.nextLine();
            if (!hexboardLine.isBlank()) {
                int hexLocalIndex = rowIndex % Y_COORDINATE_PATTERN_REPETITION;
                boolean isCoordinateRow = hexLocalIndex == 0 || hexLocalIndex == 2 || hexLocalIndex == 3 ||
                        hexLocalIndex == 5 || hexLocalIndex == 7 || hexLocalIndex == 8;
                if (isCoordinateRow) {
                    generateRowWithYCoordinate(builder, hexboardLine, yCoordinate);
                    yCoordinate++;
                } else {
                    generateNormalRow(builder, hexboardLine);
                }
                rowIndex++;
            }
        }
    }

    /**
     * Generates a single row with y coordinates added to the left of the hexboard row.
     *
     * @param builder specifies the StringBuilder where the new row will be appended to.
     * @param line specifies the string of the corresponding hexboard row.
     * @param yCoordinate specifies the y coordinate for the current row.
     * @throws IllegalArgumentException if builder parameter is null.
     */
    private void generateRowWithYCoordinate(StringBuilder builder, String line, int yCoordinate) {
        if (builder == null){
            throw new IllegalArgumentException("Builder parameter must not be null!");
        }
        builder.append(String.format("%3d----", yCoordinate))
                .append(line)
                .append('\n');
    }

    /**
     * Generates a single row with padding added to left of the hexboard instead of coordinates.
     *
     * @param builder specifies the StringBuilder where the new row will be appended to.
     * @param line specifies the string of the corresponding hexboard row.
     * @throws IllegalArgumentException if builder or line parameter is null.
     */
    private void generateNormalRow(StringBuilder builder, String line) {
        if (builder == null || line == null){
            throw new IllegalArgumentException("Builder and line parameter must not be null!");
        }
        String emptyYCoordinate = "       ";
        builder.append(emptyYCoordinate)
                .append(line)
                .append('\n');
    }
}
