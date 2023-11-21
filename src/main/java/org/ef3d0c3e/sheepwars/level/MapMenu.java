package org.ef3d0c3e.sheepwars.level;


import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.*;
import org.ef3d0c3e.sheepwars.items.ItemBase;
import org.ef3d0c3e.sheepwars.kits.Kit;
import org.ef3d0c3e.sheepwars.kits.Kits;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.UUID;

@AllArgsConstructor
public class MapMenu implements IGui
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

		// Set voted map
		for (final Map map : Map.getMaps().values())
		{
			if (!item.getItemMeta().getDisplayName().endsWith(map.getDisplayName()))
				continue;

			Map.setVote(cp, map);
			break;
		}

		// Update inventory
		p.openInventory(getInventory());

		cp.getHandle().playSound(cp.getHandle().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 65536.f, 1.4f);

	}

	@Override
	public Inventory getInventory()
	{
		final Inventory inv = Bukkit.createInventory(this, (Map.getMaps().size() / 9 + 1) * 9, cp.getLocale().MAP_CHOOSE);
		for (final Map map : Map.getMaps().values())
		{
			final ItemStack item = new ItemStack(map.getIcon(), Math.max(1, Math.min(64, map.getVote())));
			final ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§a" + map.getDisplayName());
			meta.setLore(Collections.singletonList(MessageFormat.format(cp.getLocale().MAP_VOTES, map.getVote())));

			if (Map.votedFor(cp, map)) // Glitter
			{
				meta.addEnchant(Enchantment.DURABILITY, 1, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}

			item.setItemMeta(meta);

			inv.addItem(item);
		}

		return inv;
	}

	public static class MapMenuItem extends ItemBase
	{
		public MapMenuItem()
		{
			super();
		}

		@Override
		protected boolean onInteract(Player p, ItemStack item, Action action, EquipmentSlot hand, Block clicked, BlockFace clickedFace)
		{
			if (Game.hasStarted() || (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK))
				return true;

			p.openInventory(new MapMenu(CPlayer.getPlayer(p)).getInventory());

			return true;
		}

		@Override
		protected boolean onDrop(Player p, ItemStack item)
		{
			return true;
		}
	}

	static final private MapMenuItem MapItem = new MapMenuItem();
	public static ItemStack getItem(final CPlayer cp)
	{
		final ItemStack item = new ItemStack(Material.MAP);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(cp.getLocale().ITEM_VOTE + " " + cp.getLocale().ITEM_RIGHTCLICK);
		meta.setLore(cp.getLocale().ITEM_VOTELORE);
		item.setItemMeta(meta);

		SheepWars.getItemRegistry().registerItem(MapItem);
		return MapItem.apply(item);
	}
}
