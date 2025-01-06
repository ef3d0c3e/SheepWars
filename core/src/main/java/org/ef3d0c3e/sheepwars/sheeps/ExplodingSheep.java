package org.ef3d0c3e.sheepwars.sheeps;

import lombok.NonNull;
import org.bukkit.DyeColor;
import org.bukkit.Particle;
import org.bukkit.World;
import org.ef3d0c3e.sheepwars.player.CPlayer;

public class ExplodingSheep extends FuseSheep {
    public ExplodingSheep(@NonNull CPlayer owner) {
        super(owner, 40);
    }

    @Override
    public void launchTrail(int time) {
        final var loc = getLocation();
        loc.getWorld().spawnParticle(Particle.SMOKE, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        loc.getWorld().spawnParticle(Particle.SMOKE, loc.getX(), loc.getY(), loc.getZ(), 3, 0.2, 0.2, 0.2, 0.0);
    }

    @Override
    public DyeColor getColor() {
        return DyeColor.RED;
    }

    @Override
    public void onFuseEnd() {
        final var loc = getLocation();
        loc.getWorld().createExplosion(loc, 6.f);
    }

}
