package org.ef3d0c3e.sheepwars.events;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.ef3d0c3e.sheepwars.player.CPlayer;

/**
 * Transforms minecraft's events to events for SheepWars
 * Mainly used for wrapping the player into the CPlayer wrapper
 */
@WantsListen(phase = WantsListen.Target.Always)
public class Events implements Listener {
    @EventHandler
    public void onJoin(final PlayerJoinEvent ev)
    {
        ev.setJoinMessage(null);

        CPlayer cp = CPlayer.get(ev.getPlayer());
        boolean newPlayer = false;
        if (cp == null) // New player
            Bukkit.getPluginManager().callEvent(new CPlayerJoinEvent(
                    CPlayer.add(ev.getPlayer()), true));
        else
        {
            // Update
            cp.setHandle(ev.getPlayer());
            Bukkit.getPluginManager().callEvent(new CPlayerJoinEvent(cp, false));
        }

    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent ev)
    {
        ev.setQuitMessage(null);

        Bukkit.getPluginManager().callEvent(new CPlayerQuitEvent(CPlayer.get(ev.getPlayer())));
    }

}
