package locale;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ef3d0c3e.sheepwars.SheepWars;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;

public class LocaleManager
{
	static private HashMap<String, Locale> locales;

	static public File getLocaleFolder()
	{
		return new File(SheepWars.getPlugin().getDataFolder().toPath() + "/locales");
	}

	static public Locale getLocale(final String lang)
	{
		return locales.get(lang);
	}

	static public Locale getDefaultLocale() { return locales.get("french"); }

	static public void loadLocales()
	{
		locales = new HashMap<>();
		final File dir = getLocaleFolder();

		if (!dir.isDirectory())
			throw new IllegalArgumentException("Cannot find locales directory");

		for (final File f : dir.listFiles())
		{
			if (!f.isFile())
				continue;

			final YamlConfiguration c = YamlConfiguration.loadConfiguration(f);
			Bukkit.getConsoleSender().sendMessage(MessageFormat.format("§cSheepWars>§7 Loading locale '{}'", c.getString("config.displayname")));
			locales.put(c.getString("config.name"), new Locale(c));
		}
	}
}
