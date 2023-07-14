package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.sheeps.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
			"§7Et obtien un Bâton de Lévitation",
			"§7à la place de l'épée"
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

	public EnchanterKit(@Nullable CPlayer player)
	{
		super(player);

		// Special
		woolRandomizer.add(SlimeSheep.class, 2.f);

		// Class specific
		woolRandomizer.add(ShieldSheep.class, 1.5f);
		woolRandomizer.add(HealerSheep.class, 1.5f);
		woolRandomizer.add(FrozenSheep.class, 1.5f);
		woolRandomizer.add(DarkSheep.class, 1.5f);
		woolRandomizer.add(TsunamiSheep.class, 1.0f);

		// Other
		woolRandomizer.add(ExplosiveSheep.class, 1.f);
		woolRandomizer.add(DistortionSheep.class, 0.5f);
		woolRandomizer.add(EarthQuakeSheep.class, 0.5f);
		woolRandomizer.add(BoardingSheep.class, 0.5f);
		woolRandomizer.add(SwapSheep.class, 0.5f);
		woolRandomizer.add(FragmentationSheep.class, 0.5f);
		woolRandomizer.add(IncendiarySheep.class, 0.5f);
		woolRandomizer.add(SeekerSheep.class, 0.5f);
	}

	@Override
	public List<ItemStack> getWeapons()
	{
		final ArrayList<ItemStack> items = new ArrayList<>();

		// Wand
		final ItemWand wand = new ItemWand();
		final ItemStack wandItem = new ItemStack(Material.BLAZE_ROD);
		{
			ItemMeta meta = wandItem.getItemMeta();
			meta.setDisplayName("§bBâton de Lévitation");
			wandItem.setItemMeta(meta);
		}
		items.add(wand.apply(wandItem));
		SheepWars.getItemRegistry().registerItem(wand);

		// Bow
		final ItemStack bow = new ItemStack(Material.BOW);
		{
			ItemMeta meta = bow.getItemMeta();
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addEnchant(Enchantment.ARROW_KNOCKBACK, 1, true);
			bow.setItemMeta(meta);
		}
		items.add(bow);

		return items;
	}
}
