package org.ef3d0c3e.sheepwars.events;

import lombok.RequiredArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.player.skin.Skin;

@RequiredArgsConstructor
public class SkinChangeEvent extends Event
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
    @NonNull
    final CPlayer player;
    @Getter
    final Skin oldSkin;
    @Getter
    final Skin newSkin;
}

