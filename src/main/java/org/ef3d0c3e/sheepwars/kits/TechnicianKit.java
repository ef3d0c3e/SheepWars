package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.sheeps.*;

import java.util.Arrays;

public class TechnicianKit extends Kit
{
	static ItemStack ITEM;

	static
	{
		ITEM = new ItemStack(Material.LEVER);
		final ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#61B36C>Technicien"));
		meta.setLore(Arrays.asList(
			"",
			"§7Augmente vos chances d'obtenir",
			"§7des moutons technique"
		));
		ITEM.setItemMeta(meta);
	}

	@Override
	public String getName()
	{
		return "technician";
	}

	@Override
	public String getColoredName()
	{
		return Util.getColored("<#61B36C>Technicien");
	}

	@Override
	public ItemStack getDisplayItem()
	{
		return ITEM;
	}


	@Override
	public double getAdditionalSheepChance()
	{
		return 0.1;
	}

	public TechnicianKit()
	{
		super();

		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		sheeps.add(SeekerSheep.getItem());
		//sheeps.add(RemoteSheep.getItem());
		//sheeps.add(RemoteSheep.getItem());
		sheeps.add(LightningSheep.getItem());
		sheeps.add(LightningSheep.getItem());
		sheeps.add(LightningSheep.getItem());
		sheeps.add(LightningSheep.getItem());
		sheeps.add(LightningSheep.getItem());
		sheeps.add(LightningSheep.getItem());
		sheeps.add(LightningSheep.getItem());
	}
}
