package org.ef3d0c3e.sheepwars.locale;

import com.google.common.base.CaseFormat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;

public class Locale
{
	/**
	 * Format the name of identifiers to YML format
	 * @param name Identifier name
	 * @return Identifier name in YML
	 */
	private static String formatName(String name)
	{
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name);
	}


	public ItemStack CONFIG_BANNER;
	public String CONFIG_NAME;
	public String CONFIG_DISPLAYNAME;
	public String SYSTEM_RELOAD;

	// Teams
	public String TEAM_RED;
	public String TEAM_BLUE;

	// Maps
	public String MAP_CHOOSE;
	public String MAP_VOTES;

	// Locale
	public String LOCALE_CHANGED;
	public String LOCALE_MENU_TITLE;

	// Kits
	public String KIT_CHOOSE;
	public String KIT_ARCHER;
	public List<String> KIT_ARCHERLORE;
	public String KIT_BARBARIAN;
	public List<String> KIT_BARBARIANLORE;
	public String KIT_BUILDER;
	public List<String> KIT_BUILDERLORE;
	public String KIT_ENCHANTER;
	public String KIT_ENCHANTERWAND;
	public List<String> KIT_ENCHANTERLORE;
	public String KIT_MAGE;
	public List<String> KIT_MAGELORE;
	public String KIT_TECHNICIAN;
	public List<String> KIT_TECHNICIANLORE;

	// Scoreboard
	public String SCOREBOARD_TITLE;
	public String SCOREBOARD_KIT;
	public String SCOREBOARD_TEAM;
	public String SCOREBOARD_DURATION;
	public String SCOREBOARD_FOOTER;

	// Tab
	public String TAB_HEADER;
	public String TAB_FOOTER;
	public String TAB_FOOTERMAP;

	// Items
	public String ITEM_RIGHTCLICK;
	public String ITEM_TEAM;
	public List<String> ITEM_TEAMLORE;
	public String ITEM_KIT;
	public List<String> ITEM_KITLORE;
	public String ITEM_VOTE;
	public List<String> ITEM_VOTELORE;
	public String ITEM_LANGUAGE;
	public List<String> ITEM_LANGUAGELORE;

	/**
	 * Constructor
	 */
	public Locale()
	{
	}


	public YamlConfiguration serialize()
	{
		final YamlConfiguration cfg = new YamlConfiguration();
		final Field[] fields = Locale.class.getDeclaredFields();

		cfg.set("config.banner", CONFIG_BANNER);

		for (Field f : fields)
		{

			final String name = formatName(f.getName());
			Bukkit.getConsoleSender().sendMessage(name);
			try
			{
				if (f.getType().equals(String.class))
					cfg.set(name, f.get(this));
				else if (f.getType().equals(List.class))
					cfg.set(name, f.get(this));
			}
			catch (IllegalAccessException e)
			{
				e.printStackTrace();
			}
		}

		return cfg;
	}

	public void deserialize(YamlConfiguration cfg)
	{
		CONFIG_BANNER = cfg.getItemStack("config.banner");

		final Field[] fields = Locale.class.getDeclaredFields();
		for (Field f : fields)
		{
			final String name = formatName(f.getName()).replace('-', '.');

			if (cfg.contains(name))
			{
				try
				{
					if (f.getType().equals(String.class))
						f.set(this, cfg.get(name));
					else if (f.getType().equals(List.class))
						f.set(this, cfg.getStringList(name));
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage(MessageFormat.format("Unknown locale string: {0}", name));
			}
		}

		// Banner name
		final ItemMeta meta = CONFIG_BANNER.getItemMeta();
		meta.setDisplayName("§9" + CONFIG_DISPLAYNAME);
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DYE);
		CONFIG_BANNER.setItemMeta(meta);

	}
}
