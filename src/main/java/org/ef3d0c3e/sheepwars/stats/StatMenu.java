package org.ef3d0c3e.sheepwars.stats;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Items;
import org.ef3d0c3e.sheepwars.kits.Kit;
import org.ef3d0c3e.sheepwars.kits.Kits;
import org.ef3d0c3e.sheepwars.level.Map;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.Sheeps;
import oshi.util.tuples.Pair;
import oshi.util.tuples.Triplet;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class StatMenu
{
	static public class Events implements Listener
	{
		@EventHandler
		public void onMenuClick(final InventoryClickEvent ev)
		{
			if (!ev.getView().getTitle().startsWith(StatMenu.name))
				return;
			ev.setCancelled(true);
			if (ev.getCurrentItem() == null || ev.getCurrentItem().getType() == Material.AIR)
				return;

			final String invName = ev.getView().getTitle();
			final CPlayer cp = CPlayer.getPlayer(Bukkit.getPlayer(invName.substring(invName.indexOf('[')+3, invName.indexOf(']')-2)));

			switch (ev.getRawSlot())
			{
				case 10:
					ev.getWhoClicked().openInventory(SHEEPWARS.getInventory(cp));
					break;
				case 11:
					ev.getWhoClicked().openInventory(KITS.getInventory(cp));
					break;
				case 12:
					ev.getWhoClicked().openInventory(MAPS.getInventory(cp));
					break;
				case 13:
					ev.getWhoClicked().openInventory(SHEEPS.getInventory(cp));
					break;
				default:
					break;
			}
		}

		@EventHandler
		public void onCategoryClick(final InventoryClickEvent ev)
		{
			if (!ev.getView().getTitle().startsWith(nameCat))
				return;
			ev.setCancelled(true);
			if (ev.getCurrentItem() == null || ev.getCurrentItem().getType() == Material.AIR)
				return;

			final String invName = ev.getView().getTitle();
			final CPlayer cp = CPlayer.getPlayer(Bukkit.getPlayer(invName.substring(invName.indexOf('[')+3, invName.indexOf(']')-2)));

			if (ev.getRawSlot() == 4) // RETURN
				ev.getWhoClicked().openInventory(getInventory(cp));
		}

		@EventHandler
		public void onInventoryDrag(final InventoryInteractEvent ev)
		{
			if (!ev.getView().getTitle().startsWith(name) && !ev.getView().getTitle().startsWith(nameCat))
				return;

			ev.setCancelled(true);
		}

		@EventHandler
		public void onStatItemUse(final PlayerInteractEvent ev)
		{
			if (ev.getAction() != Action.RIGHT_CLICK_AIR && ev.getAction() != Action.RIGHT_CLICK_BLOCK)
				return;
			if (ev.getItem() == null || !Items.is(Items.ID.STAT, ev.getItem()))
				return;

			ev.setCancelled(true);
			final CPlayer cp = CPlayer.getPlayer(ev.getPlayer());
			cp.getHandle().openInventory(StatMenu.getInventory(cp));
		}
	}

	static public abstract class StatCategory
	{
		static final ItemStack RETURN = Items.createItem(Material.BARRIER, "§cRetour");
		protected ItemStack icon;
		protected ArrayList<Stat> list;

		/**
		 * Gets category's name
		 * @return Category's name
		 */
		protected abstract String getName();

		/**
		 * Gets icon ofr category
		 * @return Icon
		 */
		protected ItemStack getIcon()
		{
			return icon;
		}

		/**
		 * Gets inventory for player
		 * @param cp Player
		 * @return Inventory
		 */
		Inventory getInventory(final CPlayer cp)
		{
			final int size = 9*(list.size()/7+2);
			Inventory inv = Bukkit.createInventory(null, size, MessageFormat.format("§6Stats §8[§b{0}§8] » §a{1}", cp.getHandle().getName(), getName()));
			inv.setItem(4, RETURN);

			for (int i = 0; i < list.size(); ++i)
				inv.setItem(9*(i/7+1) + i%7 + 1, list.get(i).getIcon(cp));

			return inv;
		}

		/**
		 * Initializes stats to default value for player
		 * @param cp Player to initialize stats for
		 */
		void init(final CPlayer cp)
		{
			for (final Stat s : list)
				s.init(cp);
		}

		String serialize(final CPlayer cp)
		{
			String r = new String();
			for (final Stat s : list)
				r += s.serialize(cp);

			return r;
		}
	}

	static public class StatCategorySW extends StatCategory
	{
		@Override
		protected String getName()
		{
			return "SheepWars";
		}

		private static Stat GAMES;
		private static Stat DAMAGE;
		private static Stat KILLS;

		public StatCategorySW()
		{
			icon = Items.createItem(
				Material.WHITE_WOOL, "§bSheepWars",
				"", "§7Statistiques sur le SheepWars");

			list = new ArrayList<>();
			{
				ArrayList<Triplet<String, String, StatValue>> l = new ArrayList<>();
				l.add(new Triplet<String, String, StatValue>("played", "§7Parties jouées: §e{0}", new StatLong()));
				l.add(new Triplet<String, String, StatValue>("won", "§7Parties gagnées: §e{0}", new StatLong()));
				GAMES = new Stat("sw#games", Material.COMPASS, "§aParties", l);
				list.add(GAMES);
			}
			{
				ArrayList<Triplet<String, String, StatValue>> l = new ArrayList<>();
				l.add(new Triplet<String, String, StatValue>("dealt", "§7Dégâts infligé: §e{0}", new StatDouble()));
				l.add(new Triplet<String, String, StatValue>("taken", "§7Dégâts reçu: §e{0}", new StatDouble()));
				DAMAGE = new Stat("sw#damage", Material.REDSTONE_BLOCK, "§aDégâts", l);
				list.add(DAMAGE);
			}
			{
				ArrayList<Triplet<String, String, StatValue>> l = new ArrayList<>();
				l.add(new Triplet<String, String, StatValue>("player_killed", "§7Nombre de joueurs tués: §e{0}", new StatLong()));
				l.add(new Triplet<String, String, StatValue>("sheep_killed", "§7Nombre de moutons tués: §e{0}", new StatLong()));
				l.add(new Triplet<String, String, StatValue>("death", "§7Nombre de morts: §e{0}", new StatLong()));
				KILLS = new Stat("sw#kills", Material.IRON_SWORD, "§aKills", l);
				list.add(KILLS);
			}
		}
	}

	static public class StatCategoryKits extends StatCategory
	{
		@Override
		protected String getName()
		{
			return "Kits";
		}

		public StatCategoryKits()
		{
			icon = Items.createItem(
				Material.NAME_TAG, "§bKits",
				"", "§7Statistiques sur les kits");

			list = new ArrayList<>();
			for (final Kit k : Kits.list)
			{
				ArrayList<Triplet<String, String, StatValue>> l = new ArrayList<>();
				l.add(new Triplet<String, String, StatValue>("played", "§7Parties jouées: §e{0}", new StatLong()));
				l.add(new Triplet<String, String, StatValue>("won", "§7Parties gagnées: §e{0}", new StatLong()));
				list.add(new Stat("kits#" + k.getName(), k.getDisplayItem().getType(), k.getColoredName(), l));

			}
		}
	}

	static public class StatCategoryMaps extends StatCategory
	{
		@Override
		protected String getName()
		{
			return "Maps";
		}

		public StatCategoryMaps()
		{
			icon = Items.createItem(
				Material.MAP, "§bCartes",
				"", "§7Statistiques sur les cartes");

			list = new ArrayList<>();
			for (final Map m : Map.getMaps().values())
			{
				ArrayList<Triplet<String, String, StatValue>> l = new ArrayList<>();
				l.add(new Triplet<String, String, StatValue>("played", "§7Parties jouées: §e{0}", new StatLong()));
				l.add(new Triplet<String, String, StatValue>("won", "§7Parties gagnées: §e{0}", new StatLong()));
				list.add(new Stat("maps#" + m.getName(), m.getIcon(), "§a" + m.getDisplayName(), l));
			}
		}
	}

	static public class StatCategorySheeps extends StatCategory
	{
		@Override
		protected String getName()
		{
			return "Moutons";
		}

		public StatCategorySheeps()
		{
			icon = Items.createItem(
				Material.RED_WOOL, "§bMoutons",
				"", "§7Statistiques sur les moutons");

			list = new ArrayList<>();
			for (final HashMap.Entry<String, Pair<ItemStack, Class<? extends BaseSheep>>> ent : Sheeps.list.entrySet())
			{
				ItemStack item = null;
				try
				{
					item = (ItemStack) ent.getValue().getB().getDeclaredMethod("getItem").invoke(null);
				}
				catch (NoSuchMethodException e)
				{
					e.printStackTrace();
				}
				catch (InvocationTargetException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
				ArrayList<Triplet<String, String, StatValue>> l = new ArrayList<>();
				l.add(new Triplet<String, String, StatValue>("used", "§7Nombre de fois utilisées: §e{0}", new StatLong()));
				list.add(new Stat("sheeps#" + ent.getKey(), item.getType(), item.getItemMeta().getDisplayName(), l));

			}
		}
	}

	public static final StatCategorySW SHEEPWARS = new StatCategorySW();
	public static final StatCategoryKits KITS = new StatCategoryKits();
	public static final StatCategoryMaps MAPS = new StatCategoryMaps();
	public static final StatCategorySheeps SHEEPS = new StatCategorySheeps();
	private static final String name = "§0§6Stats";
	private static final String nameCat = "§6Stats";

	/**
	 * Get inventory for player
	 * @param cp Player to get inventory for
	 * @return Inventory
	 */
	public static Inventory getInventory(final CPlayer cp)
	{
		final Inventory inv = Bukkit.createInventory(null, 36, MessageFormat.format("{0} §8[§b{1}§8]", name, cp.getHandle().getName()));
		inv.setItem(10, SHEEPWARS.getIcon());
		inv.setItem(11, KITS.getIcon());
		inv.setItem(12, MAPS.getIcon());
		inv.setItem(13, SHEEPS.getIcon());

		return inv;
	}


	public static void init()
	{

	}
}
