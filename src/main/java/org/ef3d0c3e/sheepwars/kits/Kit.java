package org.ef3d0c3e.sheepwars.kits;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.IGui;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.Sheeps;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;

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
					ItemMeta meta = item.getItemMeta();
					meta.setLore(Lists.newArrayList(MessageFormat.format("§e{0}%", 100.f*(p.getB()-t) / woolRandomizer.total)));
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
}
