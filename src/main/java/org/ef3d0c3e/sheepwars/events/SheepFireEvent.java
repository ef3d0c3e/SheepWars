package org.ef3d0c3e.sheepwars.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Team;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;

/**
 * Called when game starts
 */
public class SheepFireEvent extends Event
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
	private BaseSheep sheep;

	@Getter
	private CPlayer shooter;

	@Getter
	private boolean launched;

	@Getter
	private String sheepName;

	/**
	 * Constructor
	 */
	public SheepFireEvent(final BaseSheep sheep, final CPlayer shooter, final boolean launched, final String sheepName)
	{
		this.sheep = sheep;
		this.shooter = shooter;
		this.launched = launched;
		this.sheepName = sheepName;
	}
}
