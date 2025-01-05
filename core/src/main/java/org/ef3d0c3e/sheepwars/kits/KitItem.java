package org.ef3d0c3e.sheepwars.kits;

import lombok.NonNull;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

import java.text.MessageFormat;

public class KitItem extends IItem {
    @Override
    protected boolean onDrop(Player p, ItemStack item) {
        return true;
    }

    @Override
    protected boolean onInteract(Player p, ItemStack item, Action action, EquipmentSlot hand, Block clicked, BlockFace clickedFace) {
        p.openInventory(new KitMenu(CPlayer.get(p)).getInventory());
        return true;
    }

    static final public KitItem ITEM = new KitItem();

    /**
     * Gets item for player
     * @param cp Player to get item for
     * @return Item
     */
    public static @NonNull ItemStack getItem(final CPlayer cp)
    {
        final ItemStack item = new ItemStack(Material.KNOWLEDGE_BOOK);
        final ItemMeta meta = item.getItemMeta();
        if (cp.getKit() == null)
            meta.setDisplayName(MessageFormat.format("§e{0} §7{1}", cp.getLocale().ITEMS_KIT, cp.getLocale().ITEMS_RIGHTCLICK));
        else {
            final var ser = LegacyComponentSerializer.legacy('§');
            meta.setDisplayName(MessageFormat.format("§e{0} §8: {1} §7{2}", cp.getLocale().ITEMS_VOTE, ser.serialize(
                    cp.getKit().getColoredName(cp)
            ), cp.getLocale().ITEMS_RIGHTCLICK));
        }
        meta.setLore(Util.coloredLore("§7", cp.getLocale().ITEMS_KITLORE));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }
}
