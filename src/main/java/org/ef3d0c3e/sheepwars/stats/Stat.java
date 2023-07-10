package org.ef3d0c3e.sheepwars.stats;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Items;
import oshi.util.tuples.Triplet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class Stat
{
	private String namespace; ///< Namespace name (for serialization)
	private ArrayList<Triplet<String, String, StatValue>> components;
	private ItemStack icon; // Base icon

	/**
	 * Constructor
	 * @param namespace Namespace key name for serialization
	 * @param material Material to use for the icon
	 * @param name Stat's display name
	 * @param components Stat's components
	 * @param desc Description to show before components
	 */
	public Stat(final String namespace, final Material material, final String name, final ArrayList<Triplet<String, String, StatValue>> components, final String... desc)
	{
		this.namespace = namespace;
		this.components = components;
		icon = Items.createItem(material, name, desc);
	}

	/**
	 * Gets icon associated with stat
	 * @param cp Player to get icon for
	 * @return Icon as an ItemStack
	 */
	public ItemStack getIcon(final CPlayer cp)
	{
		ItemStack item = icon.clone();
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<String>();
		lore.add("§0");
		for (final Triplet<String, String, StatValue> t : components)
		{
			final StatValue v = cp.getStat(MessageFormat.format("{0}#{1}", namespace, t.getA()));
			if (v != null)
				lore.add(v.format(t.getB()));
			else // Error
				lore.add(MessageFormat.format("ERR: {0} is null", t.getB()));
		}
		meta.setLore(lore);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.addItemFlags(ItemFlag.HIDE_DYE);
		item.setItemMeta(meta);

		return item;
	}

	/**
	 * Initialize a stat to it's default value
	 * @param cp Player to initialize stat for
	 */
	public void init(final CPlayer cp)
	{
		for (final Triplet<String, String, StatValue> t : components)
			cp.setStat(MessageFormat.format("{0}#{1}", namespace, t.getA()), t.getC().clone());
	}

	String serialize(final CPlayer cp)
	{
		String r = new String();
		for (final Triplet<String, String, StatValue> t : components)
			r += MessageFormat.format("{0}#{1}:{2}\n", namespace, t.getA(), cp.getStat(MessageFormat.format("{0}#{1}", namespace, t.getA())).serialize());

		return r;
	}
}
