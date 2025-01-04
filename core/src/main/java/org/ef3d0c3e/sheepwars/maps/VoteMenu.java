package org.ef3d0c3e.sheepwars.maps;

import lombok.NonNull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.gui.ScrollableGui;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VoteMenu extends ScrollableGui {
    /**
     * Constructor
     *
     * @param player Player the gui is shown to
     */
    public VoteMenu(@NonNull CPlayer player) {
        super(2, player.getLocale().VOTE_PICKER, player);
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
            meta.setLore(List.of("§7" + MessageFormat.format(getPlayer().getLocale().ITEMS_VOTEDESC, votes.get(map))));
            item.setItemMeta(meta);
            items.add(item);
        });
        return items;
    }
}
