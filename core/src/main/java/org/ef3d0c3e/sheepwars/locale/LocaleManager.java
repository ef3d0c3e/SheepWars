package org.ef3d0c3e.sheepwars.locale;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.checkerframework.checker.nullness.qual.Nullable;

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
    @Getter
    private final List<Locale> locales = new ArrayList<>();

    public Locale getDefaultLocale()
    {
        // TODO: config.default_locale
        for (Locale l : locales)
        {
            if (l.CONFIG_NAME.equals("en"))
                return l;
        }
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

        // Read locales
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
                Bukkit.getServer().getLogger().log(Level.WARNING, "Unable to save default locale to 'en.yml' : " + e.getMessage());
            }
        }
    }

    public int size() { return locales.size(); }

    /**
     * @brief Gets a locale by name, ignoring case
     * @param name The (config) name of the locale to get
     * @return The locale or null if not found
     */
    public @Nullable Locale getByName(final String name)
    {
        for (final Locale loc : this.locales)
        {
            if (loc.CONFIG_NAME.equalsIgnoreCase(name))
                return loc;
        }
        return null;
    }
}

