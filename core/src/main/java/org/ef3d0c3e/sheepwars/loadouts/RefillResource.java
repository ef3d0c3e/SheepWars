package org.ef3d0c3e.sheepwars.loadouts;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public abstract class RefillResource {
    /**
     * The registered resources
     */
    private final static ArrayList<@NonNull RefillResource> RESOURCES = new ArrayList<>();

    /**
     * A refill predicate that counts the number of items with a specific material
     * @param mat The material to count for
     * @param max The maximum amount of items that can be of that material
     * @return true If player has fewer items of type mat than max
     */
    static boolean COUNT_MATERIAL(final @NonNull CPlayer cp, @NonNull Material mat, int max) {
        final var inv = cp.getHandle().getInventory();
        AtomicInteger count = new AtomicInteger();
        inv.forEach(item -> {
            if (item.getType() == mat) {
                count.set(count.get() + item.getAmount());
            }
        });
        return count.get() < max;
    }

    /**
     * Interface to refill
     */
    public abstract void refillItem(final @NonNull CPlayer cp);

    /**
     * Interface to determine whether a player can have more of the resource
     */
    public abstract boolean refillPredicate(final @NonNull CPlayer cp);


    /**
     * Refill delay in ticks
     */
    @Getter @Setter
    private int delay;

    /**
     * Adds a {@link RefillResource} to the system
     * @param resource Resource to add
     */
    public static void addResource(final @NonNull RefillResource resource) {
        RESOURCES.add(resource);
    }

    /**
     * Ticks the {@link RefillResource} system
     * @param tick The number of elapsed ticks
     */
    public static void tick(final int tick)
    {
        for (final @NonNull RefillResource resource : RESOURCES) {
            if (tick % resource.delay != 0) continue;

            CPlayer.forEachOnline(cp -> {
                if (resource.refillPredicate(cp)) {
                    resource.refillItem(cp);
                }
            });
        }
    }
}
