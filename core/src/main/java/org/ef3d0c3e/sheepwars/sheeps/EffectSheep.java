package org.ef3d0c3e.sheepwars.sheeps;

import lombok.NonNull;
import org.bukkit.DyeColor;
import org.ef3d0c3e.sheepwars.player.CPlayer;


/**
 * A base class for sheeps that apply effects when activated
 */
public abstract class EffectSheep extends BaseSheep {
    private int groundedTime = 0;
    private final int maxLifeTime;
    private final int maxGroundTime;

    public EffectSheep(final @NonNull CPlayer owner, int maxLifeTime, int maxGroundTime)
    {
        super(owner);
        this.maxLifeTime = maxLifeTime;
        this.maxGroundTime = maxGroundTime;
    }

    /**
     * Method called when the grounded time of the sheep exceeds it's maximum ground time
     */
    public void onGroundTimeEnd() {}

    /**
     * Method called while the sheep is grounded
     * @param groundTime The grounded time of the sheep
     */
    public abstract void onGrounded(final int groundTime);

    @Override
    public void tick()
    {
        if (onGround()) {
            ++groundedTime;

            if (groundedTime >= 5)
                onGrounded(groundedTime - 5);

            if (groundedTime - 5 > maxGroundTime)
            {
                onGroundTimeEnd();
                remove();
            }
        }

        if (getLifetime() > maxLifeTime)
            remove();
        else if (maxGroundTime - groundedTime <= 40) // Blink
        {
            if ((groundedTime / 8) % 2 == 0)
                setColor(DyeColor.WHITE);
            else
                setColor(getDyeColor());
        }
    }
}
