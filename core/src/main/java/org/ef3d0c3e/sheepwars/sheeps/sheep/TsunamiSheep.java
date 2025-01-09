package org.ef3d0c3e.sheepwars.sheeps.sheep;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.data.Levelled;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepItem;
import org.bukkit.block.Block;

import java.util.List;

@LocalePath("sheeps.tsunami")
public class TsunamiSheep extends BaseSheep {
    static Localized<String> NAME;
    static Localized<List<String>> DESC;

        final static SheepItem ITEM = new SheepItem(TsunamiSheep.class);
    final static TextColor COLOR = TextColor.color(74, 72, 232);

    private int grounded = 0;

    public static @NonNull ItemStack getItem(final @NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.BLUE_WOOL);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(
                Component.text(NAME.localize(cp)).color(COLOR)
        ));
        meta.setLore(Util.coloredLore("§7", DESC.localize(cp)));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

    public TsunamiSheep(@NonNull CPlayer owner) {
        super(owner);
    }

    @Override
    public @NonNull Component getName(@NonNull CPlayer cp) {
        return Component.text(NAME.localize(cp)).color(COLOR);
    }

    @Override
    public void launchTrail(int time) {
        final var loc = getLocation();
        loc.getWorld().spawnParticle(Particle.BUBBLE, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        loc.getWorld().spawnParticle(Particle.SPLASH, loc.getX(), loc.getY(), loc.getZ(), 3, 0.2, 0.2, 0.2, 0.0);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLUE;
    }

    @Override
    public void tick() {
        if (onGround())
            ++grounded;

        if (getLifetime() >= 100 || grounded >= 5) {
            remove();
            return;
        }

        //if (getLifetime() % 5 != 0) return;

        final var center = getLocation().add(0.0, 0.5, 0.0);

        final var vel = getBukkitHandle().getVelocity();
        final var dir = vel.normalize().crossProduct(new Vector(0.0, 1.0, 0.0));
        final var bpt = Math.ceil(vel.length() * 1.3);

        for (int l = 0; l <= bpt; l++)
        {
            final Vector pos = center.toVector().add(vel.clone().multiply(1.3 * ((double)l) / bpt));

            for (int i = -3; i <= 3; ++i)
            {
                final var loc = new Location(center.getWorld(), pos.getX() + dir.getX() * i, pos.getY() + dir.getY() * i - 1, pos.getZ() + dir.getZ() * i);
                final Block block = loc.getBlock();
                if (block.getType() != Material.AIR)
                    continue;
                block.setType(Material.WATER);
                final var water = (Levelled)block.getBlockData();
                water.setLevel(1);
                block.setBlockData(water);
            }

        }
    }
}
