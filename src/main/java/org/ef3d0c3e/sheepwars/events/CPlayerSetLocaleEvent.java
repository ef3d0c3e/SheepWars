package org.ef3d0c3e.sheepwars.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Team;
import org.ef3d0c3e.sheepwars.locale.Locale;

public class CPlayerSetLocaleEvent extends Event
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
	final Locale locale;

	/**
	 * Constructor
	 * @note Note fired on join
	 * @param cp Player
	 * @param locale Player's locale
	 */
	public CPlayerSetLocaleEvent(final CPlayer cp, final Locale locale)
	{
		this.player = cp;
		this.locale = locale;
	}
}
