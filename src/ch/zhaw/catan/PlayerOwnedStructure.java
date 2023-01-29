package ch.zhaw.catan;

/**
 * Represents structures on the SiedlerBoard and links them to the owing player.
 *
 * @author Michel Fäh
 * @version 27.11.2022
 */
public abstract class PlayerOwnedStructure {
    protected final Player owner;

    /**
     * Constructs a new PlayerOwnedStructure.
     *
     * @param owner specifies the player who owns this structure.
     * @throws IllegalArgumentException if owner parameter is null.
     */
    public PlayerOwnedStructure(Player owner) {
        if (owner == null){
            throw new IllegalArgumentException("Player parameter must not be null!");
        }
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    /**
     * Checks if the structure is owned by target player.
     *
     * @param player specifies the player to check against.
     * @return true if structure is owned by player otherwise false.
     * @throws IllegalArgumentException if player parameter is null.
     */
    public boolean isOwnedBy(Player player) {
        if (player == null){
            throw new IllegalArgumentException("Player parameter must not be null!");
        }
        return owner.getFaction() == player.getFaction();
    }

    /**
     * Returns text representation of the player who owns this structure.
     *
     * @return a string of the players' faction.
     */
    @Override
    public String toString() {
        return owner.getFaction().toString();
    }

}
