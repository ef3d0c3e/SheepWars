package org.ef3d0c3e.sheepwars.level;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.Util;

import java.io.File;
import java.lang.reflect.Field;

public class Biomes
{
	private static Registry<Biome> biomeRegistry;
	public static Holder<Biome> lobbyBiome;

	private static void changeRegistryLock(boolean lock)
	{
		MappedRegistry<Biome> materials = getRegistry(Registries.BIOME);
		try
		{
			Class<?> registryMaterialsClass = Class.forName("net.minecraft.core.RegistryMaterials");
			for (Field field : registryMaterialsClass.getDeclaredFields())
			{
				if (field.getType() == boolean.class)
				{
					field.setAccessible(true);
					field.setBoolean(materials, lock);
				}
			}
		}
		catch (ClassNotFoundException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private static <T> MappedRegistry<T> getRegistry(ResourceKey<Registry<T>> key)
	{
		DedicatedServer server = ((CraftServer) Bukkit.getServer()).getServer();
		return (MappedRegistry<T>)server.registryAccess().registryOrThrow(key);
	}

	/**
	 * Loads the biome registry
	 */
	public static void loadRegistry()
	{
		biomeRegistry = ((CraftServer)Bukkit.getServer()).getServer().registryAccess().registryOrThrow(Registries.BIOME);

		// Init lobby biome
		final Biome plains = biomeRegistry.get(ResourceKey.create(Registries.BIOME, new ResourceLocation("minecraft", "plains")));
		final ResourceKey<Biome> customBiomeKey = ResourceKey.create(Registries.BIOME, new ResourceLocation("sheepwars", "lobby"));
		Biome.BiomeBuilder biomeBuilder = new Biome.BiomeBuilder();
		biomeBuilder.hasPrecipitation(false);
		biomeBuilder.temperature(plains.getBaseTemperature());
		biomeBuilder.temperatureAdjustment(Biome.TemperatureModifier.NONE);
		biomeBuilder.downfall(0.f);
		biomeBuilder.mobSpawnSettings(MobSpawnSettings.EMPTY);
		biomeBuilder.generationSettings(BiomeGenerationSettings.EMPTY);

		final FileConfiguration config = YamlConfiguration.loadConfiguration(new File(SheepWars.getPlugin().getDataFolder().getAbsolutePath() + "/sw_lobby.yml"));

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

	/**
	 * Registers a biome
	 * @param biomeKey Registered biome's key
	 * @param biome Biome to registr
	 * @returns Registered biome or null
	 */
	public static Holder<Biome> registerBiome(final ResourceKey<Biome> biomeKey, final Biome biome)
	{
		if (biomeRegistry.containsKey(biomeKey))
			return biomeRegistry.getHolderOrThrow(biomeKey);

		Holder<Biome> held = null;
		changeRegistryLock(false); // Unlock

		// Register
		held = Registry.registerForHolder(getRegistry(Registries.BIOME), biomeKey.location(), biome);

		getRegistry(Registries.BIOME).freeze(); // Relock
		//changeRegistryLock(true);

		return held;
	}

	/**
	 * Sets biome at location
	 * @param loc Location to set biome at
	 * @param biome Biome to set at location
	 */
	public static void setBiome(final Location loc, final Holder<Biome> biome)
	{
		final ServerLevel world = ((CraftWorld)loc.getWorld()).getHandle();
		final BlockPos pos = new BlockPos(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

		final ChunkAccess chunk = world.getChunk(pos);
		if (chunk != null)
		{
			chunk.setBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2, biome);
		}
	}

	/**
	 * Sets biome over whole clipboard pasted at a location
	 * @param origin Where the schematic should be placed (important to get world data)
	 * @param clipboard Schematic to change
	 * @param biome Biome to set
	 */
	public static void setBiome(final Location origin, final Clipboard clipboard, final Holder<Biome> biome)
	{
		final ServerLevel world = ((CraftWorld) origin.getWorld()).getHandle();

		for (int x = clipboard.getMinimumPoint().getX(); x <= clipboard.getMaximumPoint().getX(); ++x)
			for (int y = clipboard.getMinimumPoint().getY(); y <= clipboard.getMaximumPoint().getY(); ++y)
				for (int z = clipboard.getMinimumPoint().getZ(); z <= clipboard.getMaximumPoint().getZ(); ++z)
				{
					final BlockPos pos = new BlockPos(origin.getBlockX() - clipboard.getOrigin().getBlockX() + x, origin.getBlockY()  - clipboard.getOrigin().getY()+ y, origin.getBlockZ() - clipboard.getOrigin().getBlockZ() + z);
					final ChunkAccess chunk = world.getChunk(pos);
					if (chunk != null)
						chunk.setBiome(pos.getX() >> 2, pos.getY() >> 2, pos.getZ() >> 2, biome);
				}
	}
}
