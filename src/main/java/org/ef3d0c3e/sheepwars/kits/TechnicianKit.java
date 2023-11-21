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

public class TechnicianKit extends Kit
{
	static ItemStack ITEM = new ItemStack(Material.LEVER);

	@Override
	public String getName()
	{
		return "technician";
	}

	@Override
	public String getColoredName(final CPlayer cp)
	{
		return Util.getColored("<#61B36C>") + cp.getLocale().KIT_TECHNICIAN;
	}

	@Override
	public List<String> getLore(final CPlayer cp)
	{
		return cp.getLocale().KIT_TECHNICIANLORE;
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

	public TechnicianKit(@Nullable CPlayer player)
	{
		super(player);

		// Special
		woolRandomizer.add(SlimeSheep.class, 2.0f);

		// Class specific
		woolRandomizer.add(SeekerSheep.class, 1.5f);
		woolRandomizer.add(FrozenSheep.class, 1.5f);
		woolRandomizer.add(TsunamiSheep.class, 1.0f);
		woolRandomizer.add(SwapSheep.class, 1.0f);

		// Other
		woolRandomizer.add(ExplosiveSheep.class, 1.f);
		woolRandomizer.add(ShieldSheep.class, 0.5f);
		woolRandomizer.add(HealerSheep.class, 0.5f);
		woolRandomizer.add(DarkSheep.class, 0.5f);
		woolRandomizer.add(DistortionSheep.class, 0.5f);
		woolRandomizer.add(EarthQuakeSheep.class, 0.5f);
		woolRandomizer.add(BoardingSheep.class, 0.5f);
		woolRandomizer.add(FragmentationSheep.class, 0.5f);
		woolRandomizer.add(IncendiarySheep.class, 0.5f);

	}
}
