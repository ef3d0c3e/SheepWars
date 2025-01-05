package org.ef3d0c3e.sheepwars.kits;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
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

    /**
     * Gets the kit's display component
     * @param cp The player to get the component for
     * @return The kit's display component localized for the player
     */
    public abstract @NonNull Component getColoredName(final @NonNull CPlayer cp);

    /**
     * Gets the kit's item icon
     * @param cp The player to get the icon for
     * @return The item icon
     */
    public abstract @NonNull ItemStack getIcon(final @NonNull CPlayer cp);

    /**
     * Create kit data for player
     * @param cp Player to create data for
     * @return Data
     */
    public abstract @NonNull KitData createData(final @NonNull CPlayer cp);

}
