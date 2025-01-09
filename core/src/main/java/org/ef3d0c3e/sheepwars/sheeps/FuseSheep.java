package org.ef3d0c3e.sheepwars.sheeps;

import lombok.NonNull;
import org.bukkit.DyeColor;
import org.ef3d0c3e.sheepwars.player.CPlayer;

public abstract class FuseSheep extends BaseSheep{
    private int grounded = 0;
    private int fuse = 0;
    private final int fuseTime;

    /**
     * Creates a fuse sheep that flashes between it's color and white
     * @param owner Owner of the sheep
     * @param fuseTime Duration of the fuse, after which {@ref onFuseEnd} is called
     */
    public FuseSheep(final @NonNull CPlayer owner, final int fuseTime) {
        super(owner);
        this.fuseTime = fuseTime;
    }

    /**
     * Method called when fusing
     * @param fuseTime
     */
    public void onFuse(final int fuseTime) {}

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
                setColor(getDyeColor());

            onFuse(fuse);
            if (fuse == fuseTime)
            {
                remove();
                onFuseEnd();
            }
        }
    }
}
