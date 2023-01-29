package ch.zhaw.catan;

/**
 * Represents a road which is owned by a player.
 *
 * @author Michel Fäh
 * @version 08.12.2022
 */
public class Road extends PlayerOwnedStructure {
    /**
     * Constructs a new road.
     *
     * @param owner specifies the player who owns this road.
     */
    public Road(Player owner) {
        super(owner);
    }
}
