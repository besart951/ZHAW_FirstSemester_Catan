package ch.zhaw.catan;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Random;
import java.util.Collections;

import static ch.zhaw.catan.Config.MAX_CARDS_IN_HAND_NO_DROP;

/**
 * Represents a player with an inventory of resources and structures. Also includes achieved win points.
 *
 * @author Besart Morina
 * @version 26.11.2022
 */
public class Player extends ResourceHolder {
    private static final Random random = new Random();
    private static final Config.Resource[] resourceKeys = Config.Resource.values();
    private final HashMap<Config.Structure, Integer> structures;
    private final Config.Faction faction;
    private int winPoints;

    /**
     * Constructs a new player of a specific faction and with an initial inventory.
     *
     * @param faction   specifies the faction which is associated with the player.
     * @param resource  specifies the initial resources the player starts with.
     * @param structure specifies the initial structures the player starts with.
     */
    public Player(Config.Faction faction, HashMap<Config.Resource, Integer> resource, HashMap<Config.Structure, Integer> structure) {
        super(resource);
        if (faction == null || structure == null){
            throw new IllegalArgumentException("Faction and structures must not be null!");
        }
        this.faction = faction;
        this.structures = structure;
    }

    public Config.Faction getFaction() {
        return faction;
    }

    public int getWinPoints() {
        return winPoints;
    }

    /**
     * Returns the current stock of a resource.
     *
     * @param structure specifies the target resource.
     * @return the amount of the specified structure.
     * @throws IllegalArgumentException if structure is null or the specified structure is not a key of the inventory.
     */
    public int getStructureStock(Config.Structure structure){
        if (structure == null){
            throw new IllegalArgumentException("Structure parameter must not be null!");
        }
        if (!structures.containsKey(structure)){
            throw new IllegalArgumentException("Specified structure is not a key in the players inventory!");
        }

        return structures.get(structure);
    }

    /**
     * The method first checks whether a player is carrying a total of more than 7 resource cards.
     * If this is the case, half of the resources are randomly taken away and will be returned.
     * If the player is carrying less no resources will be removed.
     *
     * @return a map with the removed resources.
     */
    public Map<Config.Resource, Integer> dropHalfResources() {
        int totalResourceCount = getTotalResourceCount();
        if (totalResourceCount <= MAX_CARDS_IN_HAND_NO_DROP) {
            return Collections.emptyMap();
        }

        HashMap<Config.Resource, Integer> removedResources = Config.createEmptyResourceMap();
        int resourcesToBeRemoved = totalResourceCount / 2;
        int removedResourceCount = 0;

        while (removedResourceCount != resourcesToBeRemoved) {
            Config.Resource randomResource = resourceKeys[random.nextInt(resourceKeys.length)];

            if (resources.containsKey(randomResource)) {
                int resourceStock = resources.get(randomResource);
                int currentRemoveCount = removedResources.get(randomResource);

                if (resourceStock > 0 && (resourceStock > currentRemoveCount)) {
                    resources.put(randomResource, resourceStock - 1);
                    removedResources.put(randomResource, currentRemoveCount + 1);
                    removedResourceCount += 1;
                }
            }
        }

        return removedResources;
    }

    /**
     * Removes one random resource from the player.
     *
     * @return the removed resource or null if the player has no resources.
     */
    public Config.Resource stealRandomResource() {
        // Make a list of resources which the player owns one or more of
        List<Config.Resource> nonEmptyResources = new ArrayList<>(resources.size());

        for (Map.Entry<Config.Resource, Integer> resource : resources.entrySet()) {
            if (resource.getValue() > 0){
                nonEmptyResources.add(resource.getKey());
            }
        }

        if (nonEmptyResources.isEmpty()) {
            return null;
        }

        int resourceIndex = new Random().nextInt(nonEmptyResources.size());
        Config.Resource targetResource = nonEmptyResources.get(resourceIndex);
        resources.put(targetResource, resources.get(targetResource) - 1);
        return targetResource;
    }

    /**
     * Adds a single resource to the players inventory.
     *
     * @param resource specifies the resource to be added.
     * @throws IllegalArgumentException if the resource parameter is null.
     */
    public void addResource(Config.Resource resource) {
        if (resource == null){
            throw new IllegalArgumentException("Resource parameter must not be null!");
        }

        resources.put(resource, resources.get(resource) + 1);
    }

    /**
     * Adds an amount of points to the players win points.
     *
     * @param amount specifies the number of points to be added. Argument needs to be positive.
     * @throws IllegalArgumentException if parameter value is negative or addition exceeds the
     * maximum limit of an integer.
     */
    public void addWinPoints(int amount) {
        if (amount < 0){
            throw new IllegalArgumentException("Can't add a negative number! " +
                    "Use removeWinPoints to remove points instead.");
        }
        if (amount > Integer.MAX_VALUE - winPoints){
            throw new IllegalArgumentException("Addition must not exceed maximum limit of an Integer!");
        }

        winPoints += amount;
    }

    /**
     * Subtracts an amount of points from the players win points.
     *
     * @param amount specifies the number of points to be subtracted. Argument needs to be positive.
     * @throws IllegalArgumentException if the amount value is negative or if more points are deducted from the
     * player than he owns.
     */
    public void removeWinPoints(int amount){
        if (amount < 0){
            throw new IllegalArgumentException("Can't remove a negative number! " +
                    "Use addWinPoints to add points instead.");
        }
        if (winPoints - amount < 0){
            throw new IllegalArgumentException("More points were deducted than the player owns!");
        }

        winPoints -= amount;
    }

    /**
     * Adds a specific structure to the players inventory.
     *
     * @param structure specifies the structure to be added.
     * @throws IllegalArgumentException if structure parameter is null or the maximum amount of structures was exceeded.
     */
    public void addStructure(Config.Structure structure){
        if (structure == null){
            throw new IllegalArgumentException("Structure parameter must not be null!");
        }
        if (structures.get(structure) + 1 > structure.getStockPerPlayer()){
            throw new IllegalArgumentException("Adding the structure must not exceed the maximum " +
                    "value given to the player at the beginning of the game!");
        }

        structures.put(structure, structures.get(structure) + 1);
    }

    /**
     * Removes a specific structure from the players inventory (if available).
     *
     * @param structure specifies the structure to be removed.
     * @throws IllegalArgumentException if the structure parameter is null or if the reduction would
     * set the amount below the minimum (0).
     */
    public void removeStructure(Config.Structure structure){
        if (structure == null) {
            throw new IllegalArgumentException("Structure parameter must not be null!");
        }
        if (structures.get(structure) - 1 < 0){
            throw new IllegalArgumentException("Removing the structure must not exceed the minimum (0)!");
        }

        structures.put(structure, structures.get(structure) - 1);
    }

    /**
     * Checks if a specific structure is available.
     *
     * @param structure specifies the structure to check if available.
     * @return true if available, false if not.
     * @throws IllegalArgumentException if the structure parameter is null or the inventory does not
     * contain the parameter key.
     */
    public boolean checkIfStructureAvailable(Config.Structure structure){
        if (structure == null){
            throw new IllegalArgumentException("Structure parameter must not be null");
        }
        if (!structures.containsKey(structure)){
            throw new IllegalArgumentException("Player does not have such a structure in the inventory!");
        }

        return structures.get(structure) > 0;
    }

    /**
     * Counts the absolute number of resources in the inventory.
     *
     * @return the absolute amount of resources in the inventory.
     */
    private int getTotalResourceCount() {
        int totalResourceCount = 0;
        for (int resourceCount : resources.values()) {
            totalResourceCount += resourceCount;
        }

        return totalResourceCount;
    }
}
