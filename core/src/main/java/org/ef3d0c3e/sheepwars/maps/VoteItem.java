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
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.player.skin.SkinItem;
import org.ef3d0c3e.sheepwars.player.skin.SkinMenu;

import java.text.MessageFormat;

public class VoteItem extends IItem {
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
            meta.setDisplayName(MessageFormat.format("§e{0} §7{1}", cp.getLocale().ITEMS_VOTE, cp.getLocale().ITEMS_RIGHTCLICK));
        else
            meta.setDisplayName(MessageFormat.format("§e{0} §8: §a{1} §7{2}", cp.getLocale().ITEMS_VOTE, map.getDisplayName(), cp.getLocale().ITEMS_RIGHTCLICK));
        meta.setLore(Util.coloredLore("§7", cp.getLocale().ITEMS_VOTELORE));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }
}
