package org.ef3d0c3e.sheepwars.game;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.CPlayerDamageEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerDeathEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;

import javax.annotation.Nullable;


@WantsListen(phase = WantsListen.Target.Game)
public class Combat implements Listener {
    /**
     * Combat data stored on a per-player basis
     */
    public static class Data
    {
        private CPlayer lastAttacker;
        private int lastAttacked;
        private int regenQueue;

        /**
         * Constructor
         */
        public Data()
        {
            lastAttacked = - killTime();
            lastAttacker = null;
            regenQueue = 0;
        }

        /**
         * Maximum time after which lastAttacker will be attributed kill
         * @return Time in ticks
         */
        public static int killTime()
        {
            return 200;
        }

        /**
         * Time after being attacked before you can regenerate
         * @return Time in ticks
         */
        public static int regenTime()
        {
            return 80;
        }

        /**
         * Gets who should be attributed kill
         * @return Player
         */
        public CPlayer getKiller()
        {
            if (Game.getTimer().getElapsed() - lastAttacked >= killTime())
                return null;

            return lastAttacker;
        }

        /**
         * Gets whether player can regenerate health
         * @return True if player can regenerate
         */
        public boolean canRegenerate()
        {
            return Game.getTimer().getElapsed() - lastAttacked >= regenTime();
        }

        /**
         * Makes cp become last damager
         * @param cp Player that damages
         */
        public void damageNow(final CPlayer cp)
        {
            lastAttacker = cp;
            lastAttacked = Game.getTimer().getElapsed();
        }
    }

    /**
     * Constructor
     *
     * Starts the timed event for regeneration
     */
    public Combat()
    {
        // Updates the player's attack speed
        for (final Player p : Bukkit.getOnlinePlayers()) {
            final var attr = p.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
            attr.setBaseValue(24.0);
            p.saveData();
        }

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
        }.runTaskTimer(SheepWars.getPlugin(), 0, 5);
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent ev)
    {
        final var attr = ev.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        attr.setBaseValue(24.0);
        ev.getPlayer().saveData();
    }

    @EventHandler
    public void onCPlayerJoin(final CPlayerJoinEvent ev)
    {
        final var attr = ev.getPlayer().getHandle().getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        attr.setBaseValue(24.0);
        ev.getPlayer().getHandle().saveData();
    }

    /**
     * Regenerate part of fall damage and prevent death
     */
    @EventHandler
    public void onFallDamage(final EntityDamageEvent ev)
    {
        if (ev.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;
        if (!(ev.getEntity() instanceof Player))
            return;

        final CPlayer cp = CPlayer.get((Player)ev.getEntity());
        if (!cp.isAlive())
            return;
        if (ev.getDamage()+1 >= cp.getHandle().getHealth())
            ev.setDamage(cp.getHandle().getHealth() - 1);
        cp.getCombatData().regenQueue = (int)(ev.getFinalDamage() * 0.40);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(final EntityDamageEvent ev)
    {
        if (ev.isCancelled() || !(ev.getEntity() instanceof Player))
            return;
        if (!Game.hasStarted())
        {
            ev.setCancelled(true);
            return;
        }
        final CPlayer victim = CPlayer.get((Player)ev.getEntity());
        if (!victim.isAlive())
        {
            ev.setCancelled(true);
            return;
        }

        CPlayer attacker = null;
        if (ev instanceof EntityDamageByEntityEvent)
        {
            final EntityDamageByEntityEvent pev = (EntityDamageByEntityEvent)ev;
            attacker = getPlayerAttacker(pev);
            if (attacker != null && attacker != victim)
            {
                if (attacker.getTeam() == victim.getTeam())
                {
                    ev.setCancelled(true);
                    return;
                }

                victim.getCombatData().lastAttacker = attacker;
                victim.getCombatData().lastAttacked = Game.getTimer().getElapsed();

                // Blood
                victim.getHandle().getWorld().spawnParticle(
                        Particle.BLOCK,
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
    }

    /**
     * Gets the attacker in a entity damage event
     * @param ev Event
     * @return The attacker, null if none
     */
    static @Nullable CPlayer getPlayerAttacker(final EntityDamageByEntityEvent ev)
    {
        if (ev.getDamager() instanceof Player)
            return CPlayer.get((Player)ev.getDamager());
        else if (ev.getDamager() instanceof Projectile)
        {
            final ProjectileSource shooter = ((Projectile)ev.getDamager()).getShooter();
            if (shooter instanceof Player)
                return CPlayer.get((Player)shooter);
            else
                return null;
        }
        else
        {
            final var sheep = BaseSheep.getInstance(ev.getDamager());
            if (sheep == null) return null;

            return sheep.getOwner();
        }
    }
}
