package ch.zhaw.catan;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a game object that has an inventory of resources. Contains methods to add or remove resources.
 *
 * @author Besart Morina
 * @version 09.12.2022
 */
public abstract class ResourceHolder {
    protected HashMap<Config.Resource, Integer> resources;

    /**
     * Constructs a new ResourceHolder.
     *
     * @param resources specifies the initial resources.
     * @throws IllegalArgumentException if the resources parameter is null.
     */
    public ResourceHolder(HashMap<Config.Resource, Integer> resources) {
        if (resources == null){
            throw new IllegalArgumentException("Resources parameter must not be null!");
        }

        this.resources = resources;
    }

    public Map<Config.Resource, Integer> getResources() {
        return resources;
    }

    /**
     * Returns the amount of the specified resource in the inventory.
     *
     * @param key specifies the resource to be checked.
     * @return the amount of the specified resource.
     * @throws IllegalArgumentException if key parameter is null or key is invalid (not available in inventory).
     */
    public int getResource(Config.Resource key) {
        if (key == null){
            throw new IllegalArgumentException("Key parameter must not be null!");
        }
        if (!resources.containsKey(key)){
            throw new IllegalArgumentException("Key is not available! The resource does not exist in inventory.");
        }
        return resources.get(key);
    }

    /**
     * Adds resources to the resource inventory.
     *
     * @param additionalResources specifies the resources to be added.
     * @throws IllegalArgumentException if the additionalResources parameter is null.
     */
    public void addResources(Map<Config.Resource, Integer> additionalResources) {
        if (additionalResources == null){
            throw new IllegalArgumentException("AdditionalResources parameter must not be null!");
        }
        for (Map.Entry<Config.Resource, Integer> entry : additionalResources.entrySet()) {
            int current = resources.get(entry.getKey());
            resources.put(entry.getKey(), current + entry.getValue());
        }
    }

    /**
     * The resource inventory is reduced by the amount of the specified HashMap.
     *
     * @param resourcesToBeRemoved specifies the resources to be removed in a HashMap.
     * @throws IllegalArgumentException if the resourcesToBeRemoved parameter is null.
     */
    public void tryRemoveResources(Map<Config.Resource, Integer> resourcesToBeRemoved) {
        if (resourcesToBeRemoved == null){
            throw new IllegalArgumentException("ResourcesToBeRemoved must not be null!");
        }
        for (Map.Entry<Config.Resource, Integer> resourceEntry : resourcesToBeRemoved.entrySet()) {
            int stock = resources.get(resourceEntry.getKey());
            if (stock < resourceEntry.getValue()) {
                resourceEntry.setValue(stock);
                resources.put(resourceEntry.getKey(), 0);
            } else {
                resources.put(resourceEntry.getKey(), stock - resourceEntry.getValue());
            }
        }
    }

    /**
     * Checks if the inventory holds enough of the required resources.
     *
     * @param requiredResources specifies the resources to be checked.
     * @return true if the inventory has enough resources, false if not.
     * @throws IllegalArgumentException if the requiredResources parameter is null.
     */
    public boolean checkIfEnoughOfAllResources(Map<Config.Resource, Integer> requiredResources) {
        if (requiredResources == null){
            throw new IllegalArgumentException("RequiredResources must not be null!");
        }
        for (Config.Resource key : requiredResources.keySet()) {
            if (resources.get(key) < requiredResources.get(key)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if holder has enough of a specified resource.
     *
     * @param resource specifies the resource.
     * @param amount specifies the amount of resources required.
     * @return true if the inventory has enough resources, false if not.
     * @throws IllegalArgumentException if resource parameter is null.
     */
    protected boolean checkIfEnoughOfResource(Config.Resource resource, int amount) {
        if (resource == null){
            throw new IllegalArgumentException("Resource parameter must not be null!");
        }
        return amount <= resources.get(resource);
    }
}