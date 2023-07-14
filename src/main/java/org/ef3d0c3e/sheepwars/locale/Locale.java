package org.ef3d0c3e.sheepwars.locale;

import com.google.common.base.CaseFormat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ef3d0c3e.sheepwars.SheepWars;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.logging.Level;

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

	public String LOCALE_NAME = "English";
	public String SYSTEM_RELOAD = "§ePlease reconnect";

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
		for (Field f : fields)
		{
			if (!f.getType().equals(String.class))
				continue;

			final String name = formatName(f.getName());
			try
			{
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
		final Field[] fields = Locale.class.getDeclaredFields();
		for (Field f : fields)
		{
			Bukkit.getConsoleSender().sendMessage(f.getName());
			if (!f.getType().equals(String.class))
				continue;

			final String name = formatName(f.getName());
			if (cfg.contains(name))
			{
				try
				{
					f.set(this, cfg.get(name));
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
