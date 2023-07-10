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
}
