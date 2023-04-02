package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.sheeps.BoardingSheep;
import org.ef3d0c3e.sheepwars.sheeps.HealerSheep;
import org.ef3d0c3e.sheepwars.sheeps.SwapSheep;

import java.util.Arrays;

public class BarbarianKit extends Kit
{
	static ItemStack ITEM;

	static
	{
		ITEM = new ItemStack(Material.STONE_SWORD);
		final ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#E15533>Barbare"));
		meta.setLore(Arrays.asList(
			"",
			"§7Améliore votre épée vous donne",
			"§7plus de moutons pour aborder"
		));
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ITEM.setItemMeta(meta);
	}

	@Override
	public String getName()
	{
		return "barbarian";
	}

	@Override
	public String getColoredName()
	{
		return Util.getColored("<#E15533>Barbare");
	}

	@Override
	public ItemStack getDisplayItem()
	{
		return ITEM;
	}

	public BarbarianKit()
	{
		super();

		sheeps.add(BoardingSheep.getItem());
		sheeps.add(BoardingSheep.getItem());
		sheeps.add(BoardingSheep.getItem());
		sheeps.add(BoardingSheep.getItem());
		sheeps.add(BoardingSheep.getItem());
		sheeps.add(SwapSheep.getItem());
		sheeps.add(SwapSheep.getItem());
		sheeps.add(SwapSheep.getItem());
		sheeps.add(SwapSheep.getItem());
		sheeps.add(SwapSheep.getItem());
		sheeps.add(HealerSheep.getItem());
		sheeps.add(HealerSheep.getItem());
		sheeps.add(HealerSheep.getItem());
	}
}
