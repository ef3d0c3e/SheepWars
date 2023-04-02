package org.ef3d0c3e.sheepwars.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when game starts
 */
public class GameStartEvent extends Event
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

	/**
	 * Constructor
	 */
	public GameStartEvent()
	{

	}
}
