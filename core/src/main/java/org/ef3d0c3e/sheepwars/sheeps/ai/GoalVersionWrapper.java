package org.ef3d0c3e.sheepwars.sheeps.ai;

import lombok.NonNull;
import org.ef3d0c3e.sheepwars.sheeps.sheep.SeekerSheep;

public interface GoalVersionWrapper {
    Object newSeekerGoal(final @NonNull SeekerSheep sheep);
}
