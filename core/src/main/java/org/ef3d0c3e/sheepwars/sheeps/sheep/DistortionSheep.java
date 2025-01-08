package org.ef3d0c3e.sheepwars.sheeps.sheep;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Vibration;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.EffectSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepItem;

import java.util.List;

@LocalePath("sheeps.distortion")
public class DistortionSheep extends EffectSheep {
    static Localized<String> NAME;
    static Localized<List<String>> DESC;

    final static SheepItem ITEM = new SheepItem(DistortionSheep.class);
    final static TextColor COLOR = TextColor.color(142, 11, 254);

    public static @NonNull ItemStack getItem(final @NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.PURPLE_WOOL);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(
                Component.text(NAME.localize(cp)).color(COLOR)
        ));
        meta.setLore(Util.coloredLore("§7", DESC.localize(cp)));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

    public DistortionSheep(@NonNull CPlayer owner) {
        super(owner, 1000, 200);
    }

    @Override
    public @NonNull Component getName(@NonNull CPlayer cp) {
        return Component.text(NAME.localize(cp)).color(COLOR);
    }

    @Override
    public void launchTrail(int time) {
        final var loc = getLocation();
        loc.getWorld().spawnParticle(Particle.PORTAL, loc, 1, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.PURPLE;
    }

    @Override
    public void onGrounded(int groundTime) {
        if (groundTime % 10 != 0) return;

        final var center = getLocation().add(0.0, 0.5, 0.0);

        // Particles
        Util.runInCircle(center, new Vector(0, 1, 0), 7.0, 15, (loc, t, i) ->
        {
            center.getWorld().spawnParticle(Particle.PORTAL, loc, 1, 0.0, 0.0, 0.0, 0.0);

        });

        if (groundTime % 20 != 0) return;

        // Affect blocks
        for (int i = 0; i < 30; ++i)
        {
            final double r = Math.random() * 6.0 + 1.0;
            final double t = (Math.random() - 0.5) * Math.PI;
            final double u = Math.random() * Math.PI * 2.0;

            final Block block = center.getWorld().getBlockAt(
                    (int)(center.getX() + r * Math.cos(t) * Math.sin(u)),
                    (int)(center.getY() + r * Math.sin(t) * Math.sin(u)),
                    (int)(center.getZ() + r * Math.cos(u))
            );
            if (block.getType() == Material.AIR ||
                    block.getType().getBlastResistance() >= Material.OBSIDIAN.getBlastResistance())
                continue;

            final FallingBlock fb = center.getWorld().spawnFallingBlock(block.getLocation(), block.getBlockData());
            fb.setDropItem(false);
            fb.setVelocity(new Vector(Math.random() - 0.5, Math.random(), Math.random() - 0.5));
            block.setType(Material.AIR);
        }

        // Affect entitites
        for (final Entity ent : center.getWorld().getEntities())
        {
            final double distSq = ent.getLocation().distanceSquared(center);
            if (distSq >= 49.0 || distSq <= 0.1 || BaseSheep.getInstance(ent) instanceof DistortionSheep)
                continue;
            if (ent instanceof Player)
            {
                final CPlayer cp = CPlayer.get((Player)ent);
                if (!cp.isAlive() || cp.getTeam() == getOwner().getTeam())
                    continue;
                cp.getCombatData().damageNow(getOwner());
            }

            final Vector dir = new Vector(
                    center.getX() - ent.getLocation().getX(),
                    center.getY() - ent.getLocation().getY(),
                    center.getZ() - ent.getLocation().getZ()
            ).normalize().multiply(Math.min(0.1 / Math.sqrt(distSq), 0.5));

            ent.setVelocity(ent.getVelocity().add(dir));
        }


    }
}
