package org.ef3d0c3e.sheepwars.game;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.destroystokyo.paper.event.player.PlayerReadyArrowEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.PhaseChangeEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.loadouts.Arrows;
import org.ef3d0c3e.sheepwars.loadouts.RefillResource;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.text.MessageFormat;

/**
 * Events triggered when the game starts
 */
@WantsListen(phase = WantsListen.Target.Game)
public class GameStart implements Listener {
    /**
     * Starts the countdown until players can move
     */
    @EventHandler
    public void onPhaseChange(final PhaseChangeEvent e)
    {
        // Update team counts
        CPlayer.forEachOnline(cp -> {
            final var team = cp.getTeam();
            cp.setAlive(true);
            team.setAliveCount(team.getAliveCount() + 1);
        });

        // Start countdown
        new BukkitRunnable() {
            /**
             * Elapsed seconds
             */
            int elapsed = 0;

            @Override
            public void run() {
                ++elapsed;

                if (elapsed >= 8)
                {
                    Game.started = true;
                    CPlayer.forEachOnline(cp -> {
                        cp.getHandle().sendTitle(
                                "§d" + cp.getLocale().GAME_COUNTDOWN_STARTTITLE,
                                "§e" + cp.getLocale().GAME_COUNTDOWN_STARTSUBTITLE,
                                0,
                                20,
                                10
                                );
                    });

                    // Give loadouts to players
                    CPlayer.forEachOnline(cp -> {
                        final var loadout = cp.getKit().loadout(cp);
                        loadout.apply(cp);
                    });

                    // Register resources
                    RefillResource.addResource(new Arrows());
                    this.cancel();
                }
                else
                {
                    CPlayer.forEachOnline(cp -> {
                        cp.getHandle().sendTitle(
                                "§d" + cp.getLocale().GAME_COUNTDOWN_WAITTITLE,
                                MessageFormat.format(
                                "§e" + cp.getLocale().GAME_COUNTDOWN_WAITSUBTITLE,
                                        8 - elapsed),
                                0,
                                30,
                                10
                        );
                    });
                }
            }
        }.runTaskTimer(SheepWars.getPlugin(), 0, 20);
    }

    @EventHandler
    public void onMove(final PlayerMoveEvent ev)
    {
        if (!Game.hasStarted())
            ev.setCancelled(true);
    }

    @EventHandler
    public void onInteract(final PlayerInteractEvent ev)
    {
        if (!Game.hasStarted())
            ev.setCancelled(true);
    }

    @EventHandler
    public void onFish(final PlayerFishEvent ev) {
        if (!Game.hasStarted())
            ev.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(final BlockBreakEvent ev) {
        if (!Game.hasStarted())
            ev.setCancelled(true);
    }

    @EventHandler
    public void onConsume(final PlayerItemConsumeEvent ev) {
        if (!Game.hasStarted())
            ev.setCancelled(true);
    }

    @EventHandler
    public void onLaunch(final PlayerLaunchProjectileEvent ev) {
        if (!Game.hasStarted())
            ev.setCancelled(true);
    }

    @EventHandler
    public void onShoot(final EntityShootBowEvent ev) {
        if (!Game.hasStarted())
            ev.setCancelled(true);
    }

    @EventHandler
    public void onPickUp(final PlayerPickupItemEvent ev) {
        if (!Game.hasStarted())
            ev.setCancelled(true); }

    @EventHandler
    public void onReadyArrow(final PlayerReadyArrowEvent ev) {
        if (!Game.hasStarted())
            ev.setCancelled(true);
    }
}
