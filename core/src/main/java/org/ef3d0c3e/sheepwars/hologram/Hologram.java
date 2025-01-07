package org.ef3d0c3e.sheepwars.hologram;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.ArrayList;
import java.util.List;

public abstract class Hologram
{
    @Getter
    private final ArrayList<HologramComponent> components = new ArrayList<>();

    protected final ArrayList<Integer> availableNetworkIds = new ArrayList<>();

    /**
     * Reserves network ids for later use
     * @param amount Amount to reserve
     */
    protected void reserveNetworkIds(int amount)
    {
        while (availableNetworkIds.size() < amount)
            availableNetworkIds.add(SheepWars.getNextEntityId());
    }

    /**
     * Constructor
     */
    protected Hologram()
    {
    }

    /**
     * Adds component to hologram
     * @param component Component to add
     */
    public void addComponent(final @NonNull HologramComponent component)
    {
        this.components.add(component);
    }

    /**
     * Gets hologram location for player
     * @param cp Player to get location for
     * @return Hologram location
     */
    public abstract @NonNull Location getLocation(final @NonNull CPlayer cp);

    protected boolean sendPredicate(final @NonNull CPlayer cp)
    {
        return true;
    }

    /**
     * Sends hologram to player
     * @param cp Player to send hologram to
     */
    public void send(final @NonNull CPlayer cp)
    {
        final List<HologramComponent> components = this.getComponents();
        final Location location = this.getLocation(cp);

        int idCount = 0;
        for (final HologramComponent component : components)
        {
            reserveNetworkIds(idCount + component.getNetworkOffset());
            // Build & send packets
            for (final PacketWrapper<?> packet : component.build(location, availableNetworkIds.subList(idCount, idCount + component.getNetworkOffset()), cp))
                PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), packet);

            idCount += component.getNetworkOffset();
        }
    }

    /**
     * Un-sends hologram to player
     * @param cp Player to un-send to
     */
    public void unsend(final @NonNull CPlayer cp)
    {
        final WrapperPlayServerDestroyEntities remove = new WrapperPlayServerDestroyEntities();
        remove.setEntityIds(availableNetworkIds.stream().mapToInt(Integer::intValue).toArray());

        PacketEvents.getAPI().getPlayerManager().sendPacket(cp.getHandle(), remove);
    }
}
