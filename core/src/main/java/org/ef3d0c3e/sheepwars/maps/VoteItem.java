package org.ef3d0c3e.sheepwars.maps;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.items.IItem;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.text.MessageFormat;
import java.util.List;

@LocalePath("vote.item")
public class VoteItem extends IItem {
    private static Localized<String> NAME;
    private static Localized<List<String>> LORE;

    public VoteItem()
    {
        super();
    }

    @Override
    protected boolean onDrop(Player p, ItemStack item) {
        return true;
    }

    @Override
    protected boolean onInteract(final Player p, final ItemStack item, final Action action, final EquipmentSlot hand, final Block clicked, final BlockFace face)
    {
        p.openInventory(new VoteMenu(CPlayer.get(p)).getInventory());
        return true;
    }

    static final public VoteItem ITEM = new VoteItem();

    /**
     * Gets item for player
     * @param cp Player to get item for
     * @return Item
     */
    public static @NonNull ItemStack getItem(final CPlayer cp)
    {
        final ItemStack item = new ItemStack(Material.PAPER);
        final ItemMeta meta = item.getItemMeta();
        var map = MapManager.getPlayerVote(cp);
        if (map == null)
            meta.setDisplayName(MessageFormat.format("§e{0} §7{1}", NAME.localize(cp), IItem.RIGHT_CLICK.localize(cp)));
        else
            meta.setDisplayName(MessageFormat.format("§e{0} §8: §a{1} §7{2}", NAME.localize(cp), map.getDisplayName(), IItem.RIGHT_CLICK.localize(cp)));
        meta.setLore(Util.coloredLore("§7", LORE.localize(cp)));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }
}
