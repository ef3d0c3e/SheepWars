package org.ef3d0c3e.sheepwars.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Team;

public class CPlayerTeamChangeEvent extends Event
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
	final Team oldTeam;
	@Getter
	final Team newTeam;

	/**
	 * Constructor
	 * @note Note fired on join
	 * @param cp Player
	 * @param oldTeam Player's old team
	 * @param newTeam Player's new team
	 */
	public CPlayerTeamChangeEvent(final CPlayer cp, final Team oldTeam, final Team newTeam)
	{
		this.player = cp;
		this.oldTeam = oldTeam;
		this.newTeam = newTeam;
	}
}
