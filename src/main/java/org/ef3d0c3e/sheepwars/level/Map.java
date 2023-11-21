package org.ef3d0c3e.sheepwars.level;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BlockState;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a playable map
 */
public class Map
{
	/**
	 * Gets the map folder
	 * @return Gets folder where maps are stored
	 */
	public static File getMapFolder()
	{
		return new File(SheepWars.getPlugin().getDataFolder().getAbsolutePath() + "/maps");
	}

	private static HashMap<String, Map> mapList = null;
	private static HashMap<CPlayer, Map> mapVotes = null;

	/**
	 * Sets vote of player
	 * @param cp Player
	 * @param map Map player voted for
	 * @note if map == null, will remove player from vote list
	 */
	public static void setVote(final CPlayer cp, final Map map)
	{
		if (map == null) // Remove
			mapVotes.remove(cp);
		else
			mapVotes.put(cp, map);

		for (final Map nmap : mapList.values())
			nmap.vote = 0;
		for (final Map nmap : mapVotes.values())
			++nmap.vote;
	}

	/**
	 * Gets whether player has voted for map
	 * @param cp Player
	 * @param map Map
	 * @return True if player has voted for map, false otherwise
	 */
	public static boolean votedFor(final CPlayer cp, final Map map)
	{
		return mapVotes.get(cp) == map;
	}

	/**
	 * Gets list of maps
	 * @return Maps
	 */
	public static HashMap<String, Map> getMaps()
	{
		return mapList;
	}

	/**
	 * Gets list of votes
	 * @return Maps
	 */
	public static HashMap<CPlayer, Map> getVotes()
	{
		return mapVotes;
	}


