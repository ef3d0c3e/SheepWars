package org.ef3d0c3e.sheepwars.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.player.CPlayer;


public class CPlayerJoinEvent extends Event
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
    @Getter
    final private boolean newPlayer;

    /**
     * Constructor
     * @param cp Player
     * @param newPlayer Whether player is a new player or not (first time joining)
     */
    public CPlayerJoinEvent(final CPlayer cp, final boolean newPlayer)
    {
        this.player = cp;
        this.newPlayer = newPlayer;
    }
}

