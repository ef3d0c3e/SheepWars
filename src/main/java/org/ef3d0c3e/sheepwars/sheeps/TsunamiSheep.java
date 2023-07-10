package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

import java.text.MessageFormat;

public class TsunamiSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.BLUE_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#4A48E8>Tsunami"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.BLUE;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Tsunami"));
		name.setStyle(Style.EMPTY.withColor(0x4A48E8));
		return name;
	}

	static public ItemStack getItem()
	{
		return ITEM;
	}

	public ItemStack getDrop()
	{
		return getItem();
	}

	protected void spawnParticles(final int time)
	{
		final World world = level().getWorld();
		world.spawnParticle(Particle.WATER_BUBBLE, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
		world.spawnParticle(Particle.WATER_SPLASH, getX(), getY(), getZ(), 3, 0.2, 0.2, 0.2, 0.0);
	}

	int grounded = 0;
	int ticks = 0;

	public TsunamiSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
	}

	public void ctick()
	{
		final World world = level().getWorld();

		final Location sheepLoc = new Location(world, getX(), getY(), getZ());
		if (onGround() || sheepLoc.getBlock().getType() == Material.WATER)
			++grounded;

		final Vec3 vel = getDeltaMovement();
		final Vec3 dir = vel.normalize().cross(new Vec3(0, 1, 0)); // Water slice direction (vel/|vel|) ^ up
		final int bpt = (int)Math.ceil(vel.length());

		// Set water
		for (int b = 0; b < bpt; ++b)
		{
			final Vec3 pos = new Vec3(getX(), getY(), getZ()).add(vel.scale(((double)b)/bpt));
			for (int i = -3; i <= 3; ++i)
			{
				final Location loc = new Location(world, pos.x + dir.x * i, pos.y + dir.y * i - 1, pos.z + dir.z * i);
				final Block bl = loc.getBlock();
				if (bl.getType() != Material.AIR)
					continue;
				bl.setType(Material.WATER);
				Levelled l = (Levelled) bl.getBlockData();
				l.setLevel(1);
				bl.setBlockData(l);
			}
		}


		if (grounded >= 5 || ticks > 200)
			remove(RemovalReason.DISCARDED);
		++ticks;
	}
}
