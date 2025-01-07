package org.ef3d0c3e.sheepwars.locale;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.reflections.Reflections;
import oshi.util.tuples.Pair;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class LocaleManager {
    @Getter
    private @NonNull DirectoryStream<Path> localeDir;
    @Getter
    private final @NonNull List<@NonNull Locale> locales = new ArrayList<>();

    private static String getLocalizationKey(final @NonNull LocalePath path, final @Nullable LocalizeAs as, final @NonNull String field)
    {
        final StringBuilder builder = new StringBuilder();
        if (as == null) {
            builder.append(path.value());
            builder.append('.');

            final AtomicBoolean needsUppercase = new AtomicBoolean(false);
            field.chars().forEach((c) -> {
                if (c == '_')
                    needsUppercase.set(true);
                else if (needsUppercase.get()) {
                    builder.appendCodePoint(Character.toUpperCase(c));
                    needsUppercase.set(false);
                } else
                    builder.appendCodePoint(Character.toLowerCase(c));
            });
        }
        else
        {
            if (as.absolute())
                builder.append(as.value());
            else {
                builder.append(path.value());
                builder.append('.');
                builder.append(as.value());
            }
        }
        return builder.toString();
    }

    public Locale getDefaultLocale()
    {
        // TODO: config.default_locale
        for (Locale l : locales)
        {
            if (l.getName().equals("en"))
                return l;
        }
        return locales.get(0);
    }

    /**
     * Creates a new LocaleManager
     * @param localeDir The locale directory path
     */
    public LocaleManager(final @NonNull Path localeDir) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        try
        {
            this.localeDir = Files.newDirectoryStream(localeDir);
        }
        catch (IOException e)
        {
            Bukkit.getServer().getLogger().log(Level.SEVERE, "Unable to create/read locale directory : " + e.getMessage());
        }

        // Build the array of localized
        final Reflections refl = new Reflections("org.ef3d0c3e.sheepwars");


        // Populate the list of keys
        final ArrayList<Pair<String, LocaleDeserializer>> keys = new ArrayList<>();
        final Set<Class<?>> annotated = refl.getTypesAnnotatedWith(LocalePath.class);
        for (final Class<?> clz : annotated)
        {
            final var annotation = clz.getAnnotation(LocalePath.class);

            for (final Field field : clz.getDeclaredFields())
            {
                if (!Modifier.isStatic(field.getModifiers()) || !field.getType().equals(Localized.class))
                    continue;

                final var as = field.getAnnotation(LocalizeAs.class);
                final var inst = field.getType().getDeclaredConstructor(int.class).newInstance(keys.size());
                field.setAccessible(true);
                field.set(null, inst);
                final var tp = (ParameterizedType)field.getGenericType();
                if (tp.getActualTypeArguments()[0].equals(String.class)) // String
                    keys.add(new Pair<>(getLocalizationKey(annotation, as, field.getName()), LocaleDeserializer.STRING));
                else if (tp.getActualTypeArguments()[0] instanceof ParameterizedType tp2)
                {
                    if (tp2.getRawType().equals(List.class)) // List<T>
                    {
                        if (tp2.getActualTypeArguments()[0].equals(String.class)) // List<String>
                            keys.add(new Pair<>(getLocalizationKey(annotation, as, field.getName()), LocaleDeserializer.STRING_LIST));
                        else
                            throw new RuntimeException("Unsupported Localized key type for list " + tp2.getActualTypeArguments()[0].getTypeName());
                    }
                    else
                        throw new RuntimeException("Unsupported Localized key type " + tp.getActualTypeArguments()[0].getTypeName());
                }
                else
                    throw new RuntimeException("Unsupported Localized key type " + tp.getActualTypeArguments()[0].getTypeName());
            }
        }

        keys.forEach((s) -> Bukkit.getConsoleSender().sendMessage(s.getA()));

        // Read locales
        for (final Path p : this.localeDir)
        {
            try
            {
                Bukkit.getServer().getLogger().log(Level.INFO, "Reading locale : " + p.toString());
                final var cfg = new YamlConfiguration();
                cfg.load(p.toFile());
                final Locale l = new Locale(keys, cfg);

                locales.add(l);
            }
            catch (IOException | InvalidConfigurationException e)
            {
                Bukkit.getServer().getLogger().log(Level.WARNING, MessageFormat.format("Unable to save default locale to ''{0}'' : {1}", p.toString(), e.getMessage()));
            }
        }
    }
}
