package org.ef3d0c3e.sheepwars.sheeps;

import org.bukkit.damage.DamageSource;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;

@WantsListen(phase = WantsListen.Target.Always)
public class SheepEvent implements Listener {
    /**
     * Cancels fall damage for sheeps
     * @param ev The event
     */
    @EventHandler
    void onSheepDamage(final EntityDamageEvent ev) {
        if (ev.getCause() == EntityDamageEvent.DamageCause.FALL) return;

        final var sheep = BaseSheep.getInstance(ev.getEntity());
        if (sheep == null) return;

        ev.setCancelled(true);
    }
}
