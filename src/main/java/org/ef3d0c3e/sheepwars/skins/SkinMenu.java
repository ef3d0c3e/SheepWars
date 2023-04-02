package org.ef3d0c3e.sheepwars.skins;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Items;

import java.text.MessageFormat;
import java.util.Arrays;

public class SkinMenu
{
	public static class Events implements Listener
	{
		@EventHandler
		public void onMenuClick(final InventoryClickEvent ev)
		{
			if (!ev.getView().getTitle().startsWith(name))
				return;
			ev.setCancelled(true);
			if (ev.getClickedInventory() != ev.getView().getTopInventory())
				return;

			if (ev.getCurrentItem() == null || ev.getCurrentItem().getType() == Material.AIR)
				return;
			final CPlayer cp = CPlayer.getPlayer((Player)ev.getWhoClicked());
			if (ev.getInventory() == cp.getHandle().getInventory())
				return;

			final String invName = ev.getView().getTitle();
			final int page = Integer.valueOf(invName.substring(invName.indexOf('-')+2, invName.indexOf('/'))) - 1;
			if (ev.getRawSlot() == 40)
			{
				cp.setSkin(-1);
				cp.getHandle().openInventory(getInventory(cp, page));

				// Change items
				Items.remove(cp, Items.ID.SKIN);
				if (cp.getHandle().getInventory().getItem(7) == null)
					cp.getHandle().getInventory().setItem(7, Items.getSkinItem(cp));
				else
					cp.getHandle().getInventory().addItem(Items.getSkinItem(cp));
			}
			else if (ev.getRawSlot() == 36)
				cp.getHandle().openInventory(getInventory(cp, page-1));
			else if (ev.getRawSlot() == 44)
				cp.getHandle().openInventory(getInventory(cp, page+1));
			else
			{
				cp.setSkin(ev.getRawSlot() + page * 36);
				Skin.updatePlayerSkin(cp);
				cp.getHandle().openInventory(getInventory(cp, page));

				// Change items
				Items.remove(cp, Items.ID.SKIN);
				if (cp.getHandle().getInventory().getItem(7) == null)
					cp.getHandle().getInventory().setItem(7, Items.getSkinItem(cp));
				else
					cp.getHandle().getInventory().addItem(Items.getSkinItem(cp));
			}
		}

		@EventHandler
		public void onInventoryDrag(final InventoryInteractEvent ev)
		{
			if (!ev.getView().getTitle().startsWith(name))
				return;

			ev.setCancelled(true);
		}

		@EventHandler
		public void onStatItemUse(final PlayerInteractEvent ev)
		{
			if (ev.getAction() != Action.RIGHT_CLICK_AIR && ev.getAction() != Action.RIGHT_CLICK_BLOCK)
				return;
			if (ev.getItem() == null || !Items.is(Items.ID.SKIN, ev.getItem()))
				return;

			ev.setCancelled(true);
			final CPlayer cp = CPlayer.getPlayer(ev.getPlayer());
			cp.getHandle().openInventory(SkinMenu.getInventory(cp, 0));
		}
	}

	private static String name = "§6Skins";
	private static int maxPage = Skin.list.size() / 36 + ((Skin.list.size() % 36 == 0) ? 0 : 1);

	static ItemStack prevArrow = Items.createHead(
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODY5NzFkZDg4MWRiYWY0ZmQ2YmNhYTkzNjE0NDkzYzYxMmY4Njk2NDFlZDU5ZDFjOTM2M2EzNjY2YTVmYTYifX19",
			"§bPrécédent");
	static ItemStack nextArrow = Items.createHead(
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjMyY2E2NjA1NmI3Mjg2M2U5OGY3ZjMyYmQ3ZDk0YzdhMGQ3OTZhZjY5MWM5YWMzYTkxMzYzMzEzNTIyODhmOSJ9fX0=",
			"§bSuivant");


	public static Inventory getInventory(final CPlayer cp, final int page)
	{
		final Inventory inv = Bukkit.createInventory(null, 45, name + MessageFormat.format(" - {0}/{1}", page+1, maxPage));
		final int end = Math.min(Skin.list.size(), page * 36 + 36);
		for (int i = page * 36; i < end; ++i)
		{
			inv.addItem(Skin.list.get(i).getDisplayItem());
		}

		if (page != 0)
			inv.setItem(36, prevArrow);
		if (page+1 != maxPage)
			inv.setItem(44, nextArrow);

		final ItemStack indicator = new ItemStack(Material.BOOK);
		final ItemMeta meta = indicator.getItemMeta();
		meta.setDisplayName("        §bInformations");
		String skinName = "§6§oDéfaut";
		if (cp.getSkin() != -1)
		{
			skinName = Skin.list.get(cp.getSkin()).getDisplayItem().getItemMeta().getDisplayName();
		}
		else
		{
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		meta.setLore(Arrays.asList(
			"§7§oCliquer ici pour remettre",
			"§7§ovotre skin de base",
			"",
			"§7Skin actuel: " + skinName));
		indicator.setItemMeta(meta);
		inv.setItem(40, indicator);


		return inv;
	}
}
