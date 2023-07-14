package org.ef3d0c3e.sheepwars.items;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

/**
 * Class used to register custom items
 */
public class ItemRegistry
{
	private final HashMap<UUID, ItemBase> registry;

	/**
	 * Gets custom item from itemstack
	 * @param item Item
	 * @return Custom item (null for normal item)
	 */
	public @Nullable
	ItemBase getItem(final ItemStack item)
	{
		final UUID id = ItemBase.getId(item);
		if (id == null)
			return null;

		return registry.get(id);
	}

	/**
	 * Registers custom item to registry
	 * @param item Item to register
	 */
	public void registerItem(final ItemBase item)
	{
		registry.put(item.getId(), item);
	}

	public ItemRegistry()
	{
		registry = new HashMap<>();
	}
}
