package org.ef3d0c3e.sheepwars.sheeps;

import lombok.NonNull;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;


public interface SheepVersionWrapper {
    void spawn(final @NonNull BaseSheep sheep, final @NonNull Location location);
    void remove(final @NonNull BaseSheep sheep);
    void launch(final @NonNull BaseSheep sheep, final Vector direction, final double strength, final int duration);
    @NonNull Location getLocation(final @NonNull BaseSheep sheep);
    boolean onGround(final @NonNull BaseSheep sheep);
    void setColor(final @NonNull BaseSheep sheep, final @NonNull DyeColor color);
}
