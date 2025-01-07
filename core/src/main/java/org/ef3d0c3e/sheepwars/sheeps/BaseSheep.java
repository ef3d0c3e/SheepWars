package org.ef3d0c3e.sheepwars.sheeps;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.game.Game;
import org.ef3d0c3e.sheepwars.hologram.Hologram;
import org.ef3d0c3e.sheepwars.hologram.HologramTextComponent;
import org.ef3d0c3e.sheepwars.hologram.PassengerHologram;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.versions.AutoWrapper;

import javax.annotation.Nullable;
import java.awt.*;

public abstract class BaseSheep {
    @AutoWrapper(name = "Sheep")
    public static SheepVersionWrapper WRAPPER;

    /**
     * Lifetime of the sheep, incremented by the custom class
     */
    @Getter @Setter
    protected int lifetime = 0;

    /**
     * Owner of the sheep
     */
    @Getter
    private final @NonNull CPlayer owner;

    /**
     * The sheep's nametag
     */
    @Getter
    private PassengerHologram nametag = null;

    /**
     * Handle to the custom sheep class
     */
    @Getter @Setter
    private Object handle;

    /**
     * Bukkit handle to the sheep
     */
    @Getter @Setter
    private LivingEntity bukkitHandle;

    /**
     * Constructor
     * @param owner Owner of the sheep
     */
    public BaseSheep(final @NonNull CPlayer owner) {
        this.owner = owner;
    }

    /**
     * Gets the base sheep instance from a Bukkit entity
     * @param ent The entity to get the handle from
     * @return The BaseSheep handle, or null
     */
    public static final @Nullable BaseSheep getInstance(final @NonNull Entity ent) {
        return WRAPPER.getInstance(ent);
    }

    /**
     * Spawns the sheep and sets the handle to the custom wrapper class
     * @param location The location to spawn the sheep at
     * @param baby Whether to create a baby sheep
     */
    public final void spawn(final @NonNull Location location, final boolean baby) {
        WRAPPER.spawn(this, location, baby);

        nametag = new PassengerHologram(getNetworkId()) {
            @Override
            public @NonNull Location getLocation(@NonNull CPlayer cp) {
                return cp.getHandle().getLocation();
            }
        };
        nametag.addComponent(new HologramTextComponent(new Vector(0, 0, 0)) {
            @Override
            protected @NonNull Component getText(@NonNull CPlayer cp) {
                return getName(cp);
            }
        });

        // Send nametag
        CPlayer.forEachOnline(nametag::send);

        onSpawn();
    }

    /**
     * Removes the sheep
     */
    public final void remove() {
        WRAPPER.remove(this);
    }

    /**
     * Launches the sheep
     * @param direction Direction to launch the sheep to
     * @param strength Strenght of the launch
     * @param duration Duration of the launch
     */
    public final void launch(final Vector direction, final double strength, final int duration)
    {
        WRAPPER.launch(this, direction, strength, duration);
    }

    /**
     * Gets the sheep's position
     * @return Position of the sheep
     */
    public final @NonNull Location getLocation() {
        return WRAPPER.getLocation(this);
    }

    /**
     * Gets whether the sheep is on the ground
     * @return true if onGround, false otherwise
     */
    public final boolean onGround() {
        return WRAPPER.onGround(this);
    }

    public final void setColor(final @NonNull DyeColor color) {
        WRAPPER.setColor(this, color);
    }

    public final int getNetworkId()
    {
        return WRAPPER.getNetworkId(this);
    }

    /**
     * Gets the height at which the sheep should despawn
     * @return The despawn height of the sheep
     *
     * This method must be callable in lobby Phase, so it defaults to 0 when the game level is null
     */
    public int getDispawnHeight() {
        final var level = Game.getLevel();
        if (level != null)
            return level.getMap().getLimboEnd();
        else
            return 0;
    }

    public abstract @NonNull Component getName(final @NonNull CPlayer cp);

    /**
     * Spawns launch particle trail
     * @param time The duration since launch (in ticks)
     */
    public abstract void launchTrail(final int time);

    /**
     * Gets the dye color of the sheep
     * @return The dye color
     */
    public abstract DyeColor getDyeColor();

    /**
     * Performs logic for the sheep every ticks
     */
    public abstract void tick();

    /**
     * Method called when the sheep spawns
     */
    public void onSpawn() {}

    /**
     * Method called when the sheep is removed (killed, dispawns, ...)
     */
    public void onRemove() {
        // Unsend nametag
        CPlayer.forEachOnline(nametag::unsend);
    }

    @WantsListen(phase = WantsListen.Target.Always)
    public static class Events implements Listener
    {
        /**
         * Resend hologram to the player
         * @param ev Event
         */
        @EventHandler
        public void onJoin(final CPlayerJoinEvent ev) {
            final var world = ev.getPlayer().getHandle().getWorld();

            world.getLivingEntities().forEach((ent) -> {
                final var sheep = WRAPPER.getInstance(ent);
                if (sheep == null) return;
                sheep.nametag.send(ev.getPlayer());
            });
        }

        /**
         * Resend hologram to the player
         * @param ev Event
         */
        @EventHandler
        public void onWorldChange(final PlayerLevelChangeEvent ev)
        {
            final var world = ev.getPlayer().getWorld();
            final var cp = CPlayer.get(ev.getPlayer());

            world.getLivingEntities().forEach((ent) -> {
                final var sheep = WRAPPER.getInstance(ent);
                if (sheep == null) return;
                sheep.nametag.send(cp);
            });
        }
    }
}
