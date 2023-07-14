package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.sheeps.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArcherKit extends Kit
{
	static ItemStack ITEM;

	static
	{
		ITEM = new ItemStack(Material.BOW);
		final ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#C8EB70>Archer"));
		meta.setLore(Arrays.asList(
			"",
			"§7Améliore votre arc et vous",
			"§7donne plus de flèches"
		));
		ITEM.setItemMeta(meta);
	}

	public int streak = 0;

	@Override
	public String getName()
	{
		return "archer";
	}

	@Override
	public String getColoredName()
	{
		return Util.getColored("<#C8EB70>Archer");
	}

	@Override
	public ItemStack getDisplayItem()
	{
		return ITEM;
	}

	public ArcherKit(@Nullable CPlayer player)
	{
		super(player);

		// Class specific
		woolRandomizer.add(SwapSheep.class, 2.5f);
		woolRandomizer.add(ShieldSheep.class, 2.0f);
		woolRandomizer.add(DarkSheep.class, 1.5f);
		woolRandomizer.add(HealerSheep.class, 1.f);
		woolRandomizer.add(EarthQuakeSheep.class, 1.f);
		woolRandomizer.add(DistortionSheep.class, 1.f);
		woolRandomizer.add(BoardingSheep.class, 1.f);

		// Other
		woolRandomizer.add(ExplosiveSheep.class, 1.f);
		woolRandomizer.add(FragmentationSheep.class, 0.5f);
		woolRandomizer.add(IncendiarySheep.class, 0.5f);
		woolRandomizer.add(TsunamiSheep.class, 0.5f);
		woolRandomizer.add(FrozenSheep.class, 0.5f);
		woolRandomizer.add(SeekerSheep.class, 0.5f);

	}

	@Override
	public List<ItemStack> getWeapons()
	{
		final ArrayList<ItemStack> items = new ArrayList<>();

		// Sword
		final ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
		{
			ItemMeta meta = sword.getItemMeta();
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			sword.setItemMeta(meta);
		}
		items.add(sword);

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
