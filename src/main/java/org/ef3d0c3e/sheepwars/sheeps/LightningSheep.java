package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Game;
import org.ef3d0c3e.sheepwars.Util;

public class LightningSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.LIGHT_GRAY_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#BABABA>Éclairs"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.LIGHT_GRAY;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Éclairs"));
		name.setStyle(Style.EMPTY.withColor(0xBABABA));
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
		world.spawnParticle(Particle.VILLAGER_ANGRY, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int grounded = 0;
	int lifeTime = 0;

	public LightningSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
	}

	public void ctick()
	{
		if (onGround())
			++grounded;

		if (lifeTime == 200)
			this.remove(RemovalReason.DISCARDED);

		if (grounded >= 5)
		{
			final World world = (World)level().getWorld();
			final Location center = new Location(world, getX(), getY()+0.5, getZ());

			// Particles
			if (grounded % 30 == 0)
			{
				Util.runInCircle(center, new Vector(0, 1, 0), 4.5, 32, (loc, t, i) ->
				{
					world.spawnParticle(Particle.VIBRATION, loc, 1,
						new Vibration(loc, new Vibration.Destination.EntityDestination(this.getBukkitEntity()), 20));
				});
			}

			// Lightning
			if (grounded % 30 == 10)
			{
				final int amt = Game.nextInt() % 3;
				for (int i = 0; i < amt; ++i)
				{
					final double r = Math.random() * 4.0 + 1.0;
					final double t = (Math.random() - 0.5) * Math.PI;
					final double u = Math.random() * Math.PI * 2.0;

					world.strikeLightning(new Location(world,
						center.getX() + r * Math.cos(t) * Math.sin(u),
						center.getY() + r * Math.sin(t) * Math.sin(u),
						center.getZ() + r * Math.cos(u)
					));
				}
			}
			++lifeTime;
		}
	}

	public static class Events implements Listener
	{
		/**
		 * Prevents lightning sheep to take lightning damage
		 */
		@EventHandler
		public void onLightningDamage(final EntityDamageEvent ev)
		{
			if (!(((CraftEntity)ev.getEntity()).getHandle() instanceof LightningSheep)
				|| (ev.getCause() != EntityDamageEvent.DamageCause.LIGHTNING && ev.getCause() != EntityDamageEvent.DamageCause.FIRE))
				return;
			ev.setCancelled(true);
		}
	}
}
