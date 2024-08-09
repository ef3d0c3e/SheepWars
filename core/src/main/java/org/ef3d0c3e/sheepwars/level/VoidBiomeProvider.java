package org.ef3d0c3e.sheepwars.level;

import com.google.common.collect.Lists;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Provides the biome for the plugin's generator
 */
public class VoidBiomeProvider extends BiomeProvider
{
    public @NotNull Biome getBiome(@NotNull WorldInfo worldInfo, int x, int y, int z)
    {
        return Biome.PLAINS;
    }

    @Override
    public @NotNull List<Biome> getBiomes(@NotNull WorldInfo worldInfo)
    {
        return Lists.newArrayList(Biome.PLAINS);
    }

}

