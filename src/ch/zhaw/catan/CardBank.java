package ch.zhaw.catan;

import java.util.HashMap;

/**
 * The card bank is the central location of all resources that are not owned by the players.
 * Resources from the bank are used for the distribution of resources or for trading with the player.
 * Resources that must be discarded when rolling a 7 are stored here as well.
 *
 * @author Besart Morina, Michel Fäh
 * @version 08.12.2022
 */
public class CardBank extends ResourceHolder {
    /**
     * Constructs a new CardBank instance.
     *
     * @param initialResources specifies the initial resources.
     */
    public CardBank(HashMap<Config.Resource, Integer> initialResources) {
        super(initialResources);
    }

    /**
     * Checks how much of the requested resource the bank is able to pay.
     *
     * @param resource specifies the resource to check (if available).
     * @param requestedAmount specifies the requested amount of resources required.
     * @return the available resources which is the maximum payout the bank can afford.
     * @throws IllegalArgumentException if resource parameter is null.
     */
    public int checkResourceCountAvailability(Config.Resource resource, int requestedAmount) {
        if (resource == null){
            throw new IllegalArgumentException("Resource parameter must not be null!");
        }
        int stock = resources.get(resource);
        return Math.min(stock, requestedAmount);
    }
}
