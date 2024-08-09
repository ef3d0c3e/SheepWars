package org.ef3d0c3e.sheepwars.level;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;

/**
 * Wrapper for {@link org.bukkit.World};
 */
public abstract class Level
{
    /**
     * Constructor
     * @param name World name
     */
    protected Level(final @NonNull String name)
    {
        this.worldName = name;
    }

    @Getter
    private final String worldName;
    @Getter
    private World handle; /* World's handle */

    /**
     * Stores whether the world has been initialized yet
     * The world get initialized after `postWorld()` call
     */
    private boolean initialized = false;

    protected abstract @NonNull World generate() throws Exception;

    /**
     * Creates world or load world
     * Adds level to level list
     */
    public void create() throws Exception
    {
        LevelFactory.add(this);

        // Try to get world handle
        handle = Bukkit.getWorld(worldName);
        if (handle != null) return;

        // Create World
        handle = generate();
    }

    /**
     * Called when world has finished initialization
     * i.e. getHandle() is non-null
     */
    protected void postWorld() {}

    /**
     * On chunk load
     * @param chunk Loaded chunk
     * @param newChunk Whether loaded chuck is a new chunk
     */
    protected abstract void onLoad(final Chunk chunk, boolean newChunk);

    @WantsListen(phase = WantsListen.Target.Always)
    public static class Events implements Listener
    {
        @EventHandler
        public void onChunkLoad(final ChunkLoadEvent ev)
        {
            final Level level = LevelFactory.get(ev.getWorld().getName());
            if (level == null) return;
            if (!level.initialized)
            {
                level.handle = Bukkit.getWorld(level.getWorldName());
                level.postWorld();
                level.initialized = true;
            }
            level.onLoad(ev.getChunk(), ev.isNewChunk());
        }

        @EventHandler
        public void onWorldInit(final WorldInitEvent ev)
        {
            ev.getWorld().setKeepSpawnInMemory(false);
        }
    }
}

