package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.sheeps.*;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BarbarianKit extends Kit
{
	static ItemStack ITEM;

	static
	{
		ITEM = new ItemStack(Material.STONE_SWORD);
		final ItemMeta meta = ITEM.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		ITEM.setItemMeta(meta);
	}

	@Override
	public String getName()
	{
		return "barbarian";
	}

	@Override
	public String getColoredName(final CPlayer cp)
	{
		return Util.getColored("<#E15533>") + cp.getLocale().KIT_BARBARIAN;
	}

	@Override
	public List<String> getLore(final CPlayer cp)
	{
		return cp.getLocale().KIT_BARBARIANLORE;
	}

	@Override
	public ItemStack getDisplayItem()
	{
		return ITEM;
	}

	@Override
	public void tick(int ticks)
	{
		if (player == null || ticks % 20 != 0)
			return;
		if (!player.isAlive() || !player.isOnline())
			return;

		player.getHandle().addPotionEffect(new PotionEffect(
			PotionEffectType.DOLPHINS_GRACE,
			200, 0, false, false, true
		));
	}

	public BarbarianKit(@Nullable CPlayer player)
	{
		super(player);

		// Class specific
		woolRandomizer.add(BoardingSheep.class, 3.0f);
		woolRandomizer.add(HealerSheep.class, 2.0f);
		woolRandomizer.add(SwapSheep.class, 2.0f);
		woolRandomizer.add(DarkSheep.class, 2.0f);
		woolRandomizer.add(ShieldSheep.class, 1.0f);
		woolRandomizer.add(TsunamiSheep.class, 1.0f);

		// Other
		woolRandomizer.add(ExplosiveSheep.class, 1.f);
		woolRandomizer.add(EarthQuakeSheep.class, 0.5f);
		woolRandomizer.add(DistortionSheep.class, 0.5f);
		woolRandomizer.add(FragmentationSheep.class, 0.5f);
		woolRandomizer.add(IncendiarySheep.class, 0.5f);
		woolRandomizer.add(FrozenSheep.class, 0.5f);
		woolRandomizer.add(SeekerSheep.class, 0.5f);
	}

	@Override
	public List<ItemStack> getWeapons()
	{
		final ArrayList<ItemStack> items = new ArrayList<>();

		// Sword
		final ItemStack sword = new ItemStack(Material.STONE_SWORD);
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
			bow.setItemMeta(meta);
		}
		items.add(bow);

		return items;
	}
}
