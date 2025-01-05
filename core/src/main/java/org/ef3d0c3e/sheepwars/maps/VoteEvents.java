package org.ef3d0c3e.sheepwars.maps;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.player.CPlayer;

@WantsListen(phase = WantsListen.Target.Lobby)
public class VoteEvents implements Listener {
    /**
     * Removes the player vote on quit
     * @param e The event
     */
    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        final CPlayer cp = CPlayer.get(e.getPlayer());
        MapManager.setPlayerVote(cp, null);

    }
}
