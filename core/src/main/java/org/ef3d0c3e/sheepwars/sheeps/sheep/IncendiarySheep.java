package org.ef3d0c3e.sheepwars.sheeps.sheep;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.FuseSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepItem;

public class IncendiarySheep extends FuseSheep {
    final static SheepItem ITEM = new SheepItem(IncendiarySheep.class);
    final static TextColor COLOR = TextColor.color(255, 127, 31);

    public static @NonNull ItemStack getItem(final @NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.ORANGE_WOOL);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(
                Component.text(cp.getLocale().SHEEPS_INCENDIARY_NAME).color(COLOR)
        ));
        meta.setLore(Util.coloredLore("§7", cp.getLocale().SHEEPS_INCENDIARY_DESC));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

    public IncendiarySheep(@NonNull CPlayer owner) {
        super(owner, 50);
    }

    @Override
    public @NonNull Component getName(@NonNull CPlayer cp) {
        return Component.text(cp.getLocale().SHEEPS_INCENDIARY_NAME).color(COLOR);
    }

    @Override
    public void launchTrail(int time) {
        final var loc = getLocation();
        loc.getWorld().spawnParticle(Particle.FLAME, loc.getX(), loc.getY()+0.4, loc.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.ORANGE;
    }

    @Override
    public void onFuseEnd() {
        final var loc = getLocation();
        loc.getWorld().createExplosion(loc, 5.f, true, true, getBukkitHandle());

        for (int i = 0; i < 20; ++i)
        {
            FallingBlock fb = loc.getWorld().spawnFallingBlock(loc, Material.FIRE.createBlockData());
            fb.setDropItem(false);
            fb.setVelocity(new Vector(
                    (Math.random() - Math.random()) / 1.5,
                    Math.random(),
                    (Math.random() - Math.random()) / 1.5));
        }

    }
}
