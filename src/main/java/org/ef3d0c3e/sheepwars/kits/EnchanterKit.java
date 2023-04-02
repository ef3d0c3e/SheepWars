package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.sheeps.*;

import java.util.Arrays;

public class EnchanterKit extends Kit
{
	static ItemStack ITEM;

	static
	{
		ITEM = new ItemStack(Material.KNOWLEDGE_BOOK);
		final ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#E1EB70>Enchanteur"));
		meta.setLore(Arrays.asList(
			"",
			"§7Augmente vos chances d'obtenir",
			"§7des moutons spéciaux",
			"§7Et obtient des blocs",
			"§7pour défendre"
		));
		ITEM.setItemMeta(meta);
	}

	@Override
	public String getName()
	{
		return "enchanter";
	}

	@Override
	public String getColoredName()
	{
		return Util.getColored("<#E1EB70>Enchanteur");
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

	public EnchanterKit()
	{
		super();

		sheeps.add(HealerSheep.getItem());
		sheeps.add(HealerSheep.getItem());
		sheeps.add(HealerSheep.getItem());
		sheeps.add(HealerSheep.getItem());
		sheeps.add(HealerSheep.getItem());
		sheeps.add(HealerSheep.getItem());
		sheeps.add(ShieldSheep.getItem());
		sheeps.add(ShieldSheep.getItem());
		sheeps.add(ShieldSheep.getItem());
		sheeps.add(ShieldSheep.getItem());
		sheeps.add(ShieldSheep.getItem());
		sheeps.add(ShieldSheep.getItem());
		sheeps.add(ShieldSheep.getItem());
		sheeps.add(ShieldSheep.getItem());
		sheeps.add(ShieldSheep.getItem());
		sheeps.add(ShieldSheep.getItem());
		sheeps.add(ShieldSheep.getItem());
		sheeps.add(DarkSheep.getItem());
		sheeps.add(DarkSheep.getItem());
		sheeps.add(DarkSheep.getItem());
		sheeps.add(DarkSheep.getItem());
		sheeps.add(DarkSheep.getItem());
		sheeps.add(DarkSheep.getItem());
		sheeps.add(DarkSheep.getItem());
		sheeps.add(DarkSheep.getItem());
		sheeps.add(FrozenSheep.getItem());
		sheeps.add(FrozenSheep.getItem());
		sheeps.add(FrozenSheep.getItem());
		sheeps.add(FrozenSheep.getItem());
		sheeps.add(FrozenSheep.getItem());
		sheeps.add(FrozenSheep.getItem());
		sheeps.add(FrozenSheep.getItem());
		sheeps.add(FrozenSheep.getItem());
	}
}
