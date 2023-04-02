package org.ef3d0c3e.sheepwars;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.ef3d0c3e.sheepwars.events.CPlayerDamageEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerDeathEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.kits.ArcherKit;
import org.ef3d0c3e.sheepwars.kits.BuilderKit;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Combat
{
	static public class Data
	{
		private CPlayer lastAttacker;
		private int lastAttacked;
		private int regenQueue;

		/**
		 * Maximum time after which lastAttacker will be attributed kill
		 * @return Time in seconds
		 */
		public static int killTime()
		{
			return 10;
		}

		/**
		 * Time after being attacked before you can regenerate
		 * @return Time in seconds
		 */
		public static int regenTime()
		{
			return 4;
		}

		/**
		 * Gets who should be attributed kill
		 * @return Player
		 */
		public CPlayer getKiller()
		{
			if (Game.getTimer().getElapsedTime() - lastAttacked >= killTime())
				return null;

			return lastAttacker;
		}

		/**
		 * Gets whether player can regenerate health
		 * @return True if player can regenerate
		 */
		public boolean canRegenerate()
		{
			return Game.getTimer().getElapsedTime() - lastAttacked >= regenTime();
		}

		/**
		 * Makes cp become last damager
		 * @param cp Player that damages
		 */
		public void damageNow(final CPlayer cp)
		{
			lastAttacker = cp;
			lastAttacked = Game.getTimer().getElapsedTime();
		}

		/**
		 * Constructor
		 */
		public Data()
		{
			lastAttacked = - killTime();
			lastAttacker = null;
			regenQueue = 0;
		}
	}

	static public class Events implements Listener
	{
		Events()
		{
			for (final Player p : Bukkit.getOnlinePlayers())
				updateAttackSpeed(p);

			new BukkitRunnable()
			{
				int ticks = 0;
				@Override
				public void run()
				{
					if (ticks == 0)
					{
						ticks += 5;
						return;
					}

					if (ticks % 60 == 0) // Natural regen
					{
						CPlayer.forEach((cp) -> {
							if (!cp.isAlive() || !cp.getCombatData().canRegenerate())
								return;
							final double max = cp.getHandle().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
							cp.getHandle().setHealth(Math.min(max, cp.getHandle().getHealth()+1));
						});
					}

					if (ticks % 10 == 0) // Queue regen
					{
						CPlayer.forEach((cp) -> {
							if (!cp.isAlive() || cp.getCombatData().regenQueue == 0)
								return;

							final double max = cp.getHandle().getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
							cp.getHandle().setHealth(Math.min(max, cp.getHandle().getHealth()+1));
							--cp.getCombatData().regenQueue;
						});
					}

					ticks += 5;
				}
			}.runTaskTimer(SheepWars.plugin, 0, 5);
		}

		@EventHandler
		public void onPlayerTeleport(final PlayerTeleportEvent ev)
		{
			updateAttackSpeed(ev.getPlayer());
		}

		@EventHandler
		public void onFoodLevelChange(final FoodLevelChangeEvent ev)
		{
			ev.setCancelled(true);
		}

		@EventHandler
		public void onCPlayerJoin(final CPlayerJoinEvent ev)
		{
			updateAttackSpeed(ev.getPlayer().getHandle());
		}

		/**
		 * Regenerate part of fall damage
		 */
		@EventHandler
		public void onFallDamage(final EntityDamageEvent ev)
		{
			if (ev.getCause() != EntityDamageEvent.DamageCause.FALL)
				return;
			if (!(ev.getEntity() instanceof Player))
				return;

			final CPlayer cp = CPlayer.getPlayer((Player)ev.getEntity());
			if (!cp.isAlive())
				return;
			cp.getCombatData().regenQueue = (int)(ev.getFinalDamage() * 0.65);
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onPlayerDamage(final EntityDamageEvent ev)
		{
			if (!(ev.getEntity() instanceof Player))
				return;
			if (Game.isSafe())
			{
				ev.setCancelled(true);
				return;
			}
			final CPlayer victim = CPlayer.getPlayer((Player)ev.getEntity());
			if (!victim.isAlive())
			{
				ev.setCancelled(true);
				return;
			}

			CPlayer attacker = null;
			if (ev instanceof EntityDamageByEntityEvent)
			{
				final EntityDamageByEntityEvent pev = (EntityDamageByEntityEvent)ev;
				attacker = Util.getPlayerAttacker(pev);
				if (attacker != null && attacker != victim)
				{
					if (attacker.getTeam() == victim.getTeam())
					{
						ev.setCancelled(true);
						return;
					}

					victim.getCombatData().lastAttacker = attacker;
					victim.getCombatData().lastAttacked = Game.getTimer().getElapsedTime();

					// Blood
					victim.getHandle().getWorld().spawnParticle(
						Particle.BLOCK_CRACK,
						victim.getHandle().getLocation().clone().add(0.0, victim.getHandle().getHeight() / 1.5, 0.0),
						(int)Math.min(50.0, ev.getFinalDamage() * 8.0),
						0.4, 0.4, 0.4,
						Material.REDSTONE_BLOCK.createBlockData()
					);
				}

			}

			if (ev.getFinalDamage() < victim.getHandle().getHealth()) // Would not die
			{
				Bukkit.getPluginManager().callEvent(new CPlayerDamageEvent(victim, attacker, ev, false));
				return;
			}

			ev.setCancelled(true);
			final CPlayer killer = victim.getCombatData().getKiller();
			if (killer == null)
				Bukkit.broadcastMessage(MessageFormat.format("§8'{§c☠§8}' {0}{1}§7 est mort.",
					Util.getColored(victim.getTeam().getColorCode()), victim.getHandle().getName()));
			else
				Bukkit.broadcastMessage(MessageFormat.format("§8'{§c☠§8}' {0}{1}§7 a tué {2}{3}§7.",
					Util.getColored(killer.getTeam().getColorCode()), killer.getHandle().getName(),
					Util.getColored(victim.getTeam().getColorCode()), victim.getHandle().getName()));

			Bukkit.getPluginManager().callEvent(new CPlayerDamageEvent(victim, killer, ev, true));
			Bukkit.getPluginManager().callEvent(new CPlayerDeathEvent(victim, killer));
		}

		@EventHandler
		public void onArrowHit(final ProjectileHitEvent ev)
		{
			if (ev.getHitBlock() == null || !(ev.getEntity() instanceof Arrow))
				return;

			// Make unpickable
			((Arrow)ev.getEntity()).setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

			// Resets streak
			if (!(ev.getEntity().getShooter() instanceof Player) || ev.getHitEntity() != null)
				return;
			final CPlayer cp = CPlayer.getPlayer((Player)ev.getEntity().getShooter());
			if (!(cp.getKit() instanceof ArcherKit))
				return;

			if (((ArcherKit)cp.getKit()).streak != 0)
			{
				((ArcherKit)cp.getKit()).streak = 0;
				cp.getHandle().playSound(cp.getHandle().getLocation(), Sound.BLOCK_ANVIL_PLACE, 16.f, 0.41f);
			}
		}

		@EventHandler
		public void onEntityDamageByEntity(final EntityDamageByEntityEvent ev)
		{
			if (!(ev.getEntity() instanceof LivingEntity) ||
				!(ev.getDamager() instanceof Projectile) ||
				!(((Projectile)ev.getDamager()).getShooter() instanceof Player))
				return;

			final CPlayer shooter = CPlayer.getPlayer((Player)((Projectile)ev.getDamager()).getShooter());
			if (!(shooter.getKit() instanceof ArcherKit) || ((Projectile)ev.getDamager()).getShooter() == ev.getEntity())
				return;

			final ArcherKit kit = (ArcherKit)shooter.getKit();
			final double perc = 1.0 + Math.min(kit.streak, 5) / 10.0;
			ev.setDamage(ev.getDamage() * perc);
			++kit.streak;
			shooter.getHandle().playSound(shooter.getHandle().getLocation(), Sound.BLOCK_ANVIL_PLACE, 16.f, (float)(perc+0.1));
		}
	}

	/**
	 * Updates attack speed of player
	 * @param p Player
	 */
	public static void updateAttackSpeed(final Player p)
	{
		final AttributeInstance attr = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
		attr.setBaseValue(24);
		p.saveData();
	}
}
