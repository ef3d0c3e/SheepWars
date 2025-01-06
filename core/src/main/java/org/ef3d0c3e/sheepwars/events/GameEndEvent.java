package org.ef3d0c3e.sheepwars.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.kits.Kit;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.teams.Team;

import javax.annotation.Nullable;

@AllArgsConstructor
public class GameEndEvent extends Event {
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
    @NonNull
    final Team winner;
}
