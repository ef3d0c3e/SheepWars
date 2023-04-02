package org.ef3d0c3e.sheepwars.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.CPlayer;

import javax.annotation.Nullable;

public class CPlayerDeathEvent extends Event
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
	private CPlayer victim;
	@Getter
	private CPlayer playerKiller;
	@Getter
	private LivingEntity killer;

	public CPlayerDeathEvent(final CPlayer victim, final CPlayer playerKiller)
	{
		this.victim = victim;
		this.playerKiller = playerKiller;
	}

	public CPlayerDeathEvent(final CPlayer victim, final LivingEntity killer)
	{
		this.victim = victim;
		this.killer = killer;
	}
}
