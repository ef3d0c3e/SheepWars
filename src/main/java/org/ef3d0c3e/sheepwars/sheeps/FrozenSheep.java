package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import org.bukkit.*;
import org.bukkit.block.data.type.Snow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Game;
import org.ef3d0c3e.sheepwars.Util;


public class FrozenSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.LIGHT_BLUE_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#6EADE7>Glacé"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.LIGHT_BLUE;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Glacé"));
		name.setStyle(Style.EMPTY.withColor(0x6EADE7));
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
		final World world = (World)level.getWorld();
		world.spawnParticle(Particle.SNOWFLAKE, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int grounded = 0;

	public FrozenSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
	}

	public void ctick()
	{
		if (isOnGround())
			++grounded;

		if (grounded >= 5)
		{
			if (grounded % 10 == 0)
			{
				// Particles
				final World world = (World)level.getWorld();
				final Location center = new Location(world, getX(), getY()+0.5, getZ());
				Util.runInCircle(center, new Vector(0, 1, 0), 8.0, 32, (loc, t, i) ->
				{
					world.spawnParticle(Particle.SNOWFLAKE, loc, 1, 0.0, 0.0, 0.0, 0.0);
				});

				// Effect
				CPlayer.forEach(cp ->
				{
					if (!cp.isAlive() || !cp.isOnline() || cp.getTeam() == owner.getTeam())
						return;
					if (cp.getHandle().getLocation().distance(center) >= 8.5)
						return;

					final PotionEffect effect = cp.getHandle().getPotionEffect(PotionEffectType.SLOW);
					if (effect != null && effect.getDuration() > 20) // Another frozen
						cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 2));
					else
						cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 30, 1));
				});
			}

			if (grounded % 200 == 5)
			{
				final Location center = new Location((World)level.getWorld(), getX(), getY()+0.5, getZ());
				Util.runInSphereBlock(center, 8.0, (block) ->
				{
					if (block.getType() == Material.WATER)
						block.setType(Material.ICE);
					else if (block.getType() == Material.SNOW)
					{
						final Snow snow = (Snow)block.getBlockData();
						if (snow.getLayers() < snow.getMaximumLayers())
						{
							if (Game.nextInt() % 2 == 0)
							{
								snow.setLayers(snow.getLayers() + 1);
								block.setBlockData(snow);
							}
						}
						else
							block.setType(Material.SNOW_BLOCK);
					}
					else if (block.getType() == Material.AIR && block.canPlace(Material.SNOW.createBlockData()))
					{
						block.setType(Material.SNOW);
					}
				});
			}
		}
	}
}
