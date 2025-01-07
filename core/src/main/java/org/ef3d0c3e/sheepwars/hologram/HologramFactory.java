package org.ef3d0c3e.sheepwars.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class HologramFactory
{
    private static final HashSet<Hologram> registry = new HashSet<>();

    public static void register(final @NonNull Hologram hologram)
    {
        registry.add(hologram);
    }

    public static void unregister(final @NonNull Hologram hologram)
    {
        registry.remove(hologram);
    }

    @WantsListen(phase = WantsListen.Target.Always)
    public static class HologramListener implements Listener
    {
        // TODO..., on chunk load request [something...]
        @EventHandler
        public void onJoin(final CPlayerJoinEvent ev)
        {
            final CPlayer cp = ev.getPlayer();
            for (final Hologram hologram : registry)
            {
                if (!hologram.sendPredicate(cp)) continue;

                hologram.send(cp);
            }
        }
    }
}
