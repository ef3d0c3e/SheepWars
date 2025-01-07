package org.ef3d0c3e.sheepwars.sheeps.sheep;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.game.Game;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.FuseSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepItem;

public class FragmentationSheep extends FuseSheep {
    final static SheepItem ITEM = new SheepItem(FragmentationSheep.class);
    final static TextColor COLOR = TextColor.color(55, 55, 55);

    public static @NonNull ItemStack getItem(final @NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.GRAY_WOOL);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(
                Component.text(cp.getLocale().SHEEPS_FRAGMENTATION_NAME).color(COLOR)
        ));
        meta.setLore(Util.coloredLore("§7", cp.getLocale().SHEEPS_FRAGMENTATION_DESC));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

    public FragmentationSheep(@NonNull CPlayer owner) {
        super(owner, 50);
    }

    @Override
    public @NonNull Component getName(@NonNull CPlayer cp) {
        return Component.text(cp.getLocale().SHEEPS_FRAGMENTATION_NAME).color(COLOR);
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
    public void onFuseEnd() {
        final var loc = getLocation();
        loc.getWorld().createExplosion(loc, 4.5f, false, true, getBukkitHandle());

        for (int i = 0; i < 5 + Game.nextInt(3); ++i)
        {
            final Vector dir = new Vector(
                    Math.random() * 2.0 - 1.0,
                    Math.random() * 0.5 + 0.5,
                    Math.random() * 2.0 - 1.0
            );

            final var baby = new FragmentationSubSheep(getOwner());
            baby.spawn(loc, true);
            baby.launch(dir, 1, 1);
        }

    }
}
