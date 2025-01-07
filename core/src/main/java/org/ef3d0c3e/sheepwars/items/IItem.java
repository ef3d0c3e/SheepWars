package org.ef3d0c3e.sheepwars.items;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;

import javax.annotation.Nullable;
import java.util.UUID;

// TODO: Add option to prevent using the item in crafting recipes
@LocalePath("items")
public abstract class IItem
{
    public static Localized<String> RIGHT_CLICK;

    /**
     * UUID persistant tag
     */
    static private final PersistentDataType<byte[], UUID> TAG_UUID = new UUIDItemTagType();

    /**
     * Key used to store item's id
     */
    static protected final NamespacedKey KEY_ID = new NamespacedKey(SheepWars.getPlugin(), "hunt_uuid");

    /**
     * Item's id
     */
    @Getter(AccessLevel.MODULE)
    private UUID id;

    /**
     * Constructor
     */
    public IItem()
    {
        this.id = UUID.randomUUID();
    }

    public IItem(final UUID id)
    {
        this.id = id;
    }

    /**
     * Gets item's key
     * @param item Item to get id of
     * @return Item's id or null
     */
    protected static @Nullable UUID getId(final ItemStack item)
    {
        final ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return null;
        final PersistentDataContainer container = meta.getPersistentDataContainer();
        if (container == null)
            return null;

        return container.getOrDefault(KEY_ID, TAG_UUID, null);
    }

    public static boolean replace(final @NonNull Inventory inv, final @NonNull IItem item, final @NonNull ItemStack replace)
    {
        boolean replaced = false;
        for (int i = 0; i < inv.getSize(); ++i)
        {
            if (inv.getItem(i) == null || inv.getItem(i).getItemMeta() == null) continue;
            final IItem base = ItemFactory.getItem(inv.getItem(i));
            if (base == null || base.getId() != item.getId()) continue;

            inv.setItem(i, replace);
            replaced = true;
        }

        return replaced;
    }

    /**
     * Applies custom item to itemstack
     * @param item Item to apply custom item to
     * @return ItemStack
     */
    public ItemStack apply(final ItemStack item)
    {
        final ItemStack custom = item.clone();
        final ItemMeta meta = custom.getItemMeta();
        meta.getPersistentDataContainer().set(KEY_ID, TAG_UUID, getId());
        custom.setItemMeta(meta);

        return custom;
    }

    protected abstract boolean onDrop(final Player p, final ItemStack item);
    protected abstract boolean onInteract(final Player p, final ItemStack item, final Action action, final EquipmentSlot hand, final Block clicked, final BlockFace clickedFace);
    protected boolean onEnchant(final EnchantItemEvent ev) { return true; }
    protected boolean onPickup(final LivingEntity ent, final Item item, final int remaining) { return false; }
    protected boolean onDespawn(final Item item) { return false; }

    /**
     * Events to handle custom items
     * Note: Does not handle events related to entities/inventories not linked to a player
     */
    @WantsListen(phase = WantsListen.Target.Always)
    public static class Events implements Listener
    {
        /**
         * Handles custom item interactions
         * @param ev Event
         */
        @EventHandler
        public void onInteract(final PlayerInteractEvent ev)
        {
            if (ev.getItem() == null)
                return;
            final IItem item = ItemFactory.getItem(ev.getItem());
            if (item == null)
                return;

            ev.setCancelled(item.onInteract(
                    ev.getPlayer(),
                    ev.getItem(),
                    ev.getAction(),
                    ev.getHand(),
                    ev.getClickedBlock(),
                    ev.getBlockFace()
            ));
        }

        /**
         * Handles custom item dropping
         * @param ev Event
         */
        @EventHandler
        public void onDrop(final PlayerDropItemEvent ev)
        {
            final ItemStack stack = ev.getItemDrop().getItemStack();

            final IItem item = ItemFactory.getItem(stack);
            if (item == null)
                return;

            ev.setCancelled(item.onDrop(ev.getPlayer(), stack));
        }

        /**
         * Handles custom item enchant
         * @param ev Event
         */
        @EventHandler
        public void onEnchant(final EnchantItemEvent ev)
        {
            final IItem item = ItemFactory.getItem(ev.getItem());
            if (item == null)
                return;

            item.onEnchant(ev);
        }

        /**
         * Handles custom item pickup
         * @param ev Event
         */
        @EventHandler
        public void onPickup(final EntityPickupItemEvent ev)
        {
            final IItem item = ItemFactory.getItem(ev.getItem().getItemStack());
            if (item == null)
                return;

            ev.setCancelled(item.onPickup(
                    ev.getEntity(),
                    ev.getItem(),
                    ev.getRemaining()
            ));
        }

        /**
         * Handles custom item despawn
         * @param ev Event
         */
        @EventHandler
        public void onDespawn(final ItemDespawnEvent ev)
        {
            final IItem item = ItemFactory.getItem(ev.getEntity().getItemStack());
            if (item == null)
                return;

            ev.setCancelled(item.onDespawn(ev.getEntity()));
        }
    }
}
