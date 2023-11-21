package org.ef3d0c3e.sheepwars.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.kits.Kit;

public class CPlayerKitChangeEvent extends Event
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
	final CPlayer player;
	@Getter
	final Kit oldKit;
	@Getter
	final Kit newKit;

	/**
	 * Constructor
	 * @note Note fired on join
	 * @param cp Player
	 * @param oldTeam Player's old team
	 * @param newTeam Player's new team
	 */
	public CPlayerKitChangeEvent(final CPlayer cp, final Kit oldKit, final Kit newKit)
	{
		this.player = cp;
		this.oldKit = oldKit;
		this.newKit = newKit;
	}
}
