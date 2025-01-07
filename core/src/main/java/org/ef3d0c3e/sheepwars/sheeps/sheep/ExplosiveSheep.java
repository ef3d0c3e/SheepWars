package org.ef3d0c3e.sheepwars.sheeps.sheep;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.FuseSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepItem;

import java.util.List;

@LocalePath("sheeps.explosive")
public class ExplosiveSheep extends FuseSheep {
    static Localized<String> NAME;
    static Localized<List<String>> DESC;

    final static SheepItem ITEM = new SheepItem(ExplosiveSheep.class);
    final static TextColor COLOR = TextColor.color(255, 0, 0);

    public static @NonNull ItemStack getItem(final @NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.RED_WOOL);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(
            Component.text(NAME.localize(cp)).color(COLOR)
        ));
        meta.setLore(Util.coloredLore("§7", DESC.localize(cp)));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

    public ExplosiveSheep(@NonNull CPlayer owner) {
        super(owner, 50);
    }

    @Override
    public @NonNull Component getName(@NonNull CPlayer cp) {
        return Component.text(NAME.localize(cp)).color(COLOR);
    }

    @Override
    public void launchTrail(int time) {
        final var loc = getLocation();
        loc.getWorld().spawnParticle(Particle.SMOKE, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        loc.getWorld().spawnParticle(Particle.SMOKE, loc.getX(), loc.getY(), loc.getZ(), 3, 0.2, 0.2, 0.2, 0.0);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.RED;
    }

    @Override
    public void onFuseEnd() {
        final var loc = getLocation();
        loc.getWorld().createExplosion(loc, 6.f, false, true, getBukkitHandle());
    }

}
