package org.ef3d0c3e.sheepwars;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerQuitEvent;

public class Events implements Listener
{
	@EventHandler
	public void onJoin(final PlayerJoinEvent ev)
	{
		ev.setJoinMessage(null);

		CPlayer cp = null;
		boolean newPlayer = false;
		if (CPlayer.getPlayer(ev.getPlayer()) == null) // New player
		{
			cp = CPlayer.addPlayer(ev.getPlayer());
			newPlayer = true;
		}
		else // Update underlying player struct
		{
			cp = CPlayer.getPlayer(ev.getPlayer());
			cp.setHandle(ev.getPlayer());
		}
		Bukkit.getPluginManager().callEvent(new CPlayerJoinEvent(cp, newPlayer));
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent ev)
	{
		ev.setQuitMessage(null);

		Bukkit.getPluginManager().callEvent(new CPlayerQuitEvent( CPlayer.getPlayer(ev.getPlayer()) ));
	}

	@EventHandler
	public void onCraft(final CraftItemEvent ev)
	{
		ev.setCancelled(true);
	}
}
