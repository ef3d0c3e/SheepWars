package org.ef3d0c3e.sheepwars.loadouts;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.player.CPlayer;

public class Arrows extends RefillResource{
    public Arrows() {
        super(35);
    }

    @Override
    public void refillItem(@NonNull CPlayer cp) {
        final var inv = cp.getHandle().getInventory();

        // If player still has arrow, increment their amount
        for (final ItemStack item : inv) {
            if (item != null && item.getType() == Material.ARROW)
            {
                item.setAmount(item.getAmount() + 1);
                return;
            }
        }

        // Add to preferred slot if possible
        if (inv.getItem(8) == null)
            inv.setItem(8, new ItemStack(Material.ARROW));
        else
            inv.addItem(new ItemStack(Material.ARROW));
    }

    @Override
    public boolean refillPredicate(@NonNull CPlayer cp) {
        return COUNT_MATERIAL(cp, Material.ARROW, cp.getKit().loadout(cp).arrowCount());
    }
}
