package org.ef3d0c3e.sheepwars.level;

import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.HashMap;

public class LevelFactory
{
    /**
     * All registered levels
     */
    public static HashMap<String, Level> levels = new HashMap<>();

    public static void add(final @NonNull Level l) { levels.put(l.getWorldName(), l); }

    public static @Nullable Level get(final @NonNull String name) { return levels.get(name); }
}

