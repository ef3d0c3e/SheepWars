package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.sheeps.*;

import javax.annotation.Nullable;
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

	public BuilderKit(@Nullable CPlayer player)
	{
		super(player);

		// Class specific
		woolRandomizer.add(TsunamiSheep.class, 2.0f);
		woolRandomizer.add(ShieldSheep.class, 1.5f);
		woolRandomizer.add(DistortionSheep.class, 1.0f);
		woolRandomizer.add(EarthQuakeSheep.class, 1.0f);
		woolRandomizer.add(HealerSheep.class, 1.0f);
		woolRandomizer.add(FrozenSheep.class, 1.0f);

		// Other
		woolRandomizer.add(ExplosiveSheep.class, 1.f);
		woolRandomizer.add(BoardingSheep.class, 0.5f);
		woolRandomizer.add(SwapSheep.class, 0.5f);
		woolRandomizer.add(DarkSheep.class, 0.5f);
		woolRandomizer.add(FragmentationSheep.class, 0.5f);
		woolRandomizer.add(IncendiarySheep.class, 0.5f);
		woolRandomizer.add(SeekerSheep.class, 0.5f);
	}
}
