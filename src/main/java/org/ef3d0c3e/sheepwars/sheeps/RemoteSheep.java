package org.ef3d0c3e.sheepwars.sheeps;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.PlayerInteraction;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.events.CPlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.lang.invoke.MutableCallSite;

public class RemoteSheep extends BaseSheep implements PlayerRideable
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.LIME_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#8DD398>Télécommandé"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.LIME;
	}

	public MutableComponent getSheepName()
	{
		final MutableComponent name = MutableComponent.create(new LiteralContents("Télécommandé"));
		name.setStyle(Style.EMPTY.withColor(0x8DD398));

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
		world.spawnParticle(Particle.CRIT_MAGIC, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int lifeTime = 0;
	Location ownerLocation;

	public RemoteSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);

		ownerLocation = owner.getHandle().getLocation();
		((CraftPlayer) owner.getHandle()).getHandle().startRiding(this, true);
	}

	public void launch(final Vector direction, final double strength, final int duration)
	{
		super.launch(direction, strength, duration);
	}

	void disguise()
	{
		for (final Player p : Bukkit.getOnlinePlayers())
		{
			if (p != owner.getHandle())
			{
				p.hidePlayer(SheepWars.plugin, owner.getHandle());
			}
		}
	}

	void unDisguise()
	{
		for (final Player p : Bukkit.getOnlinePlayers())
		{
			if (p != owner.getHandle())
			{
				p.showPlayer(SheepWars.plugin, owner.getHandle());
			}
		}
	}

	public void travel(Vec3 vec3d)
	{
		if (!this.isAlive())
			return;

		LivingEntity entityliving = ((CraftLivingEntity)owner.getHandle()).getHandle();
		this.setYRot(entityliving.getYRot());
		this.yRotO = this.getYRot();
		this.setXRot(entityliving.getXRot() * 0.5F);
		this.setRot(this.getYRot(), this.getXRot());
		this.yBodyRot = this.getYRot();
		this.yHeadRot = this.yBodyRot;
		float f = entityliving.xxa * 0.5F;
		float f1 = entityliving.zza;

		if (this.isOnGround())
		{
			// Allow jumping
		}

		this.setSpeed(0.2f);
		super.travel(new Vec3((double)f, vec3d.y, (double)f1));

		this.calculateEntityAnimation(this, false);
		this.tryCheckInsideBlocks();
	}


	public void ctick()
	{
		if (lifeTime == 0)
		{
			disguise();
		}

		final World world = (World)level.getWorld();
		final Location center = new Location(world, getX(), getY()+0.5, getZ());
		world.spawnParticle(Particle.NAUTILUS, center, 1, 0.0, 0.0, 0.0, 0.0);

		if ((lifeTime / 12) % 2 == 0)
			setColor(DyeColor.WHITE);
		else
			setColor(getColor());

		if (lifeTime == 180)
		{
			unDisguise();
			this.remove(RemovalReason.DISCARDED);
		}

		++lifeTime;
	}

	public static class Events implements Listener
	{
		/**
		 * Sets player location when disconnecting while on remote
		 */
		@EventHandler
		public void onCPlayerQuit(final CPlayerQuitEvent ev)
		{
			if (ev.getPlayer().getHandle().getVehicle() == null ||
				!(((CraftEntity)ev.getPlayer().getHandle().getVehicle()).getHandle() instanceof RemoteSheep))
				return;

			final RemoteSheep sheep = (RemoteSheep) ((CraftEntity)ev.getPlayer().getHandle().getVehicle()).getHandle();
			sheep.remove(RemovalReason.DISCARDED);
			PlayerInteraction.teleport(ev.getPlayer(), sheep.ownerLocation, false);
		}

		/**
		 * Prevents player from taking damage from own remote
		 */
		@EventHandler
		public void onRemoteDamageOwner(final EntityDamageByEntityEvent ev)
		{
			if (!(((CraftEntity)ev.getDamager()).getHandle() instanceof RemoteSheep)
				|| ev.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
				|| !(ev.getEntity() instanceof Player))
				return;

			final CPlayer cp = CPlayer.getPlayer((Player)ev.getEntity());
			final RemoteSheep sheep = (RemoteSheep)((CraftEntity)ev.getDamager()).getHandle();

			if (cp == sheep.owner)
			{
				ev.setDamage(0);
			}
		}

		/**
		 * Prevents player from droping items while on remote
		 */
		@EventHandler(priority = EventPriority.LOWEST)
		public void onPlayerDropItem(final PlayerDropItemEvent ev)
		{
			if (ev.getPlayer().getVehicle() == null ||
				!(((CraftEntity)ev.getPlayer().getVehicle()).getHandle() instanceof RemoteSheep))
				return;

			ev.setCancelled(true);
		}

		/**
		 * Prevents player from droping items while on remote
		 */
		@EventHandler(priority = EventPriority.LOWEST)
		public void onPlayerInteract(final PlayerInteractEvent ev)
		{
			if (ev.getPlayer().getVehicle() == null ||
				!(((CraftEntity)ev.getPlayer().getVehicle()).getHandle() instanceof RemoteSheep))
				return;

			ev.setCancelled(true);
		}

		/**
		 * Prevents player from shooting with their bow while on a remote sheep
		 */
		@EventHandler
		public void onShoot(final EntityShootBowEvent ev)
		{
			if (!(ev.getEntity() instanceof Player))
				return;
			if (ev.getEntity().getVehicle() == null ||
				!(((CraftEntity)ev.getEntity().getVehicle()).getHandle() instanceof RemoteSheep))
				return;

			ev.setCancelled(true);
		}

		/**
		 * Make remote rider teleport to old position
		 */
		@EventHandler
		public void onDismount(final EntityDismountEvent ev)
		{
			if (!(ev.getEntity() instanceof Player))
				return;
			if (!(((CraftEntity)ev.getDismounted()).getHandle() instanceof RemoteSheep))
				return;

			final RemoteSheep sheep = (RemoteSheep)((CraftEntity)ev.getDismounted()).getHandle();
			ev.getDismounted().remove();
			new BukkitRunnable()
			{
				@Override
				public void run()
				{
					if (!((Player) ev.getEntity()).isOnline())
						return;

					ev.getEntity().teleport(sheep.ownerLocation);
					sheep.level.explode(sheep, sheep.getX(), sheep.getY(), sheep.getZ(), 5, false, Explosion.BlockInteraction.DESTROY);
				}
			}.runTaskLater(SheepWars.plugin, 1);
		}

		/**
		 * Prevents remote from damaging owner
		 */
		@EventHandler
		public void onRemoteDamage(final EntityDamageByEntityEvent ev)
		{
			if (!(((CraftEntity)ev.getDamager()).getHandle() instanceof RemoteSheep)
				|| ev.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
				return;
			final RemoteSheep sheep = (RemoteSheep) ((CraftEntity)ev.getDamager()).getHandle();
			if (!(ev.getEntity() instanceof Player)
				|| CPlayer.getPlayer((Player)ev.getEntity()) != sheep.owner)
				return;
			ev.setCancelled(true);
		}
	}
}
