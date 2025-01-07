package org.ef3d0c3e.sheepwars.hologram;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.List;

/**
 * Abstract class for a Hologram's component
 */
public abstract class HologramComponent
{
    @Getter
    private Vector offset;

    protected HologramComponent(final @NonNull Vector offset)
    {
        this.offset = offset;
    }

    /**
     * Gets the amount of network entity id that the component takes
     * @return The number of entity id taken by the component
     */
    protected abstract int getNetworkOffset();

    /**
     * Builds hologram packets
     *
     * @param networkIds Available network ids
     * @param cp        Player to build hologram component for
     * @return Packets to display component
     */
    protected abstract @NonNull List<PacketWrapper<?>> build(final @NonNull Location location, final @NonNull List<Integer> networkIds, final @NonNull CPlayer cp);
}
