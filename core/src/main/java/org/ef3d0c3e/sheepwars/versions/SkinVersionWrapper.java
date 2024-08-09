package org.ef3d0c3e.sheepwars.versions;

import com.mojang.authlib.properties.PropertyMap;
import lombok.NonNull;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.player.skin.Skin;

public interface SkinVersionWrapper
{
    /**
     * Updates player skin
     * @param cp Player
     */
    void updateSkin(final @NonNull CPlayer cp);

    /**
     * Gets the skin of a player
     * @param cp Player to get the skin from
     * @return Player's skin
     */
    @NonNull Skin fromPlayer(final @NonNull CPlayer cp);

    /**
     * Gets the player's PropertyMap
     * @param cp Player to get the PropertyMap from
     * @return Player's PropertyMap
     */
    @NonNull PropertyMap getProperties(final @NonNull CPlayer cp);
}

