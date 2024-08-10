package org.ef3d0c3e.sheepwars.level.lobby;

import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.items.IItem;
import org.ef3d0c3e.sheepwars.items.ItemFactory;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.text.MessageFormat;

public class RocketItem extends IItem
{
    public RocketItem()
    {
        super();
    }

    @Override
    protected boolean onInteract(final Player p, final ItemStack item, final Action action, final EquipmentSlot hand, final Block clicked, final BlockFace face)
    {
        if (p.getCooldown(item.getType()) != 0) return true;

        p.getLocation().getWorld().playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 12.f, 1.f);
        p.setVelocity(p.getVelocity()
                .clone().add(new Vector(0.0, 0.8, 0.0)));

        p.setCooldown(item.getType(), 30);
        return true;
    }

    @Override
    protected boolean onDrop(final Player p, final ItemStack item) { return true; }

    static final public RocketItem ITEM = new RocketItem();
    /**
     * Gets item for player
     * @param cp Player to get item for
     * @return Item
     */
    public static @NonNull ItemStack getItem(final CPlayer cp)
    {
        final ItemStack item = new ItemStack(Material.FIREWORK_ROCKET);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageFormat.format("§a{0} §7{1}", cp.getLocale().ITEMS_ROCKET, cp.getLocale().ITEMS_RIGHTCLICK));
        item.setItemMeta(meta);

        ItemFactory.registerItem(ITEM);
        return ITEM.apply(item);
    }
}
