package org.ef3d0c3e.sheepwars.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.ef3d0c3e.sheepwars.player.skin.Skin;

/**
 * Manages cosmetics for the player
 */
public class CosmeticManager {
    private CPlayer cp;

    @Getter @Setter
    private Skin originalSkin = null;
    @Getter
    private Skin currentSkin = null;

    protected CosmeticManager(final @NonNull CPlayer cp)
    {
        this.cp = cp;
    }
}
