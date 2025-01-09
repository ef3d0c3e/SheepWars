package org.ef3d0c3e.sheepwars.sheeps.sheep;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.FuseSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepItem;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@LocalePath("sheeps.swap")
public class SwapSheep extends FuseSheep {
    static Localized<String> NAME;
    static Localized<List<String>> DESC;

    final static SheepItem ITEM = new SheepItem(SwapSheep.class);
    final static TextColor COLOR = TextColor.color(184, 136, 236);

    public static @NonNull ItemStack getItem(final @NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.MAGENTA_WOOL);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(
            Component.text(NAME.localize(cp)).color(COLOR)
        ));
        meta.setLore(Util.coloredLore("§7", DESC.localize(cp)));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

    public SwapSheep(@NonNull CPlayer owner) {
        super(owner, 50);
    }

    @Override
    public @NonNull Component getName(@NonNull CPlayer cp) {
        return Component.text(NAME.localize(cp)).color(COLOR);
    }

    @Override
    public void launchTrail(int time) {
        final var loc = getLocation();
        loc.getWorld().spawnParticle(Particle.SOUL, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.MAGENTA;
    }

    @Override
    public void onFuse(final int fuseTime)
    {
        if (fuseTime % 10 != 0) return;

        final var center = getLocation().add(0.0, 0.5, 0.0);
        Util.runInCircle(center, new Vector(0, 1, 0), 9.0, 24, (loc, t, i) ->
        {
            center.getWorld().spawnParticle(Particle.SOUL, loc, 1, 0.0, 0.0, 0.0, 0.0);
        });
        Util.runInCircle(center, new Vector(0, 1, 0), 6.0, 12, (loc, t, i) ->
        {
            center.getWorld().spawnParticle(Particle.SOUL, loc, 1, 0.0, 0.0, 0.0, 0.0);
        });
    }

    @Override
    public void onFuseEnd() {
        final var center = getLocation().add(0.0, 0.5, 0.0);

        final AtomicReference<CPlayer> target = new AtomicReference<>(null);
        CPlayer.forEach(cp -> cp.isOnline() && cp.isAlive() && cp.getTeam() != getOwner().getTeam(), cp ->
        {
            if (target.get() == null)
                target.set(cp);
            else if (center.distanceSquared(cp.getHandle().getLocation()) <= center.distanceSquared(target.get().getHandle().getLocation()))
                target.set(cp);
        });
        if (target.get() != null && target.get().getHandle().getLocation().distance(center) <= 9)
        {
            final var targetLoc = target.get().getHandle().getLocation();
            center.getWorld().playSound(center, Sound.ENTITY_ENDERMAN_TELEPORT, SoundCategory.MASTER, 10.f, 1.f);
            target.get().getHandle().teleport(getOwner().getHandle().getLocation());
            getOwner().getHandle().teleport(targetLoc);

            target.get().getCombatData().damageNow(getOwner());
        }

    }

}
