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
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepItem;

public class FragmentationSubSheep extends BaseSheep {
    final static SheepItem ITEM = new SheepItem(FragmentationSubSheep.class);
    final static TextColor COLOR = FragmentationSheep.COLOR;

    public static @NonNull ItemStack getItem(final @NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.GRAY_WOOL);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(
                Component.text(FragmentationSheep.NAME.localize(cp)).color(COLOR)
        ));
        meta.setLore(Util.coloredLore("§7", FragmentationSheep.DESC.localize(cp)));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

    public FragmentationSubSheep(@NonNull CPlayer owner) {
        super(owner);
    }

    @Override
    public @NonNull Component getName(@NonNull CPlayer cp) {
        return Component.text(FragmentationSheep.NAME.localize(cp)).color(COLOR);
    }

    @Override
    public void launchTrail(int time) {
        final var loc = getLocation();
        loc.getWorld().spawnParticle(Particle.EXPLOSION, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GRAY;
    }

    @Override
    public void onSpawn() {
        getBukkitHandle().setInvulnerable(true);
    }

    @Override
    public void tick() {
        if (onGround())
        {
            remove();
            final var loc = getLocation();
            loc.getWorld().createExplosion(loc, 2.5f, false, true, getBukkitHandle());
        }
        else if (getLifetime() == 160)
        {
            remove();
        }
    }
}
