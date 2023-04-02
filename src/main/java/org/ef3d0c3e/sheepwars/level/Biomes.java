package org.ef3d0c3e.sheepwars.level;

import com.mojang.serialization.Lifecycle;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_19_R1.CraftServer;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.Util;

import java.io.File;
import java.lang.reflect.Field;

public class Biomes
{
	static Registry<Biome> biomeRegistry;
	public static Holder<Biome> lobbyBiome;

	public static void loadBiomes()
	{
		biomeRegistry = ((CraftServer) Bukkit.getServer()).getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);

		Biome plains = biomeRegistry.get(ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("minecraft", "plains")));

		// Lobby
		{
			ResourceKey<Biome> customBiomeKey = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("sheepwars", "lobby"));
			Biome.BiomeBuilder biomeBuilder = new Biome.BiomeBuilder();
			biomeBuilder.precipitation(Biome.Precipitation.NONE);
			biomeBuilder.temperature(plains.getBaseTemperature());
			biomeBuilder.temperatureAdjustment(Biome.TemperatureModifier.NONE);
			biomeBuilder.downfall(0.f);
			biomeBuilder.mobSpawnSettings(MobSpawnSettings.EMPTY);
			biomeBuilder.generationSettings(BiomeGenerationSettings.EMPTY);

			FileConfiguration config = YamlConfiguration.loadConfiguration(new File(SheepWars.plugin.getDataFolder().getAbsolutePath() + "/sw_lobby.yml"));

			BiomeSpecialEffects.Builder effectsBuilder = new BiomeSpecialEffects.Builder();
			effectsBuilder
				.fogColor(Util.parseColor(config.getString("biome.fog")))
				.skyColor(Util.parseColor(config.getString("biome.sky")))
				.waterFogColor(Util.parseColor(config.getString("biome.waterfog")))
				.waterColor(Util.parseColor(config.getString("biome.water")))
				.grassColorOverride(Util.parseColor(config.getString("biome.grass")))
				.foliageColorOverride(Util.parseColor(config.getString("biome.foliage")));
			biomeBuilder.specialEffects(effectsBuilder.build());


			lobbyBiome = registerBiome(customBiomeKey, biomeBuilder.build());
		}
	}

	/**
	 * Registers a biome
	 */
	public static Holder<Biome> registerBiome(final ResourceKey<Biome> biomeKey, final Biome biome)
	{
		if (biomeRegistry.containsKey(biomeKey))
			return biomeRegistry.getHolderOrThrow(biomeKey);

		Holder<Biome> held = null;
		try
		{
			// Un-freeze
			Field frozen = MappedRegistry.class.getDeclaredField("ca"); // net.minecraft.core.MappedRegistry [boolean frozen]
			frozen.setAccessible(true);
			frozen.set(((MappedRegistry<Biome>)biomeRegistry), false);

			held = ((WritableRegistry<Biome>) biomeRegistry).register(biomeKey, biome, Lifecycle.stable());

			// Freeze
			frozen.set(((MappedRegistry<Biome>)biomeRegistry), true);
		}
		catch (IllegalAccessException | NoSuchFieldException e)
		{
			e.printStackTrace();
		}

		return held;
	}

	/**
	 * Sets biome at location
	 */
	public static void setBiome(final Location loc, final Holder<Biome> biome)
	{
		final ServerLevel world = ((CraftWorld)loc.getWorld()).getHandle();
		final BlockPos pos = new BlockPos(loc.getX(), loc.getY(), loc.getZ());

		final ChunkAccess chunk = world.getChunk(pos);
		if (chunk != null)
		{
			chunk.setBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2, biome);
		}
	}

	public static void setBiome(final Location origin, final Clipboard clipboard, final Holder<Biome> biome)
	{
		final ServerLevel world = ((CraftWorld) origin.getWorld()).getHandle();

		for (int x = clipboard.getMinimumPoint().getX(); x <= clipboard.getMaximumPoint().getX(); ++x)
			for (int y = clipboard.getMinimumPoint().getY(); y <= clipboard.getMaximumPoint().getY(); ++y)
				for (int z = clipboard.getMinimumPoint().getZ(); z <= clipboard.getMaximumPoint().getZ(); ++z)
				{
					final BlockPos pos = new BlockPos(origin.getX() - clipboard.getOrigin().getX() + x, origin.getY()  - clipboard.getOrigin().getY()+ y, origin.getZ() - clipboard.getOrigin().getZ() + z);
					final ChunkAccess chunk = world.getChunk(pos);
					if (chunk != null)
						chunk.setBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2, biome);
				}
	}
}
