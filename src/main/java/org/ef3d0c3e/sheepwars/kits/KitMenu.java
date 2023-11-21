package org.ef3d0c3e.sheepwars.kits;

import lombok.AllArgsConstructor;
import org.bukkit.*;
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
import org.ef3d0c3e.sheepwars.level.Lobby;
import org.ef3d0c3e.sheepwars.locale.Locale;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.UUID;

@AllArgsConstructor
public class KitMenu implements IGui
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

		final Kit kit = Kits.list.get(slot);
		final CPlayer cp = CPlayer.getPlayer(p);

		// Set kit
		if (click.isLeftClick())
		{
			try
			{
				cp.setKit(kit.getClass().getDeclaredConstructor(CPlayer.class).newInstance(cp));
			}
			catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
			{
				e.printStackTrace();
			}

			// Update inventory
			p.openInventory(getInventory());

			// Change items
			Lobby.updateInventory(cp);

			cp.getHandle().playSound(cp.getHandle().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 65536.f, 1.4f);
		}
		// Open wool probability
		else if (click.isRightClick())
		{
			p.openInventory(new Kit.WoolRandomizer.Gui(kit.getWoolRandomizer()).getInventory());
		}
	}

	@Override
	public Inventory getInventory()
	{
		final Inventory inv = Bukkit.createInventory(this, (Kits.list.size() / 9 + 1) * 9, cp.getLocale().KIT_CHOOSE);
		for (final Kit kit : Kits.list)
		{
			if (cp.getKit().getClass() == kit.getClass())
			{
				ItemStack item = kit.getDisplayItem(cp);
				ItemMeta meta = item.getItemMeta();
				meta.addEnchant(Enchantment.DURABILITY, 1, true);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
				inv.addItem(item);
			}
			else
				inv.addItem(kit.getDisplayItem(cp));
		}

		return inv;
	}

	public static class KitMenuItem extends ItemBase
	{
		public KitMenuItem()
		{
			super();
		}

		@Override
		protected boolean onInteract(Player p, ItemStack item, Action action, EquipmentSlot hand, Block clicked, BlockFace clickedFace)
		{
			if (Game.hasStarted() || (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK))
				return true;

			p.openInventory(new KitMenu(CPlayer.getPlayer(p)).getInventory());

			return true;
		}

		@Override
		protected boolean onDrop(Player p, ItemStack item)
		{
			return true;
		}
	}

	static final private KitMenuItem KitItem = new KitMenuItem();
	public static ItemStack getItem(final CPlayer cp)
	{
		final ItemStack item = new ItemStack(Material.NAME_TAG);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageFormat.format(cp.getLocale().ITEM_KIT, cp.getKit().getColoredName(cp)) + " " + cp.getLocale().ITEM_RIGHTCLICK);
		meta.setLore(cp.getLocale().ITEM_KITLORE);
		item.setItemMeta(meta);

		SheepWars.getItemRegistry().registerItem(KitItem);
		return KitItem.apply(item);
	}
}
