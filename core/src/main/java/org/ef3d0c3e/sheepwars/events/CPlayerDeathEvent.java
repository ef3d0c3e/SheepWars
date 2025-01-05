package org.ef3d0c3e.sheepwars.events;

import lombok.Getter;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.jetbrains.annotations.NotNull;

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
    private @NotNull CPlayer victim;
    @Getter
    private @Nullable CPlayer playerKiller;
    @Getter
    private @NotNull LivingEntity killer;

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

