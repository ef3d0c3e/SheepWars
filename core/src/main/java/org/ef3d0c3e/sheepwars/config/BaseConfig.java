package org.ef3d0c3e.sheepwars.config;

import com.google.common.base.CaseFormat;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.SheepWars;
//import org.ef3d0c3e.sheepwars.locale.Locale;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;

/**
 * Derive this class to create a config-exportable class
 * For non-exportable fields, @see @ConfigExclude
 */
public abstract class BaseConfig
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

    @ConfigExclude
    private File configFile;

    /**
     * Constructor
     * @param path Config YML file path
     */
    public BaseConfig(final File path)
    {
        this.configFile = path;
        YamlConfiguration config = new YamlConfiguration();
        try
        {
            config.load(path);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }

        deserialize(config);
    }

    /**
     * Loads config from YML
     */
    private void deserialize(final YamlConfiguration config)
    {
        final Field[] fields = this.getClass().getDeclaredFields();
        final HashSet<Field> setFields = new HashSet<>();

        for (final Field f : fields)
        {
            if (f.getAnnotation(ConfigExclude.class) != null) continue;

            final String name = formatName(f.getName()).replace('-', '.');

            if (config.contains(name))
            {
                try
                {
                    if (f.getType().equals(String.class)) // String
                        f.set(this, config.get(name));
                    else if (f.getType().equals(int.class)) // int
                        f.set(this, config.getInt(name));
                    else if (f.getType().equals(double.class)) // double
                        f.set(this, config.getDouble(name));
                    else if (f.getType().equals(Vector.class)) // Vector
                    {
                        final List<Double> lst = config.getDoubleList(name);
                        if (lst.size() != 3) throw new Exception("In " + configFile.getName() + ": While parsing `" + name + "`: Invalid number of elements for Vector expected 3 got " + lst.size());
                        f.set(this, new Vector(lst.get(0), lst.get(1), lst.get(2)));
                    }
                    else if (f.getType().equals(AnyLocation.class))
                    {
                        final List<Double> lst = config.getDoubleList(name);
                        if (lst.size() != 5) throw new Exception("In " + configFile.getName() + ": While parsing `" + name + "`: Invalid number of elements for AnyLocation expected 5 got " + lst.size());
                        f.set(this, new AnyLocation(lst.get(0), lst.get(1), lst.get(2), lst.get(3).floatValue(), lst.get(4).floatValue()));
                    }
                    else if (f.getType().equals(List.class))
                        f.set(this, config.getStringList(name));
                    else
                        throw new IllegalArgumentException("Field `" + f.getName() + "` of type " + f.getType().getName() + " cannot be parsed.");

                    setFields.add(f);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                Bukkit.getConsoleSender().sendMessage(MessageFormat.format("In `{0}`: Unknown config variable: {1}", configFile.getName(), name));
            }
        }

        // Verify
        for (final Field f : fields)
        {
            if (setFields.contains(f)) continue;

            SheepWars.consoleMessage(MessageFormat.format("In `{0}`: Missing variable in config: {1}", configFile.getName(), formatName(f.getName())));
        }

    }
}

