package org.ef3d0c3e.sheepwars.level;

import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class VoidChunkGenerator extends ChunkGenerator
{
    @Override
    public boolean shouldGenerateNoise()
    {
        return false;
    }

    @Override
    public boolean shouldGenerateSurface()
    {
        return false;
    }

    @Override
    public boolean shouldGenerateCaves()
    {
        return false;
    }

    @Override
    public boolean shouldGenerateDecorations()
    {
        return false;
    }

    @Override
    public boolean shouldGenerateMobs()
    {
        return false;
    }

    @Override
    public boolean shouldGenerateStructures()
    {
        return false;
    }

    @Override
    public BiomeProvider getDefaultBiomeProvider(final @NotNull WorldInfo worldInfo)
    {
        return new VoidBiomeProvider();
    }

    @Override
    public @NotNull List<BlockPopulator> getDefaultPopulators(final World world)
    {
        return Collections.emptyList();
    }

}