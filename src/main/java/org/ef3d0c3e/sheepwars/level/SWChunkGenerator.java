package org.ef3d0c3e.sheepwars.level;

import org.bukkit.World;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.ArrayList;
import java.util.List;

public class SWChunkGenerator extends ChunkGenerator
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
	public BiomeProvider getDefaultBiomeProvider(WorldInfo worldInfo)
	{
		return new SWBiomeProvider();
	}

	@Override
	public List<BlockPopulator> getDefaultPopulators(World world)
	{
		List<BlockPopulator> populators = new ArrayList<>();
		return populators;
	}

}
