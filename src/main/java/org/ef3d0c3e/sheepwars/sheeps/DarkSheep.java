package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

public class DarkSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.BLACK_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#1F1F1F>Sombre"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.BLACK;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Sombre"));
		name.setStyle(Style.EMPTY.withColor(0x1F1F1F));
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
		world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int grounded = 0;

	public DarkSheep(final Location loc, final CPlayer owner)
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
				Util.runInCircle(center, new Vector(0, 1, 0), 7.0, 32, (loc, t, i) ->
				{
					world.spawnParticle(Particle.SQUID_INK, loc, 1, 0.0, 0.0, 0.0, 0.0);

					if (grounded % 20 == 0)
						world.spawnParticle(Particle.VIBRATION, loc, 1,
							new Vibration(loc, new Vibration.Destination.EntityDestination(this.getBukkitEntity()), 20));
				});

				// Effect
				CPlayer.forEach(cp ->
				{
					if (!cp.isAlive() || !cp.isOnline() || cp.getTeam() == owner.getTeam())
						return;
					if (cp.getHandle().getLocation().distance(center) >= 7.5)
						return;

					cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
				});
			}
		}
	}
}
