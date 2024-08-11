package org.ef3d0c3e.sheepwars.teams;

import lombok.NonNull;
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

public class TeamItem extends IItem {
    public TeamItem()
    {
        super();
    }

    @Override
    protected boolean onInteract(final Player p, final ItemStack item, final Action action, final EquipmentSlot hand, final Block clicked, final BlockFace face)
    {
        final CPlayer cp = CPlayer.get(p);
        if (cp.getTeam() == Team.RED) {
            Team.setPlayerTeam(cp, Team.BLUE);
        } else {
            Team.setPlayerTeam(cp, Team.RED);
        }
        return true;
    }

    @Override
    protected boolean onDrop(final Player p, final ItemStack item) { return true; }

    static final public TeamItem ITEM = new TeamItem();
    /**
     * Gets item for player
     * @param cp Player to get item for
     * @return Item
     */
    public static @NonNull ItemStack getItem(final CPlayer cp)
    {
        final ItemStack item = new ItemStack(cp.getTeam().getBanner());
        final ItemMeta meta = item.getItemMeta();
        if (cp.getTeam() == null)
            meta.setDisplayName(MessageFormat.format("§a{0} §7{1}", cp.getLocale().ITEMS_TEAM, cp.getLocale().ITEMS_RIGHTCLICK));
        else
            meta.setDisplayName(MessageFormat.format("§a{0}§8 : {1} §7{2}", cp.getLocale().ITEMS_TEAM, cp.getTeam().getColoredName(cp), cp.getLocale().ITEMS_RIGHTCLICK));
        meta.setLore(Util.coloredLore("§7", cp.getLocale().ITEMS_TEAMLORE));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }

}
