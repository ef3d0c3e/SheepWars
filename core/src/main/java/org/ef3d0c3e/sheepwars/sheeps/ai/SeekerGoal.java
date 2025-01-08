package org.ef3d0c3e.sheepwars.sheeps.ai;

import lombok.Getter;
import lombok.NonNull;
import org.ef3d0c3e.sheepwars.sheeps.sheep.SeekerSheep;

public class SeekerGoal extends BaseGoal {
    /**
     * The Goal's NMS handle
     */
    @Getter
    private final @NonNull Object handle;

    public SeekerGoal(final @NonNull SeekerSheep handle) {
        this.handle = WRAPPER.newSeekerGoal(handle);
    }
}
