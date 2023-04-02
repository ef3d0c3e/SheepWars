package org.ef3d0c3e.sheepwars;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import sun.jvm.hotspot.utilities.ProcImageClassLoader;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

public class PlayerInteraction
{
	public static class Events implements Listener
	{
		@EventHandler
		public void onCPlayerJoin(final CPlayerJoinEvent ev)
		{
			ev.getPlayer().getInteractionData().perform(ev.getPlayer());
		}
	}

	@AllArgsConstructor
	public static class PILocation
	{
		@Getter @Setter
		final private Location location;
	}

	public static class Data
	{
		private PILocation location;

		public Data()
		{
			location = null;
		}

		public void perform(final CPlayer cp)
		{
			if (cp.isAlive())
			{
				if (location != null)
				{
					cp.getHandle().teleport(location.getLocation());

					location = null;
				}
			}
		}

		public void add(final PILocation q)
		{
			location = q;
		}
	}

	public static PILocation teleport(final CPlayer cp, final Location loc, final boolean now)
	{
		if (now && cp.isOnline())
		{
			cp.getHandle().teleport(loc);
			return null;
		}
		cp.getInteractionData().location = new PILocation(loc);
		return cp.getInteractionData().location;
	}
}
