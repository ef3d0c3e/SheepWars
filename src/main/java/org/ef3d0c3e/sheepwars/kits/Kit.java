package org.ef3d0c3e.sheepwars.kits;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.checkerframework.checker.units.qual.A;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.IGui;
import org.ef3d0c3e.sheepwars.Team;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.Sheeps;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class Kit
{
	public class WoolRandomizer
	{
		private ArrayList<Pair<ItemStack, Float>> pool;
		private float total;

		public WoolRandomizer()
		{
			pool = new ArrayList<>();
		}

		public void add(Class<? extends BaseSheep> clz, float weight)
		{
			try
			{
				pool.add(new Pair<>((ItemStack)clz.getDeclaredMethod("getItem").invoke(null), total+weight));
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
			catch (NoSuchMethodException e)
			{
				e.printStackTrace();
			}

			total += weight;
			pool.sort((p, q) -> Float.compare(p.getB(), q.getB()));
		}

		public ItemStack getNextWool()
		{
			final double v = Math.random() * total;

			int l = 0, h = pool.size();

			while (l <= h)
			{
				int m = l + (h-l)/2;

				if (pool.get(m).getB() < v)
					l = m+1;
				else if (pool.get(m).getB() > v)
					h = m-1;
				else
				{
					return pool.get(m).getA();
				}
			}

			return pool.get(l).getA();
		}

		@AllArgsConstructor
		public static class Gui implements IGui
		{
			private WoolRandomizer woolRandomizer;

			@Override
			public void onGuiClick(Player p, ClickType click, int slot, ItemStack item) {}
			@Override
			public void onGuiClose(Player p) {}
			@Override
			public void onGuiDrag(Player p, InventoryDragEvent ev) {}

			@Override
			public Inventory getInventory()
			{
				final Inventory inv = Bukkit.createInventory(this, (woolRandomizer.pool.size() / 9 + 1) * 9, "§9Probabilités");

				float t = 0;
				for (final Pair<ItemStack, Float> p : woolRandomizer.pool)
				{
					final ItemStack item = p.getA().clone();
					final float perc = 100.f*(p.getB()-t) / woolRandomizer.total;
					item.setAmount(Math.min(Math.round(perc), item.getMaxStackSize()));
					final ItemMeta meta = item.getItemMeta();
					meta.setLore(Lists.newArrayList(MessageFormat.format("§e{0}%", perc)));
					item.setItemMeta(meta);

					t = p.getB();
					inv.addItem(item);
				}

				return inv;
			}
		}
	}

	@Getter
	protected WoolRandomizer woolRandomizer;
	CPlayer player;


	public abstract String getName();
	public abstract String getColoredName();
	public abstract ItemStack getDisplayItem();

	public double getAdditionalSheepChance()
	{
		return 0.0;
	}
	public void tick(int ticks) {}

	public Kit(@Nullable CPlayer player)
	{
		this.player = player;
		this.woolRandomizer = new WoolRandomizer();
	}

	/**
	 * Gets the kit's weapons
	 * @return
	 */
	public List<ItemStack> getWeapons()
	{
		final ArrayList<ItemStack> items = new ArrayList<>();

		// Sword
		final ItemStack sword = new ItemStack(Material.WOODEN_SWORD);
		{
			ItemMeta meta = sword.getItemMeta();
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			sword.setItemMeta(meta);
		}
		items.add(sword);

		// Bow
		final ItemStack bow = new ItemStack(Material.BOW);
		{
			ItemMeta meta = bow.getItemMeta();
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			bow.setItemMeta(meta);
		}
		items.add(bow);

		return items;
	}

	/**
	 * Adds loadout to player's inventory
	 * @param inv Player's Inventory
	 * @param team Player's team
	 */
	public final void setLoadout(final PlayerInventory inv, final Team team)
	{
		final ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		{
			LeatherArmorMeta meta = (LeatherArmorMeta)helmet.getItemMeta();
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_DYE);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
			meta.setColor(team.getArmorColor());
			helmet.setItemMeta(meta);
		}
		final ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
		{
			LeatherArmorMeta meta = (LeatherArmorMeta)chestplate.getItemMeta();
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_DYE);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
			meta.setColor(team.getArmorColor());
			chestplate.setItemMeta(meta);
		}
		final ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
		{
			LeatherArmorMeta meta = (LeatherArmorMeta)leggings.getItemMeta();
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_DYE);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
			meta.setColor(team.getArmorColor());
			leggings.setItemMeta(meta);
		}
		final ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
		{
			LeatherArmorMeta meta = (LeatherArmorMeta)boots.getItemMeta();
			meta.setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_DYE);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
			meta.setColor(team.getArmorColor());
			boots.setItemMeta(meta);
		}

		for (final ItemStack item : getWeapons())
			inv.addItem(item);

		inv.setHelmet(helmet);
		inv.setChestplate(chestplate);
		inv.setLeggings(leggings);
		inv.setBoots(boots);
	}
}
