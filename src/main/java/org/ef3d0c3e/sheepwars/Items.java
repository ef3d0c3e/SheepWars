package org.ef3d0c3e.sheepwars;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Sheep;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.ef3d0c3e.sheepwars.skins.Skin;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Items
{
	static private NamespacedKey key;
	public static ItemStack voteItem;
	public static ItemStack statItem;

	public static enum ID
	{
		VOTE(0),
		TEAM(1),
		KIT(2),
		SKIN(3),
		STAT(4);

		private int id;
		private ID(int id)
		{
			this.id = id;
		}

		/**
		 * Creates an item from it's id
		 * @param material Material to use
		 * @param name Item's name
		 * @param lore Item's lore
		 * @return Created item
		 */
		public ItemStack create(final Material material, final String name, final String... lore)
		{
			final ItemStack item = new ItemStack(material);
			final ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(name);
			meta.setLore(Arrays.asList(lore));
			meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, id);
			item.setItemMeta(meta);

			return item;
		}

		/**
		 * Gets value
		 * @return Value as an integer
		 */
		public int value()
		{
			return id;
		}
	};

	public static void init()
	{
		key = new NamespacedKey(SheepWars.plugin, "swid");

		voteItem = ID.VOTE.create(Material.MAP, "§eCarte §7(Click-Droit)", "§7Utilisez cet objet pour", "§7voter pour une carte");
		statItem = ID.STAT.create(Material.ENDER_CHEST, "§6Statistiques §7(Click-Droit)", "§7Utilisez cet objet pour", "§7afficher les statistiques");
	}

	/**
	 * Compares item with ID
	 * @param id ID to check for
	 * @param item Item to compare
	 * @return True if both items are the same, false otherwise
	 */
	public static boolean is(final ID id, final ItemStack item)
	{
		// Check if item has tag
		if (item.getItemMeta() == null || !item.getItemMeta().getPersistentDataContainer().has(key, PersistentDataType.INTEGER))
			return false;

		return id.value() == item.getItemMeta().getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
	}

	public static ItemStack createItem(final Material material, final String name, final String... lore)
	{
		final ItemStack item = new ItemStack(material);
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack createHead(final String texture, final String name, final String... lore)
	{

		final ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		final SkullMeta meta = (SkullMeta)item.getItemMeta();
		meta.setDisplayName(Util.getColored(name));
		List<String> lines = Arrays.asList(lore);
		for (String line : lines)
			line = Util.getColored(line);
		meta.setLore(lines);

		GameProfile profile = new GameProfile(UUID.randomUUID(), "");
		profile.getProperties().put("textures", new Property("textures", texture));
		Field profileField = null;
		try
		{
			profileField = meta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(meta, profile);
		}
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
		{
			e.printStackTrace();
		}
		item.setItemMeta(meta);

		return item;
	}

	/**
	 * Removes all items with matching id
	 * @param cp Player to remove form
	 * @param id Item id
	 */
	public static void remove(final CPlayer cp, final ID id)
	{
		Iterator<ItemStack> it = cp.getHandle().getInventory().iterator();
		while (it.hasNext())
		{
			ItemStack item = it.next();
			if (item != null && is(id, item))
				item.setAmount(0);
		}
	}

	/**
	 * Gets wool for team
	 * @param cp Player
	 * @return Colored wool
	 */
	public static ItemStack getTeamItem(final CPlayer cp)
	{
		return ID.TEAM.create(cp.getTeam().getColoredBanner(),
			Util.getColored(cp.getTeam().getColorCode()) + "Équipe " + cp.getTeam().getName() + " §7(Click-Droit)",
			"§7Utilisez cet objet pour", "§7changer d'équipe");
	}

	public static ItemStack getKitItem(final CPlayer cp)
	{
		return ID.KIT.create(Material.NAME_TAG,
			Util.getColored(cp.getTeam().getColorCode()) + "§6Kit §7: " + cp.getKit().getColoredName() + " §7(Click-Droit)",
			"§7Utilisez cet objet pour", "§7changer de kit");
	}

	public static ItemStack getSkinItem(final CPlayer cp)
	{
		final ItemStack head = ID.SKIN.create(Material.PLAYER_HEAD,
			MessageFormat.format("§dSkin §7: §b{0} §7(Click-Droit)", cp.getSkin() == -1 ? "§oAucun" : Skin.getSkinName(cp.getSkin())),
			"§7Utilisez cet objet pour", "§7changer de skin"
		);
		if (cp.getSkin() == -1)
		{
			final SkullMeta meta = (SkullMeta) head.getItemMeta();
			meta.setOwnerProfile(cp.getHandle().getPlayerProfile());
			head.setItemMeta(meta);
		}
		else
		{
			final SkullMeta meta = (SkullMeta) head.getItemMeta();
			meta.setOwnerProfile(Skin.getSkinMeta(cp.getSkin()).getOwnerProfile());
			head.setItemMeta(meta);
		}


		return head;
	}
}
