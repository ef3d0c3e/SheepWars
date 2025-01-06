package org.ef3d0c3e.sheepwars.sheeps;

import lombok.NonNull;
import org.bukkit.DyeColor;
import org.ef3d0c3e.sheepwars.player.CPlayer;

public abstract class FuseSheep extends BaseSheep{
    private int grounded = 0;
    private int fuse = 0;
    private final int fuseTime;

    public FuseSheep(final @NonNull CPlayer owner, int fuseTime) {
        super(owner);
        this.fuseTime = fuseTime;
    }

    /**
     * Method called when fuse timer reaches it's end
     */
    public abstract void onFuseEnd();

    @Override
    public void tick() {
        if (onGround())
            ++grounded;

        if (grounded >= 5)
        {
            ++fuse;
            if ((fuse / 8) % 2 == 0)
                setColor(DyeColor.WHITE);
            else
                setColor(getColor());

            if (fuse == fuseTime)
            {
                remove();
                onFuseEnd();
            }
        }
    }
}
