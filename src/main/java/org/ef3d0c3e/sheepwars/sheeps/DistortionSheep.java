package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

public class DistortionSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.PURPLE_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#8E0BFE>Distortion"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.PURPLE;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Distortion"));
		name.setStyle(Style.EMPTY.withColor(0x8E0BFE));
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
		final World world = (World)level().getWorld();
		world.spawnParticle(Particle.PORTAL, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int grounded = 0;
	int fuse = 0;

	public DistortionSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
	}

	public void ctick()
	{
		if (onGround())
			++grounded;

		if (grounded >= 5)
		{
			final World world = (World)level().getWorld();
			final Location center = new Location(world, getX(), getY() + 0.5, getZ());

			// Particles
			if (grounded % 10 == 0)
			{
				Util.runInCircle(center, new Vector(0, 1, 0).normalize(), 7.0, 15, (loc, t, i) ->
				{
					Util.runInLine(loc, center, 4, (loc2, i2) ->
					{
						world.spawnParticle(Particle.PORTAL, loc2, 1, 0.0, 0.0, 0.0, 0.0);
					});
				});
			}

			if (grounded % 20 == 0)
			{
				for (int i = 0; i < 30; ++i)
				{
					final double r = Math.random() * 6.0 + 1.0;
					final double t = (Math.random() - 0.5) * Math.PI;
					final double u = Math.random() * Math.PI * 2.0;

					final Block block = world.getBlockAt(
						 (int)(center.getX() + r * Math.cos(t) * Math.sin(u)),
						 (int)(center.getY() + r * Math.sin(t) * Math.sin(u)),
						 (int)(center.getZ() + r * Math.cos(u))
					);
					if (block.getType() == Material.AIR ||
						Util.isBlastResistant(block.getType()))
						continue;

					final FallingBlock fb = world.spawnFallingBlock(block.getLocation(), block.getBlockData());
					fb.setDropItem(false);
					block.setType(Material.AIR);
				}
			}

			// Effect
			for (final Entity ent : world.getEntities())
			{
				final double distSq = ent.getLocation().distanceSquared(center);
				if (distSq >= 49.0 || distSq <= 0.1 || ((CraftEntity)ent).getHandle() instanceof DistortionSheep)
					continue;
				if (ent instanceof Player)
				{
					CPlayer cp = CPlayer.getPlayer((Player)ent);
					if (!cp.isAlive() || cp.getTeam() == owner.getTeam())
						continue;
					cp.getCombatData().damageNow(owner);
				}

				final Vector dir = new Vector(
					center.getX() - ent.getLocation().getX(),
					center.getY() - ent.getLocation().getY(),
					center.getZ() - ent.getLocation().getZ()
				).normalize().multiply(Math.min(0.1 / Math.sqrt(distSq), 0.5));

				ent.setVelocity(ent.getVelocity().add(dir));
			}
		}
	}
}
