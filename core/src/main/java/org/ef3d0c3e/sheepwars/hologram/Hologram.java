package org.ef3d0c3e.sheepwars.hologram;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Location;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.ArrayList;

public abstract class Hologram
{
    @Getter
    private final int networkId;
    @Getter
    private final ArrayList<HologramComponent> components;

    /**
     * Constructor
     * @param networkId Base network id
     */
    protected Hologram(final int networkId)
    {
        this.networkId = networkId;
        components = new ArrayList<>();
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
}
