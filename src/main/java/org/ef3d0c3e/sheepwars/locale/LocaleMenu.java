package org.ef3d0c3e.sheepwars.locale;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ef3d0c3e.sheepwars.*;
import org.ef3d0c3e.sheepwars.items.ItemBase;
import org.ef3d0c3e.sheepwars.kits.KitMenu;
import org.ef3d0c3e.sheepwars.level.Lobby;
import org.ef3d0c3e.sheepwars.level.Map;

import java.text.MessageFormat;
import java.util.UUID;


@AllArgsConstructor
public class LocaleMenu implements IGui
{
	public CPlayer cp;

	@Override
	public void onGuiClose(final Player p) {}
	@Override
	public void onGuiDrag(final Player p, final InventoryDragEvent ev) {}

	@Override
	public void onGuiClick(Player p, ClickType click, int slot, ItemStack item)
	{
		if (item == null || item.getType() == Material.AIR)
			return;

		final Locale l = SheepWars.getLocaleManager().getLocales().get(slot);
		cp.setLocale(l);

		// Change item
		Lobby.updateInventory(cp);
		p.sendMessage(MessageFormat.format(cp.getLocale().LOCALE_CHANGED, cp.getLocale().CONFIG_DISPLAYNAME));
		p.closeInventory();
	}

	@Override
	public Inventory getInventory()
	{
		final Inventory inv = Bukkit.createInventory(this, (SheepWars.getLocaleManager().size() / 9 + 1) * 9, cp.getLocale().LOCALE_MENU_TITLE);

		for (Locale l : SheepWars.getLocaleManager().getLocales())
			inv.addItem(l.CONFIG_BANNER);

		return inv;
	}

	public static class LocaleMenuItem extends ItemBase
	{
		public LocaleMenuItem()
		{
			super();
		}

		@Override
		protected boolean onInteract(Player p, ItemStack item, Action action, EquipmentSlot hand, Block clicked, BlockFace clickedFace)
		{
			if (Game.hasStarted() || (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK))
				return true;

			p.openInventory(new LocaleMenu(CPlayer.getPlayer(p)).getInventory());

			return true;
		}

		@Override
		protected boolean onDrop(Player p, ItemStack item)
		{
			return true;
		}
	}

	static final private LocaleMenuItem LocaleItem = new LocaleMenuItem();
	public static ItemStack getItem(final CPlayer cp)
	{
		final ItemStack item = cp.getLocale().CONFIG_BANNER.clone();
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageFormat.format(cp.getLocale().ITEM_LANGUAGE, cp.getLocale().CONFIG_DISPLAYNAME) + " " + cp.getLocale().ITEM_RIGHTCLICK);
		meta.setLore(cp.getLocale().ITEM_LANGUAGELORE);
		item.setItemMeta(meta);

		SheepWars.getItemRegistry().registerItem(LocaleItem);
		return LocaleItem.apply(item);
	}
}
