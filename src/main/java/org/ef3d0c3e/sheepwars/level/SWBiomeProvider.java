package org.ef3d0c3e.sheepwars.level;

import com.google.common.collect.Lists;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.List;


public class SWBiomeProvider extends BiomeProvider
{
	public Biome getBiome(WorldInfo worldInfo, int x, int y, int z)
	{
		return Biome.PLAINS;
	}

	@Override
	public List<Biome> getBiomes(WorldInfo worldInfo)
	{
		return Lists.newArrayList(Biome.PLAINS);
	}

}

