package org.ef3d0c3e.sheepwars.sheeps.ai;

import org.ef3d0c3e.sheepwars.versions.AutoWrapper;

public abstract class BaseGoal {
    @AutoWrapper(name = "Goal")
    protected static GoalVersionWrapper WRAPPER;

    /**
     * Gets the NMS handle for this goal
     * @return The goal's NMS handle
     */
    public abstract Object getHandle();
}
