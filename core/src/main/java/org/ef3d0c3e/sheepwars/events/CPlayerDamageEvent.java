package org.ef3d0c3e.sheepwars.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import javax.annotation.Nullable;

@AllArgsConstructor
public class CPlayerDamageEvent extends Event {
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
    @Getter @Nullable
    private CPlayer attacker;
    @Getter
    private EntityDamageEvent originalEvent;
    @Getter
    private boolean wouldDie;

}
