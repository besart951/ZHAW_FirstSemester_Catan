package ch.zhaw.catan;

/**
 * Represents a player owned city. Cities are represented on the board in capital letters.
 *
 * @author Michel Fäh
 * @version 08.12.2022
 */
public class City extends Settlement {
    /**
     * Constructs a new City instance with the corresponding payout and win points.
     *
     * @param owner specifies the player who owns this city.
     */
    public City(Player owner) {
        super(owner);
        payoutFactor = 2;
        winPoints = 2;
    }

    /**
     * Constructs a new city from an existing settlement and takes the settlements' owner.
     *
     * @param settlement specifies the source settlement.
     * @return a new city instance with the same owner as settlement.
     */
    public static City fromSettlement(Settlement settlement) {
        return new City(settlement.owner);
    }

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }
}
