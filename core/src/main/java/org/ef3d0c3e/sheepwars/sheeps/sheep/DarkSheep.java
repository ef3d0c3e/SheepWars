package org.ef3d0c3e.sheepwars.sheeps.sheep;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Vibration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.EffectSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepItem;

import java.util.List;

@LocalePath("sheeps.dark")
public class DarkSheep extends EffectSheep {
    static Localized<String> NAME;
    static Localized<List<String>> DESC;

    final static SheepItem ITEM = new SheepItem(DarkSheep.class);
    final static TextColor COLOR = TextColor.color(31, 31, 31);

    public static @NonNull ItemStack getItem(final @NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.BLACK_WOOL);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(
                Component.text(NAME.localize(cp)).color(COLOR)
        ));
        meta.setLore(Util.coloredLore("§7", DESC.localize(cp)));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

    public DarkSheep(@NonNull CPlayer owner) {
        super(owner, 1000, 100);
    }

    @Override
    public @NonNull Component getName(@NonNull CPlayer cp) {
        return Component.text(NAME.localize(cp)).color(COLOR);
    }

    @Override
    public void launchTrail(int time) {
        final var loc = getLocation();
        loc.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.BLACK;
    }

    @Override
    public void onGrounded(int groundTime) {
        if (groundTime % 20 != 0) return;

        final var center = getLocation().add(0.0, 0.5, 0.0);

        // Particles
        Util.runInCircle(center, new Vector(0, 1, 0), 7.0, 32, (loc, t, i) ->
        {
            center.getWorld().spawnParticle(Particle.VIBRATION, loc, 1, new Vibration(loc, new Vibration.Destination.EntityDestination(this.getBukkitHandle()), 20));
        });

        // Effect
        CPlayer.forEach(cp -> cp.isAlive() && cp.isOnline() && cp.getTeam() != getOwner().getTeam(), cp ->
        {
            if (cp.getHandle().getLocation().distance(center) >= 7.5)
                return;

            cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
        });
    }
}
