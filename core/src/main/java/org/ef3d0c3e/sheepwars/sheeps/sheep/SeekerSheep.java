package org.ef3d0c3e.sheepwars.sheeps.sheep;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.FuseSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepItem;
import org.ef3d0c3e.sheepwars.sheeps.ai.BaseGoal;
import org.ef3d0c3e.sheepwars.sheeps.ai.SeekerGoal;
import oshi.util.tuples.Pair;

import java.util.List;

@LocalePath("sheeps.seeker")
public class SeekerSheep extends FuseSheep {
    static Localized<String> NAME;
    static Localized<List<String>> DESC;

    final static SheepItem ITEM = new SheepItem(SeekerSheep.class);
    final static TextColor COLOR = TextColor.color(50, 119, 91);

    @Getter @Setter
    private CPlayer target = null;
    private int grounded = 0;

    public static @NonNull ItemStack getItem(final @NonNull CPlayer cp) {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var item = new ItemStack(Material.GREEN_WOOL);
        final var meta = item.getItemMeta();
        meta.setDisplayName(ser.serialize(
                Component.text(NAME.localize(cp)).color(COLOR)
        ));
        meta.setLore(Util.coloredLore("§7", DESC.localize(cp)));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

    public SeekerSheep(@NonNull CPlayer owner) {
        super(owner, 120);
    }

    @Override
    public @NonNull Component getName(@NonNull CPlayer cp) {
        return Component.text(NAME.localize(cp)).color(COLOR);
    }

    @Override
    public void launchTrail(int time) {
        final var loc = getLocation();
        loc.getWorld().spawnParticle(Particle.SNEEZE, loc.getX(), loc.getY(), loc.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
    }

    @Override
    public DyeColor getDyeColor() {
        return DyeColor.GREEN;
    }

    @Override
    public List<Pair<Integer, ? extends BaseGoal>> getCustomGoals()
    {
        return List.of(new Pair(0, new SeekerGoal(this)));
    }

    @Override
    public void onFuseEnd() {
        final var loc = getLocation();
        loc.getWorld().createExplosion(loc, 3.5f, false, true, getBukkitHandle());
    }

    @Override
    public void tick() {
        super.tick();

        if (onGround())
            ++grounded;
        if (grounded % 40 == 5)
        {
            final var loc = getLocation();
            target = null;
            CPlayer.forEach(cp -> cp.isOnline() && cp.isAlive() && cp.getTeam() != getOwner().getTeam(), cp -> {
                if (target == null)
                    target = cp;
                else if (loc.distanceSquared(cp.getHandle().getLocation()) <= loc.distanceSquared(target.getHandle().getLocation()))
                    target = cp;
            });
        }
    }
}
