package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.sheeps.*;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class BuilderKit extends Kit
{
	static ItemStack ITEM = new ItemStack(Material.BRICKS);

	public int streak = 0;

	@Override
	public String getName()
	{
		return "builder";
	}

	@Override
	public String getColoredName(final CPlayer cp)
	{
		return Util.getColored("<#C9BEAC>") + cp.getLocale().KIT_BUILDER;
	}

	@Override
	public List<String> getLore(final CPlayer cp)
	{
		return cp.getLocale().KIT_BUILDERLORE;
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
