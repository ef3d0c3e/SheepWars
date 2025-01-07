package org.ef3d0c3e.sheepwars.locale;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Objects;

public class Locale {
    @Getter
    final @NonNull String name;
    @Getter
    final @NonNull String displayName;
    @Getter
    final @NonNull ItemStack bannerItem;

    /**
     * All the localized items
     */
    private final ArrayList<Object> items;

    public<T> T get(final int id)
    {
        return (T)items.get(id);
    }

    /**
     * Constructs a new locale from a YAML file
     * @param keys The keys that must be in the YAML file
     * @param config The YAML file containing the locale
     */
    public Locale(final ArrayList<Pair<String, LocaleDeserializer>> keys, final @NonNull YamlConfiguration config)
    {
        name = Objects.requireNonNull(config.getString("config.name"));
        displayName = Objects.requireNonNull(config.getString("config.displayName"));
        bannerItem = Objects.requireNonNull(config.getItemStack("config.banner"));

        items = new ArrayList<>(keys.size());
        for (final Pair<String, LocaleDeserializer> key : keys) {
            items.add(key.getB().deserialize(key.getA(), config));
        }
    }
}
