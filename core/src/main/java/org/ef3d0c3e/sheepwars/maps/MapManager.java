package org.ef3d0c3e.sheepwars.maps;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Sheep;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.events.CPlayerQuitEvent;
import org.ef3d0c3e.sheepwars.events.MapVoteEvent;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class MapManager {
    /**
     * Gets the map folder
     * @return Gets folder where maps are stored
     */
    public static File getMapFolder()
    {
        return new File(SheepWars.getPlugin().getDataFolder().getAbsolutePath() + "/maps");
    }

    /**
     * All the discovered maps
     */
    private static HashMap<String, Map> mapList = null;
    /**
     * Votes of each players
     */
    private static HashMap<CPlayer, @NonNull Map> playerVotes = new HashMap<>();

    /**
     * Gets a map by it's name
     * @param name Name of the map to get
     * @return The map if found, null otherwise
     */
    public static @Nullable Map getMap(final String name) {
        return mapList.get(name);
    }

    /**
     * Gets a map by it's id
     * @param id Id of the map to get
     * @return The map if found, null otherwise
     */
    public static @Nullable Map getMap(final int id) {
        int counter = 0;
        for (Map map : mapList.values()) {
            if (counter == id)
                return map;
            ++counter;
        }
        return null;
    }

    /**
     * Loads maps from disk
     */
    public static void reloadMaps()
    {
        mapList = new HashMap<>();
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
                SheepWars.consoleMessage("Added map '" + name + "'");
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    public interface ForEachMap
    {
        public void operation(final Map map);
    }

    /**
     * Executes lambda for all maps
     * @param f Lambda expression to execute on all maps
     */
    public static void forEach(final ForEachMap f)
    {
        for (final Map map : mapList.values())
            f.operation(map);
    }

    /**
     * Sets the vote of a player
     * @param player Player to set the vote of
     * @param map Map to vote for (may be null)
     */
    public static void setPlayerVote(final @NonNull CPlayer player, final @Nullable Map map)
    {
        final Map oldMap = playerVotes.get(player);
        playerVotes.remove(player);
        if (map == null) {
            Bukkit.getPluginManager().callEvent(new MapVoteEvent(player, oldMap, map));
            return;
        }

        playerVotes.put(player, map);
        Bukkit.getPluginManager().callEvent(new MapVoteEvent(player, oldMap, map));
    }

    /**
     * Gets the map a player voted for
     * @param player The player
     * @return The map voted for by the player (null if none)
     */
    public static @Nullable Map getPlayerVote(final @NonNull CPlayer player)
    {
        return playerVotes.get(player);
    }

    /**
     * Gets the number of votes for every maps
     * @return The tables of votes per maps
     */
    public static @NonNull HashMap<@NonNull Map, Integer> getVotes()
    {
        HashMap<@NonNull Map, Integer> votes = new HashMap<>();

        playerVotes.forEach((player, map) -> {
            if (votes.containsKey(map))
            {
                final int count = votes.get(map);
                votes.put(map, count + 1);
            }
            else {
                votes.put(map, 1);
            }
        });
        for (final Map map : mapList.values())
        {
            if (votes.containsKey(map))
                continue;
            votes.put(map, 0);
        }

        return votes;
    }
}
