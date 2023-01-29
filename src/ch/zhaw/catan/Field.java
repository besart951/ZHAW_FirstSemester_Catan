package ch.zhaw.catan;

/**
 * Represents a hex field on the SiedlerBoard and holds additional information about the current state of the field.
 * This information includes the dice value, if the thief is currently on this field and land information.
 *
 * @author Michel Fäh
 * @version 08.12.2022
 */
public class Field {
    private static final String THIEF_LABEL = "TH";
    private final Config.Land land;
    private final int diceValue;
    private boolean hasThief;

    /**
     * Constructs a new Field instance.
     *
     * @param land specifies the associated land which holds the field resource type.
     * @param diceValue specifies the dice value associated with this field which is used to during the dice throw.
     */
    public Field(Config.Land land, int diceValue) {
        if (land == null){
            throw new IllegalArgumentException("Land parameter must not be null!");
        }
        this.land = land;
        this.diceValue = diceValue;
    }

    /**
     * Sets or removes the thief from this field. It is important to note that
     * it is the callers' responsibility to make sure no other field has the thief set.
     *
     * @param hasThief true if the thief should be set, false if he should be removed.
     */
    public void setThief(boolean hasThief) {
        this.hasThief = hasThief;
    }

    /**
     * Returns the value whether the thief is placed on this field or not.
     *
     * @return true if thief is placed on this field, false otherwise.
     */
    public boolean hasThief() {
        return hasThief;
    }

    public int getDiceValue() {
        return diceValue;
    }

    public Config.Land getLand() {
        return land;
    }

    /**
     * Outputs the fields current label to be displayed.
     *
     * @return either the fields' resource as a string or the thief label.
     */
    @Override
    public String toString() {
        if (hasThief) {
            return THIEF_LABEL;
        } else {
            return land.toString();
        }
    }
}
