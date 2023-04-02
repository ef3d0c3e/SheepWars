package org.ef3d0c3e.sheepwars.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.Team;

/**
 * Called when game starts
 */
public class GameEndEvent extends Event
{
	private static final HandlerList HANDLERS_LIST = new HandlerList();
	public static HandlerList getHandlerList()
	{
		return HANDLERS_LIST;
	}
	@Override
	public HandlerList getHandlers()
	{
		return HANDLERS_LIST;
	}

	@Getter
	private Team winner;

	/**
	 * Constructor
	 */
	public GameEndEvent(final Team winner)
	{
		this.winner = winner;
	}
}
