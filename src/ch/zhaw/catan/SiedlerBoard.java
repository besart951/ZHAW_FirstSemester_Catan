package ch.zhaw.catan;

import ch.zhaw.catan.Config.Land;
import ch.zhaw.hexboard.HexBoard;
import ch.zhaw.hexboard.Label;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.zhaw.catan.Config.INITIAL_THIEF_POSITION;
import static ch.zhaw.catan.Config.MIN_DICE_VALUE;

/**
 * Implements and extends the functionality provided by HexBoard.
 *
 * @author Michel Fäh
 * @version 09.12.2022
 */
public class SiedlerBoard extends HexBoard<Field, Settlement, Road, String> {
    private Point thiefPosition = INITIAL_THIEF_POSITION;
    private final SiedlerBoardTextView textView = new SiedlerBoardTextView(this);
    private final CardBank bank;

    /**
     * Constructs a new SiedlerBoard.
     *
     * @param bank specifies the CardBank in which to trade, dispense and distribute resources.
     * @throws IllegalArgumentException if bank is null.
     */
    public SiedlerBoard(CardBank bank) {
        if (bank == null) {
            throw new IllegalArgumentException("CardBank parameter must not be null!");
        }
        this.bank = bank;
        initialBoardSetup();
    }

    /**
     * Sets the thief to a specified position.
     *
     * @param fieldPosition specifies the position of the field where the thief should be placed.
     * @return true if thief was successfully set to position, false otherwise.
     * @throws IllegalArgumentException if field position is null.
     */
    public boolean setThiefPosition(Point fieldPosition) {
        if (fieldPosition == null){
            throw new IllegalArgumentException("Point parameter must not be null!");
        }
        if (!hasField(fieldPosition)) {
            return false;
        }

        Field newThief = getField(fieldPosition);
        if (newThief.getLand() == Land.WATER) {
            return false;
        }

        getField(thiefPosition).setThief(false);
        newThief.setThief(true);
        thiefPosition = fieldPosition;
        return true;
    }

    /**
     * Returns the fields associated with the specified dice value.
     * Only returns fields which are not water or have a thief on them.
     *
     * @param dice specifies the dice value.
     * @return the valid fields associated with the dice value.
     * @throws IllegalArgumentException if dice value is not in valid range.
     */
    public List<Point> getFieldsForDiceValue(int dice) {
        if (dice < Config.MIN_DICE_VALUE || dice > Config.MAX_DICE_VALUE){
            throw new IllegalArgumentException("Dice value must be in valid range of(" + Config.MIN_DICE_VALUE +
                    "-" + Config.MAX_DICE_VALUE + ") !");
        }
        List<Point> allFieldPositions = getFields();

        List<Point> fieldsPositionsForDice = new ArrayList<>(allFieldPositions.size());
        for (Point fieldPosition: allFieldPositions) {
            Field field = getField(fieldPosition);
            if (field.getDiceValue() == dice && !field.hasThief() && field.getLand() != Land.WATER) {
                fieldsPositionsForDice.add(fieldPosition);
            }
        }

        return fieldsPositionsForDice;
    }

    /**
     * Returns the {@link Land}s adjacent to the specified corner.
     *
     * @param corner specifies the corner.
     * @return the list with the adjacent {@link Land}s
     */
    public List<Land> getLandsForCorner(Point corner) {
        List<Field> fields = getFields(corner);
        if (fields.isEmpty()) {
            return Collections.emptyList();
        }

        List<Land> lands = new ArrayList<>(fields.size());
        for (Field field: fields) {
            lands.add(field.getLand());
        }
        return lands;
    }

    /**
     * Returns the amount of {@link Config.Resource}s each {@link Config.Faction} gets for the dice throw.
     *
     * @param dicethrow specifies the number which was thrown.
     * @return the list with the {@link Config.Faction}s and the number of {@link Config.Resource}s they get.
     * @throws IllegalArgumentException if the provided faction argument is null or empty.
     */
    public Map<Config.Faction, HashMap<Config.Resource, Integer>>
    getResourcesToPayPerFaction(Integer dicethrow, List<Config.Faction> factions) {
        if (factions == null || factions.isEmpty()){
            throw new IllegalArgumentException("Faction list must not be null or empty!");
        }

        Map<Config.Faction, HashMap<Config.Resource, Integer>> payout
                = Config.createEmptyResourcePerFactionMap(factions);

        List<Point> targetFieldPositions = getFieldsForDiceValue(dicethrow);
        for (Point targetFieldPosition: targetFieldPositions) {
            Field field = getField(targetFieldPosition);
            Land land = field.getLand();
            Config.Resource fieldResource = land.getResource();

            List<Settlement> fieldCornerStructures = getCornersOfField(targetFieldPosition);
            Map<Config.Faction, HashMap<Config.Resource, Integer>> fieldPayoutPerFaction
                    = Config.createEmptyResourcePerFactionMap(factions);

            int totalResource = getFieldPayoutPerFaction(fieldResource, fieldCornerStructures, fieldPayoutPerFaction);
            Config.Faction exclusiveFieldOwner = getExclusiveFieldFaction(fieldCornerStructures);

            if (bank.checkIfEnoughOfResource(fieldResource, totalResource)) {
                mergeFieldPayoutInto(fieldResource, fieldPayoutPerFaction, payout);
            } else if (exclusiveFieldOwner != null) {
                addPartialResourcePayout(payout, exclusiveFieldOwner, fieldResource, totalResource);
            }
        }
        return payout;
    }

