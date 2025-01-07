package org.ef3d0c3e.sheepwars.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPassengers;
import lombok.NonNull;
import org.bukkit.Location;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.List;
import java.util.stream.IntStream;

public abstract class PassengerHologram extends Hologram {
    private final int riderNetworkId;

    /**
     * Constructor
     *
     * @param riderNetworkId The network id of the rider
     */
    protected PassengerHologram(int riderNetworkId) {
        super();
        this.riderNetworkId = riderNetworkId;
    }

    /**
     * Sends hologram to player
     * @param cp Player to send hologram to
     */
    public void send(final @NonNull CPlayer cp)
    {
        super.send(cp);

        final var passengerPacket = new WrapperPlayServerSetPassengers(riderNetworkId, availableNetworkIds.stream().mapToInt(Integer::intValue).toArray());
        PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), passengerPacket);
    }
}
