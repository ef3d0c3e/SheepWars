package org.ef3d0c3e.sheepwars.maps;

import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.gui.ScrollableGui;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@LocalePath("vote.menu")
public class VoteMenu extends ScrollableGui {
    private static Localized<String> NAME;
    private static Localized<String> DESC;

    /**
     * Constructor
     *
     * @param player Player the gui is shown to
     */
    public VoteMenu(@NonNull CPlayer player) {
        super(2, NAME.localize(player), player);
    }

    @Override
    public void onClick(int id, @NonNull ItemStack item) {
        final Map map = MapManager.getMap(id);
        if (map == null) return;

        MapManager.setPlayerVote(getPlayer(), map);
        getPlayer().getHandle().closeInventory();
    }

    @Override
    protected @NonNull ArrayList<ItemStack> getItems() {
        // Get counts & voted map
        var votes = MapManager.getVotes();
        var mapVote = MapManager.getPlayerVote(getPlayer());

        ArrayList<ItemStack> items = new ArrayList<>();
        MapManager.forEach((map) -> {
            final ItemStack item = new ItemStack(map.getIcon());
            final ItemMeta meta = item.getItemMeta();
            if (mapVote != null && mapVote == map)
                meta.setDisplayName("§8» §d" + map.getDisplayName() + " §8«");
            else
                meta.setDisplayName("§d" + map.getDisplayName());
            meta.setLore(List.of("§7" + MessageFormat.format(DESC.localize(getPlayer()), votes.get(map))));
            item.setItemMeta(meta);
            int count = Math.min(Math.max(votes.get(map), 1), 64);
            item.setAmount(count);
            items.add(item);
        });
        return items;
    }
}
