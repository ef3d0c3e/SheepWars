package org.ef3d0c3e.sheepwars.locale;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class LocaleManager
{
	@Getter
	private DirectoryStream<Path> localeDir;
	private final List<Locale> locales = new ArrayList<>();

	public Locale getDefaultLocale()
	{
		return locales.get(0);
	}

	public LocaleManager(File dir)
	{
		try
		{
			localeDir = Files.newDirectoryStream(dir.toPath());
		}
		catch (IOException e)
		{
			Bukkit.getServer().getLogger().log(Level.WARNING, "Unable to create/read locale directory : " + e.getMessage());
		}

		if (!dir.exists()) // Create locale dir
		{
			Bukkit.getServer().getLogger().log(Level.INFO, "Creating locales directory");
				dir.mkdirs();
		}

		// Write default locale
		if (!localeDir.iterator().hasNext())
		{
			locales.add(new Locale()); // Default locale

			Bukkit.getServer().getLogger().log(Level.INFO, "Writing default locale");
			final YamlConfiguration cfg = getDefaultLocale().serialize();
			try
			{
				cfg.save(new File(localeDir + "english.yml"));
			}
			catch (IOException e)
			{
				Bukkit.getServer().getLogger().log(Level.WARNING, "Unable to save default locale to 'english.yml' : " + e.getMessage());
			}
		}
		else // Read all locales
		{
			for (final Path p : localeDir)
			{
				final YamlConfiguration cfg = new YamlConfiguration();
				try
				{
					Bukkit.getServer().getLogger().log(Level.INFO, "Reading locale : " + p.toString());
					cfg.load(p.toFile());
					final Locale l = new Locale();
					l.deserialize(cfg);

					locales.add(l);
				}
				catch (IOException | InvalidConfigurationException e)
				{
					Bukkit.getServer().getLogger().log(Level.WARNING, "Unable to save default locale to 'english.yml' : " + e.getMessage());
				}
			}
		}
	}
}
