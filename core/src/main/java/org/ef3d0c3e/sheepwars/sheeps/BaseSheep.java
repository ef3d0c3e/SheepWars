package org.ef3d0c3e.sheepwars.sheeps;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Sheep;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.game.Game;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.versions.AutoWrapper;

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
     * Handle to the custom sheep class
     */
    @Getter @Setter
    private Object handle;

    /**
     * Constructor
     * @param owner Owner of the sheep
     */
    public BaseSheep(final @NonNull CPlayer owner) {
        this.owner = owner;
    }

    /**
     * Spawns the sheep and sets the handle to the custom wrapper class
     * @param location The location to spawn the sheep at
     */
    public final void spawn(final @NonNull Location location) {
        WRAPPER.spawn(this, location);
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

    /**
     * Spawns launch particle trail
     * @param time The duration since launch (in ticks)
     */
    public abstract void launchTrail(final int time);

    /**
     * Gets the dye color of the sheep
     * @return The dye color
     */
    public abstract DyeColor getColor();

    /**
     * Performs logic for the sheep every ticks
     */
    public abstract void tick();
}
