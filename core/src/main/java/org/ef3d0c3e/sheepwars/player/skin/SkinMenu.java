package org.ef3d0c3e.sheepwars.player.skin;

import com.google.common.collect.Lists;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.gui.ScrollableGui;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class SkinMenu extends ScrollableGui
{
    @Override
    public void onClick(int id, @NonNull ItemStack item)
    {
        getPlayer().getHandle().closeInventory();
        getPlayer().getCosmetics().setSkin(Skin.skinList.get(id));
        Skin.updateSkin(getPlayer());
    }

    @Override
    public void onMenuClick(int id, @NonNull ItemStack item)
    {
        if (id != 3) return;

        getPlayer().getCosmetics().setSkin(null);
        getPlayer().getHandle().closeInventory();
    }

    static ArrayList<ItemStack> ITEMS = new ArrayList<>();
    static {
        Skin.skinList.forEach(skin -> ITEMS.add(skin.getDisplayItem()));
    }

    @Override
    protected @NonNull ArrayList<ItemStack> getItems()
    {
        return ITEMS;
    }

    @Override
    public @NonNull ArrayList<ItemStack> getMenu()
    {
        if (getPlayer().getCosmetics().getCurrentSkin() == null) return null;

        final ItemStack item = new ItemStack(Material.NETHER_STAR);
        final ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6" + MessageFormat.format(getPlayer().getLocale().SKIN_MENU, "§a" + getPlayer().getCosmetics().getCurrentSkin().getName()));
        meta.setLore(Util.coloredLore("§7", getPlayer().getLocale().SKIN_MENULORE));
        item.setItemMeta(meta);

        return Lists.newArrayList(null, null, null, item, null, null, null);
    }

    public SkinMenu(final CPlayer cp)
    {
        super(4, cp.getLocale().SKIN_PICKER, cp);
    }
}

