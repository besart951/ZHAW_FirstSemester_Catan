package ch.zhaw.catan;

/**
 * Represents a settlement and contains information about the owner, its payout factor and win points.
 *
 * @author Michel Fäh
 * @version 08.12.2022
 */
public class Settlement extends PlayerOwnedStructure {
    protected int payoutFactor;
    protected int winPoints;

    /**
     * Constructs a new settlement instance with the corresponding payout and win points.
     *
     * @param owner specifies the player who owns this settlement.
     */
    public Settlement(Player owner) {
        super(owner);
        payoutFactor = 1;
        winPoints = 1;
    }

    public int getPayoutFactor() {
        return payoutFactor;
    }

    public int getWinPointAmount() {
        return winPoints;
    }
}