    /**
     * Generate the text representation of the board.
     *
     * @return String representing the SiedlerBoard.
     */
    @Override
    public String toString() {
        return this.textView.toString();
    }

    /**
     * Adds the maximal possible amount of the requested resources to the payout.
     *
     * @param payout specifies the destination payout map.
     * @param faction specifies the faction which should receive the payout.
     * @param resource specifies the resource type of the payout.
     * @param requestedPayout specifies the maximal requested payout which might only be partially paid.
     */
    private void addPartialResourcePayout(
        Map<Config.Faction, HashMap<Config.Resource, Integer>> payout,
        Config.Faction faction,
        Config.Resource resource,
        int requestedPayout) {
        int available = bank.checkResourceCountAvailability(resource, requestedPayout);
        HashMap<Config.Resource, Integer> factionPayout = payout.get(faction);
        int currentResourceValue = factionPayout.get(resource);
        factionPayout.put(resource, currentResourceValue + available);
    }

    /**
     * Merges the field specific payout into a general faction payout.
     *
     * @param resource specifies the target resource to be merged.
     * @param fieldPayoutPerFaction specifies the field specific payout.
     * @param payout specifies the destination for the payout merge.
     */
    private void mergeFieldPayoutInto(
        Config.Resource resource,
        Map<Config.Faction, HashMap<Config.Resource, Integer>> fieldPayoutPerFaction,
        Map<Config.Faction, HashMap<Config.Resource, Integer>> payout) {
        for (Map.Entry<Config.Faction, HashMap<Config.Resource, Integer>> fieldPayoutFaction : payout.entrySet()) {
            int currentResourceValue = fieldPayoutFaction.getValue().get(resource);
            int newResourceValue = currentResourceValue + fieldPayoutPerFaction.get(fieldPayoutFaction.getKey()).get(resource);
            fieldPayoutFaction.getValue().put(resource, newResourceValue);
        }
    }

    /**
     * Checks if the corner structures of the target field are all owned by the same faction.
     *
     * @param fieldCornerStructures specifies the list of corner structures of the target field.
     * @return if corner structures are all owned by the same faction, returns that faction, null otherwise.
     * @throws IllegalArgumentException if fieldCornerStructures is null.
     */
    private Config.Faction getExclusiveFieldFaction(List<Settlement> fieldCornerStructures) {
        if (fieldCornerStructures == null) {
            throw new IllegalArgumentException("FieldCornerStructures parameter must not be null");
        }

        if (fieldCornerStructures.isEmpty()) {
            return null;
        }

        Config.Faction firstFaction = fieldCornerStructures.get(0).getOwner().getFaction();
        int sameFactionCount = 1;
        for (int i = 1; i < fieldCornerStructures.size(); i++) {
            if (firstFaction == fieldCornerStructures.get(i).getOwner().getFaction()) {
                sameFactionCount++;
            }
        }

        if (sameFactionCount != fieldCornerStructures.size()) {
            return null;
        }
        return firstFaction;
    }

    /**
     * Calculates the resource payout for a field.
     *
     * @param resource specifies the fields' resource.
     * @param fieldCornerStructures specifies the corner structures of the target field.
     * @param payout specifies the map which contains the payout result for each faction.
     * @return the total payout amount over all factions for this field resource.
     * @throws IllegalArgumentException if payout parameter is null or empty.
     */
    private int getFieldPayoutPerFaction(
        Config.Resource resource,
        List<Settlement> fieldCornerStructures,
        Map<Config.Faction, HashMap<Config.Resource, Integer>> payout) {
        if (payout == null || payout.isEmpty()) {
            throw new IllegalArgumentException("Field payout map must not be null and must have predefined keys " +
                    "for each faction");
        }

        int fieldTotalPayout = 0;
        for (Settlement structure: fieldCornerStructures) {
            Config.Faction ownerFaction = structure.getOwner().getFaction();
            Map<Config.Resource, Integer> fieldPayout = payout.get(ownerFaction);

            int newPayout = fieldPayout.get(resource) + structure.getPayoutFactor();
            fieldPayout.put(resource, newPayout);
            fieldTotalPayout += structure.getPayoutFactor();
        }
        return fieldTotalPayout;
    }

    /**
     * Adds all the fields, their dice value and sets the thief to its initial position.
     */
    private void initialBoardSetup() {
        Map<Point, Integer> diceNumbers = Config.getStandardDiceNumberPlacement();
        Map<Point, Land> landPlacements = Config.getStandardLandPlacement();

        for (Map.Entry<Point, Land> land: landPlacements.entrySet()) {
            Point landPosition = land.getKey();

            int diceValue = 0;
            if (diceNumbers.containsKey(landPosition)) {
                diceValue = diceNumbers.get(landPosition);
            };

            addField(land.getKey(), new Field(land.getValue(), diceValue));

            if (diceValue != 0) {
                textView.setLowerFieldLabel(landPosition, diceToLabel(diceValue));
            }
        }
        setThiefPosition(INITIAL_THIEF_POSITION);
    }

    /**
     * Converts dice value into a label.
     *
     * @param dice specifies the value of the dice.
     * @return the instance of a label constructed with the correct dice value.
     */
    private Label diceToLabel(int dice) {
        char tens = ' ';
        if (dice > 9) {
            int firstDigit = dice / 10;
            tens = (char)(firstDigit + '0');
        }
        char ones = (char)(dice % 10 + '0');
        return new Label(tens, ones);
    }
}
