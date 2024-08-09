package org.ef3d0c3e.sheepwars.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.player.CPlayer;


public class CPlayerQuitEvent extends Event
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
    final private CPlayer player;

    /**
     * Constructor
     * @param cp Player
     */
    public CPlayerQuitEvent(final CPlayer cp)
    {
        this.player = cp;
    }
}
