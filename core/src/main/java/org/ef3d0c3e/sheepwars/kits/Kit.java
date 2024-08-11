package org.ef3d0c3e.sheepwars.kits;

import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.player.CPlayer;

public abstract class Kit {
    /**
     * Gets kit's name (internal)
     * @return Kit's name
     */
    public abstract @NonNull String getName();

    /**
     * Gets kit's localized display name
     * @param cp Player
     * @return Display name
     */
    public abstract @NonNull String getDisplayName(final @NonNull CPlayer cp);

    public abstract @NonNull ItemStack getIcon(final @NonNull CPlayer cp);

    /**
     * Create kit data for player
     * @param cp Player to create data for
     * @return Data
     */
    public abstract @NonNull KitData createData(final @NonNull CPlayer cp);

}
