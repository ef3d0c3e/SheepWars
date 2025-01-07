package org.ef3d0c3e.sheepwars.kits;

import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.gui.ScrollableGui;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.ArrayList;

@LocalePath("kit.menu")
public class KitMenu extends ScrollableGui {
    private static Localized<String> NAME;

    public KitMenu(@NonNull CPlayer player) {
        super(2, NAME.localize(player), player);
    }

    @Override
    public void onClick(int id, @NonNull ItemStack item) {
        final var kit = KitManager.getById(id);
        if (kit == null) return;

        getPlayer().setKit(kit);
    }

    @Override
    protected @NonNull ArrayList<ItemStack> getItems() {
        ArrayList<ItemStack> items = new ArrayList<>();
        KitManager.forEach((kit) -> items.add(kit.getIcon(getPlayer())));
        return items;
    }
}
