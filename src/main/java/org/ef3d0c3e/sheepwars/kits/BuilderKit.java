package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.Util;

import java.util.Arrays;

public class BuilderKit extends Kit
{
	static ItemStack ITEM;

	static
	{
		ITEM = new ItemStack(Material.BRICKS);
		final ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#C9BEAC>Maçon"));
		meta.setLore(Arrays.asList(
			"",
			"§7Vous obtenez des blocs",
			"§7pour construire"
		));
		ITEM.setItemMeta(meta);
	}

	public int streak = 0;

	@Override
	public String getName()
	{
		return "builder";
	}

	@Override
	public String getColoredName()
	{
		return Util.getColored("<#C9BEAC>Maçon");
	}

	@Override
	public ItemStack getDisplayItem()
	{
		return ITEM;
	}

	public BuilderKit()
	{
		super();
	}
}
