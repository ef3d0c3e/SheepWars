package org.ef3d0c3e.sheepwars.game;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import io.papermc.paper.event.player.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.CPlayerDeathEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.GameEndEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.ef3d0c3e.sheepwars.teams.Team.BLUE;
import static org.ef3d0c3e.sheepwars.teams.Team.RED;

@WantsListen(phase = WantsListen.Target.Game)
public class GameEvents implements Listener {

    /**
     * Registers timed events
     */
    public GameEvents() {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                applyLimbo(ticks);

                ++ticks;
            }
        }.runTaskTimer(SheepWars.getPlugin(), 0, 1);
    }

    /**
     * Applies limbo damages to players
     */
    private void applyLimbo(int ticks)
    {
        if (ticks % 15 != 0)
            return;
        CPlayer.forEach(cp -> cp.isOnline() && cp.isAlive(), cp -> {
            final var world = cp.getHandle().getWorld();
            if (world != Game.getLevel().getHandle())
                return;

            final float r = Game.getLevel().getLimboStrength(cp.getHandle().getLocation().getY());

            if (r >= 0.05f)
                cp.getHandle().damage(3.f * r);
            if (r == 1.f)
                Bukkit.getPluginManager().callEvent(new CPlayerDeathEvent(cp, (LivingEntity)null));
        });
    }

    /**
     * On player death, send message & updates team alive count
     * @param ev Event
     */
    @EventHandler
    public void onPlayerDeath(final CPlayerDeathEvent ev) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var victim = ev.getVictim();
        final var killer = ev.getPlayerKiller();

        // Update data
        victim.setAlive(false);
        victim.getHandle().setGameMode(GameMode.SPECTATOR);

        // Send death message
        if (killer == null)
            CPlayer.forEachOnline(cp -> {
                cp.getHandle().sendMessage(ser.serialize(
                        Component.text("{").color(TextColor.color(90, 90, 90))
                                .append(Component.text("☠").color(TextColor.color(220, 30, 20)))
                                .append(Component.text("} ").color(TextColor.color(90, 90, 90)))
                                .append(Component.text(victim.getHandle().getName()).color(victim.getTeam().getColor()))
                                .append(Component.text(" "))
                                .append(Component.text(cp.getLocale().GAME_KILL_DIED).color(TextColor.color(190, 190, 190))
                                )));
            });
        else
            CPlayer.forEachOnline(cp -> {
                cp.getHandle().sendMessage(ser.serialize(
                        Component.text("{").color(TextColor.color(90, 90, 90))
                                .append(Component.text("☠").color(TextColor.color(220, 30, 20)))
                                .append(Component.text("} ").color(TextColor.color(90, 90, 90)))
                                .append(Component.text(victim.getHandle().getName()).color(victim.getTeam().getColor()))
                                .append(Component.text(" "))
                                .append(Component.text(cp.getLocale().GAME_KILL_KILLED).color(TextColor.color(190, 190, 190))
                                        .append(Component.text(" "))
                                        .append(Component.text(killer.getHandle().getName()).color(killer.getTeam().getColor()))
                                )));
            });



        // Check if team has died too
        final var team = ev.getVictim().getTeam();
        team.setAliveCount(team.getAliveCount() - 1);

        if (team.getAliveCount() != 0) return;

        Bukkit.getPluginManager().callEvent(new GameEndEvent(team == RED ? BLUE : RED));
    }

    /**
     * Makes new players in spectator
     * @param ev The event
     */
    @EventHandler
    public void onJoin(final CPlayerJoinEvent ev) {
        if (ev.getPlayer().getTeam() == null || !ev.getPlayer().isAlive()) {
            ev.getPlayer().setAlive(false);
            ev.getPlayer().getHandle().setGameMode(GameMode.SPECTATOR);
            ev.getPlayer().getHandle().teleport(Game.getLevel().getSpawnLocation(ev.getPlayer()));
        }
        else
            ev.getPlayer().getHandle().setGameMode(GameMode.SURVIVAL);
    }

    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent ev) {
        ev.setFoodLevel(20);
        ev.setCancelled(true);
    }

    @EventHandler
    public void onBedEnter(final PlayerBedEnterEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onBedFailEnter(final PlayerBedFailEnterEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onDrop(final PlayerDropItemEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onFlowerPot(final PlayerFlowerPotManipulateEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onHarvest(final PlayerHarvestBlockEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onItemFrameInteract(final PlayerItemFrameChangeEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onRedstone(final BlockRedstoneEvent ev)
    {
        ev.setNewCurrent(0);
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent ev) {
        if (ev.getInventory().getLocation() == null)
            return;
        ev.setCancelled(true);
    }

    @EventHandler
    public void onOpenSign(final PlayerOpenSignEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onCraft(final CraftItemEvent ev) {
        ev.setCancelled(true);
    }

    @EventHandler
    public void onBlockMove(final BlockFromToEvent ev)
    {
        ev.setCancelled(true);
    }
}