package org.ef3d0c3e.sheepwars.maps;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Sheep;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.Util;
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
     * All the votes
     */
    private static HashMap<CPlayer, Map> mapVotes = null;

    public static @Nullable Map getMap(final String name) {
        return mapList.get(name);
    }

    /**
     * Loads maps from disk
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
                SheepWars.consoleMessage("Added map '" + name + "'");
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }
}
