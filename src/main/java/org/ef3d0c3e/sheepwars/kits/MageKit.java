package org.ef3d0c3e.sheepwars.kits;

import net.minecraft.world.level.Explosion;
import org.bukkit.Material;
import org.bukkit.entity.Explosive;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.sheeps.*;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
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

	public MageKit(@Nullable CPlayer player)
	{
		super(player);


		// Class specific
		woolRandomizer.add(ExplosiveSheep.class, 5.f);
		woolRandomizer.add(IncendiarySheep.class, 3.f);
		woolRandomizer.add(FragmentationSheep.class, 2.f);
		woolRandomizer.add(LightningSheep.class, 2.f); // Special
		woolRandomizer.add(EarthQuakeSheep.class, 1.f);
		woolRandomizer.add(DistortionSheep.class, 1.f);
		woolRandomizer.add(TsunamiSheep.class, 1.f);
		woolRandomizer.add(FrozenSheep.class, 1.f);
		woolRandomizer.add(SeekerSheep.class, 1.f);

		// Other
		woolRandomizer.add(HealerSheep.class, 0.5f);
		woolRandomizer.add(ShieldSheep.class, 0.5f);
		woolRandomizer.add(BoardingSheep.class, 0.5f);
	}
}
