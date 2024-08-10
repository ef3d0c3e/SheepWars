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
import java.util.List;

public class HologramFactory
{
    private static final HashMap<Integer, Hologram> registry = new HashMap<>();

    public static void register(final @NonNull Hologram hologram)
    {
        registry.put(hologram.getNetworkId(), hologram);
    }

    public static void unregister(final @NonNull Hologram hologram)
    {
        registry.remove(hologram.getNetworkId());
    }

    /**
     * Sends hologram to player
     * @param hologram Hologram to send
     * @param cp Player to send hologram to
     */
    public static void send(final @NonNull Hologram hologram, final @NonNull CPlayer cp)
    {
        final List<HologramComponent> components = hologram.getComponents();
        final Location location = hologram.getLocation(cp);

        int networkId = hologram.getNetworkId();
        for (final HologramComponent component : components)
        {
            // Build & send packets
            for (final PacketWrapper<?> packet : component.build(location, networkId, cp))
                PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), packet);

            // Offset network id
            networkId += component.getNetworkOffset();
        }
    }

    /**
     * Un-sends hologram to player
     * @param hologram Hologram to un-send
     * @param cp Player to un-send to
     */
    public static void unsend(final @NonNull Hologram hologram, final @NonNull CPlayer cp)
    {
        int networkId = hologram.getNetworkId();

        final List<HologramComponent> components = hologram.getComponents();
        int size = 0;
        for (final HologramComponent component : components)
        {
            size += component.getNetworkOffset();
        }

        final int[] list = new int[size];
        int pos = 0;
        for (final HologramComponent component : hologram.getComponents())
        {
            for (int i = 0; i < component.getNetworkOffset(); ++i) {
                list[pos] = networkId + i;
                ++pos;
            }
            networkId += component.getNetworkOffset();
        }

        final WrapperPlayServerDestroyEntities remove = new WrapperPlayServerDestroyEntities();
        remove.setEntityIds(list);

        PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), remove);
    }


    @WantsListen(phase = WantsListen.Target.Always)
    public static class HologramListener implements Listener
    {
        // TODO..., on chunk load request [something...]
        @EventHandler
        public void onJoin(final CPlayerJoinEvent ev)
        {
            final CPlayer cp = ev.getPlayer();
            for (final Hologram hologram : registry.values())
            {
                if (!hologram.sendPredicate(cp)) continue;

                send(hologram, cp);
            }
        }
    }
}
