package org.ef3d0c3e.sheepwars.game;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.EventListenerFactory;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.level.LevelFactory;
import org.ef3d0c3e.sheepwars.level.game.GameLevel;
import org.ef3d0c3e.sheepwars.level.lobby.LobbyLevel;
import org.ef3d0c3e.sheepwars.maps.Map;
import org.ef3d0c3e.sheepwars.maps.MapManager;
import org.ef3d0c3e.sheepwars.packets.PacketListenerFactory;

import java.util.Random;

public class Game {
    private static void changePhase(WantsListen.Target phase)
    {
        Game.phase = phase;

        EventListenerFactory.update(phase);
        //TimedListenerFactory.update(phase);
        PacketListenerFactory.update(phase);
    }

    @Getter
    private static WantsListen.Target phase;

    @Getter
    private static LobbyLevel lobby = null;

    @Getter
    private static GameLevel level = null;

    private static final Random random = new Random();
    public static int nextInt()
    {
        return random.nextInt();
    }

    public static void start(final Map map)
    {
        changePhase(WantsListen.Target.Game);

        level = new GameLevel(map);
        LevelFactory.add(level);

        try {
            level.create();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets default phase to lobby
     */
    static public void init()
    {
        changePhase(WantsListen.Target.Lobby);

        // Register levels
        lobby = new LobbyLevel();
        LevelFactory.add(lobby);

        // Load maps from disk
        MapManager.reloadMaps();

        // Create lobby world
        // Game level is created once it is needed
        // @see CmdHunt
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try {
                    lobby.create();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTask(SheepWars.getPlugin());
    }

    /**
     * @brief Terminates the game
     *
     * This function will perform cleanup and delete temporary game world
     */
    static public void finish()
    {
        // Delete map
        if (level != null && level.getHandle() != null)
        {
            var world = level.getHandle();
            // Teleports all players...
            for (final Player p : world.getPlayers())
                p.teleport(lobby.getSpawn());

            Bukkit.unloadWorld(world.getName(), false);

            if (!world.getWorldFolder().delete())
            {
                Bukkit.getConsoleSender().sendMessage("§cFailed to delete game world! Please delete it manually");
            }
        }
    }

}
