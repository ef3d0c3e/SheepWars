package org.ef3d0c3e.sheepwars.gui;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.locale.LocalePath;
import org.ef3d0c3e.sheepwars.locale.Localized;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import javax.annotation.Nullable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@LocalePath("gui")
public abstract class ScrollableGui implements IGui
{
    private static Localized<String> NEXT;
    private static Localized<String> PREVIOUS;

    @Override
    public void onGuiClose(final Player p) {}
    @Override
    public void onGuiDrag(final Player p, final InventoryDragEvent ev) {}

    @Override
    public void onGuiClick(Player p, ClickType click, int slot, ItemStack item)
    {
        if (item == null || item.getType() == Material.AIR)
            return;


        if (slot == rows*9-1) // Next
        {
            ++page;
            p.openInventory(getInventory());
        }
        else if (slot == (rows-1)*9) // Previous
        {
            --page;
            p.openInventory(getInventory());
        }
        else if (slot < (rows-1)*9) // Items
        {
            onClick(page*(rows-1)*9 + slot, item);
        }
        else // Menu
        {
            onMenuClick(slot - (rows-1)*9 - 1, item);
        }
    }

    @Override
    public @NonNull Inventory getInventory()
    {
        final List<ItemStack> items = getItems();

        String title;
        if (items.size() > (rows-1)*9)
            title = MessageFormat.format("§9{0} {1}/{2}", this.title, page+1,
                    (int)Math.ceil(((double)items.size())/(9.0*(rows-1))));
        else
            title = "§9" + this.title;

        final Inventory inv = Bukkit.createInventory(this, rows*9, title);

        final int start = page*(rows-1)*9;
        int i = 0;
        while (true)
        {
            if (i+start == items.size()) break; // Last item
            if (i == (rows-1)*9) // Full
            {
                inv.setItem((rows)*9-1, getNextArrow());
                break;
            }

            inv.setItem(i, items.get(i+start));
            ++i;
        }

        if (page != 0) inv.setItem((rows-1)*9, getPreviousArrow());

        // Menu
        final List<ItemStack> menu = getMenu();
        if (menu != null)
        {
            int j = (rows-1)*9+1;
            for (final ItemStack item : menu)
                inv.setItem(j++, item);
        }


        return inv;
    }

    /**
     * Gets next arrow
     * @return Next arrow
     */
    public @NonNull ItemStack getNextArrow()
    {
        return Util.createSkull("§8" + NEXT.localize(player), null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODAwM2YyYzlmMDFkMmVkNThiOTAwMzE0ZDcyZmIyNTE1NmNhOWJmMTNiN2ZhZTMwOTMxMDRjZThmZTk2NGU5ZiJ9fX0=");
    }

    /**
     * Gets previous arrow
     * @return Previous arrow
     */
    public @NonNull ItemStack getPreviousArrow()
    {
        return Util.createSkull("§8" + PREVIOUS.localize(player), null, "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjcyZDEwZTQxMGRmOGQ1MTVhYmYzNWI3NjY2NmYxMWI3NDYzOTUwMGNmMGVlYjZlNzBkNDVmMzhiZDRiYmEzYSJ9fX0=");
    }

    /**
     * Called when an item is clicked
     * @param id Clicked item id
     * @param item Clicked item
     */
    public abstract void onClick(int id, final @NonNull ItemStack item);


    /**
     * Called when an item in the menu is clicked
     * @param id Clicked menu item id
     * @param item Clicked menu item
     */
    public void onMenuClick(int id, final @NonNull ItemStack item) {}

    /**
     * Gets the list of items to display in the gui
     * @note Only called on gui creation
     * @return The list of items
     */
    abstract protected @NonNull ArrayList<ItemStack> getItems();

    /**
     * Gets items to be display at the bottom center of the gui
     * @return Items [array size: 7] (or null if none)
     */
    protected @Nullable ArrayList<ItemStack> getMenu() { return null; }

    @Getter
    private int rows;
    @Getter
    private String title;
    @Getter
    private CPlayer player;

    private int page = 0;

    /**
     * Constructor
     * @param rows Number of rows
     * @param title Gui title
     * @param player Player the gui is shown to
     */
    public ScrollableGui(int rows, final @NonNull String title, final @NonNull CPlayer player)
    {
        this.rows = rows;
        this.title = title;
        this.player = player;
    }

}

