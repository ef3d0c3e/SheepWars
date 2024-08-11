package org.ef3d0c3e.sheepwars.game;

import lombok.Getter;
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
    private static LobbyLevel lobby;

    @Getter
    private static GameLevel level;

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
        //level = new GameLevel();
        //LevelFactory.add(level);

        // Load maps
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

}
