package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Game;
import org.ef3d0c3e.sheepwars.Util;

import java.util.ArrayList;
import java.util.Iterator;

public class ShieldSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.CYAN_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#ADCAFE>Bouclier"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.CYAN;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Bouclier"));
		name.setStyle(Style.EMPTY.withColor(0xADCAFE));
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
		world.spawnParticle(Particle.ELECTRIC_SPARK, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int grounded = 0;

	public ShieldSheep(final Location loc, final CPlayer owner)
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
					world.spawnParticle(Particle.ELECTRIC_SPARK, loc, 1, 0.0, 0.0, 0.0, 0.0);
				});

				// Effect
				CPlayer.forEach(cp ->
				{
					if (!cp.isAlive() || !cp.isOnline() || cp.getTeam() != owner.getTeam())
						return;
					if (cp.getHandle().getLocation().distance(center) >= 8.5)
						return;

					final PotionEffect effect = cp.getHandle().getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
					if (effect != null && effect.getDuration() > 10) // Another healer
						cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30, 1));
					else
						cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30, 0));
				});
			}
		}
	}

	public static class Events implements Listener
	{
		/**
		 * Prevents shield to take explosion damage
		 */
		@EventHandler
		public void onShieldDamage(final EntityDamageEvent ev)
		{
			if (!(((CraftEntity)ev.getEntity()).getHandle() instanceof ShieldSheep)
				|| ev.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
				return;
			ev.setCancelled(true);
		}

		@EventHandler
		public void onEntityExplode(final EntityExplodeEvent ev)
		{
			int amt = 0;

			ArrayList<Entity> shields = new ArrayList<>();
			for (final Entity ent : ev.getEntity().getWorld().getEntities())
			{
				if (!(((CraftEntity)ent).getHandle() instanceof ShieldSheep))
					continue;

				shields.add(ent);
			}

			Iterator<Block> it =  ev.blockList().iterator();
			while (it.hasNext())
			{
				final Block block = it.next();
				if (!shields.isEmpty())
				{
					boolean removed = false;
					for (final Entity shield : shields)
					{
						if (block.getLocation().distanceSquared(shield.getLocation()) >= 64.0)
							continue;
						it.remove();
						removed = true;
						break;
					}
					if (!removed && block.getType() != Material.TNT && Game.nextInt() % 3 == 0 && amt != 50)
					{
						++amt;

						FallingBlock fb = block.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
						fb.setDropItem(false);
						fb.setVelocity(new Vector(
							(Math.random() - Math.random()) / 1.5,
							Math.random(),
							(Math.random() - Math.random()) / 1.5));
					}
				}
				else if (block.getType() != Material.TNT && Game.nextInt() % 3 == 0 && amt != 50)
				{
					++amt;

					FallingBlock fb = block.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
					fb.setDropItem(false);
					fb.setVelocity(new Vector(
						(Math.random() - Math.random()) / 1.5,
						Math.random(),
						(Math.random() - Math.random()) / 1.5));
				}
			}
		}

		@EventHandler
		public void onFireSpread(final BlockIgniteEvent ev)
		{
			ev.getBlock().getLocation();
			for(final Entity ent : ev.getBlock().getWorld().getEntities())
			{
				if (!(((CraftEntity)ent).getHandle() instanceof ShieldSheep))
					continue;

				if (ent.getLocation().distanceSquared(ev.getBlock().getLocation()) <= 64.0)
					ev.setCancelled(true);
			}
		}

		@EventHandler
		public void onFireBurn(final BlockBurnEvent ev)
		{
			ev.getBlock().getLocation();
			for(final Entity ent : ev.getBlock().getWorld().getEntities())
			{
				if (!(((CraftEntity)ent).getHandle() instanceof ShieldSheep))
					continue;

				if (ent.getLocation().distanceSquared(ev.getBlock().getLocation()) <= 64.0)
				{
					if (ev.getIgnitingBlock().getType() == Material.FIRE)
						ev.getIgnitingBlock().setType(Material.AIR);
					ev.setCancelled(true);
				}
			}
		}
	}
}
