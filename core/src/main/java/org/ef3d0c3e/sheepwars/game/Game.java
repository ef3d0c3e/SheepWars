package org.ef3d0c3e.sheepwars.game;

import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.EventListenerFactory;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.level.LevelFactory;
import org.ef3d0c3e.sheepwars.level.lobby.LobbyLevel;
import org.ef3d0c3e.sheepwars.packets.PacketListenerFactory;

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

    //@Getter
    //private static GameLevel level;


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