	/**
	 * Reloads list of maps from disk
	 */
	public static void reloadMaps()
	{
		mapList = new HashMap<>();
		mapVotes = new HashMap<>();
		File dir = getMapFolder();
		for (final File file : dir.listFiles())
		{
			try
			{
				if (!file.getName().endsWith(".yml"))
					continue;

				final String name = file.getName().substring(0, file.getName().lastIndexOf(".yml"));

				final File schematic = new File(file.getParentFile().getAbsolutePath() + "/" + name + ".schem");
				if (!schematic.exists())
					throw new FileNotFoundException();

				FileConfiguration config = YamlConfiguration.loadConfiguration(file);

				Map map = new Map();
				map.name = name;
				map.displayName = config.getString("displayname");
				map.icon = Material.getMaterial(config.getString("icon"));
				map.schematic = schematic;

				map.offset = Util.parseVector(config.getString("offset"));
				map.limboBegin = config.getInt("limbo-begin");
				map.limboEnd = config.getInt("limbo-end");
				map.worldTime = config.getInt("world-time");
				map.lowestWool = Util.parseVector(config.getString("bonus-lowest"));
				map.highestWool = Util.parseVector(config.getString("bonus-highest"));
				map.redSpawns = new ArrayList<>();
				for (final String spawn : config.getStringList("spawns.red"))
					map.redSpawns.add(Util.parseVector(spawn));
				map.redYaw = (float)config.getDouble("yaw.red");

				map.blueSpawns = new ArrayList<>();
				for (final String spawn : config.getStringList("spawns.blue"))
					map.blueSpawns.add(Util.parseVector(spawn));
				map.blueYaw = (float)config.getDouble("yaw.blue");

				mapList.put(name, map);
				Bukkit.getConsoleSender().sendMessage(MessageFormat.format("§cSheepWars>§7 Added map ''{0}''!", name));
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Getter
	protected String name;
	@Getter
	protected String displayName;
	@Getter
	protected Material icon;
	@Getter
	protected File schematic;
	@Getter
	protected Clipboard mapContent;

	@Getter
	protected Vector offset;
	@Getter
	protected Vector lowestPoint;
	@Getter
	protected Vector highestPoint;
	@Getter
	protected int limboBegin;
	@Getter
	protected int limboEnd;
	@Getter
	protected int worldTime;
	@Getter
	protected Vector lowestWool;
	@Getter
	protected Vector highestWool;
	@Getter
	protected ArrayList<Vector> redSpawns;
	@Getter
	protected float redYaw;
	@Getter
	protected ArrayList<Vector> blueSpawns;
	@Getter
	protected float blueYaw;

	@Getter
	protected int vote;

	/**
	 * Gets whether location is in bound
	 * @param loc Location
	 * @return true if bounded, false otherwise
	 */
	public boolean isBounded(final Location loc)
	{
		return isBounded(loc, false);
	}

	/**
	 * Gets whether location is in bound
	 * @param loc Location
	 * @param infiniteUp If set to true, will allow infinite height
	 * @return true if bounded, false otherwise
	 */
	public boolean isBounded(final Location loc, boolean infiniteUp)
	{
		return
			lowestPoint.getX() <= loc.getX() &&
			highestPoint.getX() >= loc.getX() &&
			lowestPoint.getY() <= loc.getY() &&
			(infiniteUp || highestPoint.getY() >= loc.getY()) &&
			lowestPoint.getZ() <= loc.getZ() &&
			highestPoint.getZ() >= loc.getZ();
	}

	/**
	 * Gets strength of limbo effect
	 * @param y Entity height
	 * @return Strength [0,1]
	 */
	public float limboStrength(double y)
	{
		if (y >= limboBegin)
			return 0.f;
		if (y <= limboEnd)
			return 1.f;

		return 1.f-((float)(y-limboEnd)) / ((float)(limboBegin-limboEnd));
	}

	/**
	 * Gets block from original map
	 * @param loc Location of block after pasting
	 * @return
	 */
	public BlockState getOriginalBlock(final Location loc)
	{
		return mapContent.getBlock(BlockVector3.at(
			loc.getBlockX()-offset.getBlockX(),
			loc.getBlockY()-offset.getBlockY()-64,
			loc.getBlockZ()-offset.getBlockZ()
		).add(mapContent.getOrigin()));
	}

	/**
	 * Generate map in world
	 * @return World the map is in
	 */
	public World generate()
	{
		try
		{
			//final World erase = Bukkit.getWorld("sheepwars");
			//if (erase != null)
			//{
			//	Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Suppression du monde 'sheepwars'");
			//	Bukkit.getServer().unloadWorld(erase, false);
			//	FileUtils.deleteDirectory(erase.getWorldFolder());
			//}

			Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Création du monde 'sheepwars'");
			final WorldCreator wc = new WorldCreator("sheepwars");
			wc.generator(new SWChunkGenerator());
			final World world = wc.createWorld();

			ClipboardFormat format = ClipboardFormats.findByFile(schematic);
			ClipboardReader reader = format.getReader(new FileInputStream(schematic));
			mapContent = reader.read();

			com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);
			EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld);
			Operation operation = new ClipboardHolder(mapContent).createPaste(editSession)
				.to(BlockVector3.at(offset.getX(), offset.getY()+64, offset.getZ()))
				.ignoreAirBlocks(true)
				.build();

			lowestPoint = new Vector(
				mapContent.getOrigin().getX() - mapContent.getMaximumPoint().getX() + offset.getX(),
				mapContent.getMinimumPoint().getY() - mapContent.getOrigin().getY() + offset.getY() + 64,
				mapContent.getOrigin().getZ() - mapContent.getMaximumPoint().getZ() + offset.getZ()+1
			);
			highestPoint = new Vector(
				mapContent.getOrigin().getX() - mapContent.getMinimumPoint().getX() + offset.getX()-1,
				lowestPoint.getY() + mapContent.getDimensions().getY() - 1,
				mapContent.getOrigin().getZ() - mapContent.getMinimumPoint().getZ() + offset.getZ()
			);

			Operations.complete(operation);
			editSession.close();

			world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
			world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
			world.setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
			world.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
			world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
			world.setGameRule(GameRule.DO_INSOMNIA, false);
			world.setGameRule(GameRule.SPAWN_RADIUS, 0);
			world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
			world.setGameRule(GameRule.NATURAL_REGENERATION, false);
			world.setGameRule(GameRule.DO_TILE_DROPS, false);
			world.setTime(worldTime);
			world.setWeatherDuration(0);
			world.setSpawnLocation(0, world.getHighestBlockYAt(0, 0), 0);

			return world;
		}
		catch (IOException | WorldEditException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
