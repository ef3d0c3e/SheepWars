package org.ef3d0c3e.sheepwars.locale;

import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Interface for locale deserializers
 */
public interface LocaleDeserializer {
    @NonNull Object deserialize(final @NonNull String key, final @NonNull YamlConfiguration configuration);

    /**
     * Deserializer for String
     */
    static final LocaleDeserializer STRING = new LocaleDeserializer() {
        @Override
        public @NonNull Object deserialize(final @NonNull String key, @NonNull YamlConfiguration configuration) {
            final var value = configuration.getString(key);
            if (value == null) {
                throw new RuntimeException(String.format("Cannot find locale item with key '%s'", key));
            }
            return value;
        }
    };

    /**
     * Deserializer for List<String>
     */
    static final LocaleDeserializer STRING_LIST = new LocaleDeserializer() {
        @Override
        public @NonNull Object deserialize(final @NonNull String key, @NonNull YamlConfiguration configuration) {
            final var value = configuration.getStringList(key);
            if (value == null) {
                throw new RuntimeException(String.format("Cannot find locale item with key '%s'", key));
            }
            return value;
        }
    };
}