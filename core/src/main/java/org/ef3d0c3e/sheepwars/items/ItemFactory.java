package org.ef3d0c3e.sheepwars.items;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

/**
 * Class used to register custom items
 */
public class ItemFactory
{
    protected static HashMap<UUID, IItem> registry = new HashMap<>();

    /**
     * Gets custom item from itemstack
     * @param item Item
     * @return Custom item (null for normal item)
     */
    public static @Nullable IItem getItem(final ItemStack item)
    {
        final UUID id = IItem.getId(item);
        if (id == null)
            return null;

        return registry.get(id);
    }

    /**
     * Registers custom item to registry
     * @param item Item to register
     */
    public static void registerItem(final IItem item)
    {
        registry.put(item.getId(), item);
    }
}
