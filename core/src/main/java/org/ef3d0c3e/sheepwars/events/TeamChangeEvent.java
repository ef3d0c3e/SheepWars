package org.ef3d0c3e.sheepwars.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.teams.Team;

import javax.annotation.Nullable;


@AllArgsConstructor
public class TeamChangeEvent extends Event
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
    final private @Nullable Team oldTeam;
    @Getter
    final private @Nullable Team newTeam;
}

