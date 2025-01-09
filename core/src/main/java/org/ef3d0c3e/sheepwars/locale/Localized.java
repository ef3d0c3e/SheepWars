package org.ef3d0c3e.sheepwars.locale;

import lombok.NonNull;
import org.ef3d0c3e.sheepwars.player.CPlayer;

/**
 * Represents a localized variable
 * @param <T> Type of the localized value
 */
public class Localized<T> {
    /**
     * Unique identifier in the locale table
     */
    private final int id;

    protected Localized(final int id)
    {
        this.id = id;
    }

    /**
     * Gets a localized version of this value
     * @param s The locale subscriber
     * @return The localized version of this value
     */
    public @NonNull T localize(final @NonNull LocaleSubscriber s)
    {
        return s.getLocale().get(id);
    }

    /**
     * Gets a localized version of this value
     * @param locale The locale
     * @return The localized version of this value
     */
    public @NonNull T localize(final @NonNull Locale locale)
    {
        return locale.get(id);
    }
}
