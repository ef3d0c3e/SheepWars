package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.sheeps.*;

import java.util.ArrayList;
import java.util.Arrays;

public class MageKit extends Kit
{
	static ItemStack ITEM;

	static
	{
		ITEM = new ItemStack(Material.BLAZE_ROD);
		final ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#275FFF>Mage"));
		meta.setLore(Arrays.asList(
			"",
			"§7Augmente vos chances d'obtenir",
			"§7des moutons offensifs"
		));
		ITEM.setItemMeta(meta);
	}

	@Override
	public String getName()
	{
		return "mage";
	}

	@Override
	public String getColoredName()
	{
		return Util.getColored("<#275FFF>Mage");
	}

	@Override
	public ItemStack getDisplayItem()
	{
		return ITEM;
	}


	@Override
	public double getAdditionalSheepChance()
	{
		return 0.3;
	}

	public MageKit()
	{
		super();

		sheeps.add(ExplosiveSheep.getItem());
		sheeps.add(ExplosiveSheep.getItem());
		sheeps.add(ExplosiveSheep.getItem());
		sheeps.add(ExplosiveSheep.getItem());
		sheeps.add(ExplosiveSheep.getItem());
		sheeps.add(ExplosiveSheep.getItem());
		sheeps.add(FragmentationSheep.getItem());
		sheeps.add(FragmentationSheep.getItem());
		sheeps.add(FragmentationSheep.getItem());
		sheeps.add(IncendiarySheep.getItem());
		sheeps.add(IncendiarySheep.getItem());
		sheeps.add(IncendiarySheep.getItem());
		sheeps.add(IncendiarySheep.getItem());
		sheeps.add(EarthQuakeSheep.getItem());
		sheeps.add(EarthQuakeSheep.getItem());
		sheeps.add(EarthQuakeSheep.getItem());
		sheeps.add(DistortionSheep.getItem());
		sheeps.add(DistortionSheep.getItem());
		sheeps.add(DistortionSheep.getItem());
		sheeps.add(LightningSheep.getItem());
		sheeps.add(LightningSheep.getItem());
	}
}
