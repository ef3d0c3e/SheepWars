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

public class SlimeSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.LIME_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#4AE84A>Slime"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.LIME;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Slime"));
		name.setStyle(Style.EMPTY.withColor(0x4AE84A));
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
		world.spawnParticle(Particle.SLIME, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
		world.spawnParticle(Particle.SLIME, getX(), getY(), getZ(), 3, 0.2, 0.2, 0.2, 0.0);
	}

	int grounded = 0;
	int lifeTime = 0;
	SlimeSheepSlime slime = null;

	public SlimeSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
	}

	public void launch(final Vector direction, final double strength, final int duration)
	{
		super.launch(direction, strength, duration);

	}

	public void spawn()
	{
		super.spawn();

		// Spawn slime
		slime = new SlimeSheepSlime(this, new Location(level().getWorld(), getX(), getY(), getZ()), owner);
		slime.spawn();
		slime.startRiding(this);
	}

	public void ctick()
	{
		if (onGround())
			++grounded;

		if (grounded >= 5)
		{
			if (lifeTime % 20 == 0)
			{
				// Particles
				final World world = (World) level().getWorld();
				final Location center = new Location(world, getX(), getY() + 0.5, getZ());
				Util.runInCircle(center, new Vector(0, 1, 0), 6.5, 32, (loc, t, i) ->
				{
					world.spawnParticle(Particle.SLIME, loc, 1, 0.0, 0.0, 0.0, 0.0);
				});

				// Effect
				CPlayer.forEach(cp ->
				{
					if (!cp.isAlive() || !cp.isOnline() || cp.getTeam() == null || cp.getTeam() == owner.getTeam())
						return;
					if (cp.getHandle().getLocation().distance(center) >= 7)
						return;

					cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
				});
			}

			if (lifeTime > 120)
			{
				if ((lifeTime / 8) % 2 == 0)
					setColor(DyeColor.WHITE);
				else
					setColor(getColor());

				if (lifeTime == 160)
					this.remove(RemovalReason.DISCARDED);
			}
			++lifeTime;
		}
	}
}
