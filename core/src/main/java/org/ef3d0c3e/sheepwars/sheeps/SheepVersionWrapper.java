package org.ef3d0c3e.sheepwars.sheeps;

import lombok.NonNull;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;


public interface SheepVersionWrapper {
    @Nullable
    BaseSheep getInstance(final @NonNull Entity ent);
    void spawn(final @NonNull BaseSheep sheep, final @NonNull Location location, final boolean baby);
    void remove(final @NonNull BaseSheep sheep);
    void launch(final @NonNull BaseSheep sheep, final Vector direction, final double strength, final int duration);
    @NonNull Location getLocation(final @NonNull BaseSheep sheep);
    boolean onGround(final @NonNull BaseSheep sheep);
    void setColor(final @NonNull BaseSheep sheep, final @NonNull DyeColor color);
    int getNetworkId(final @NonNull BaseSheep sheep);
}
