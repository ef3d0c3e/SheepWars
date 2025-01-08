package org.ef3d0c3e.sheepwars.v1_21_R0;

import lombok.NonNull;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.PathType;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.ef3d0c3e.sheepwars.sheeps.ai.GoalVersionWrapper;
import org.ef3d0c3e.sheepwars.sheeps.ai.SeekerGoal;
import org.ef3d0c3e.sheepwars.sheeps.sheep.SeekerSheep;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class Goal implements GoalVersionWrapper {
    /**
     * Custom Goal for the seeker
     */
    public static class SeekerGoal extends net.minecraft.world.entity.ai.goal.Goal {
        Mob entity;

        private final SeekerSheep base;
        private final Sheep.CustomSheep mob;

        private final Predicate<Mob> followPredicate;
        @Nullable
        private final double speedModifier;
        private final PathNavigation navigation;
        private int timeToRecalcPath;
        private final float stopDistance;
        private float oldWaterCost;

        public SeekerGoal(SeekerSheep base)
        {
            this.base = base;
            this.mob = (Sheep.CustomSheep) base.getHandle();
            this.followPredicate = (var1x) ->
            {
                return var1x != null && mob.getClass() != var1x.getClass();
            };
            this.speedModifier = 1.3;
            this.navigation = mob.getNavigation();
            this.stopDistance = 0.f;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
            if (!(mob.getNavigation() instanceof GroundPathNavigation) && !(mob.getNavigation() instanceof FlyingPathNavigation))
                throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }

        public boolean canUse()
        {
            return mob.getNavigation().isDone();
        }

        public boolean canContinueToUse()
        {
            final var target = base.getTarget();

            return target != null &&
                    !this.navigation.isDone() &&
                    this.mob.distanceToSqr(((CraftPlayer)target.getHandle()).getHandle()) > (double)(this.stopDistance * this.stopDistance);
        }

        public void start()
        {
            this.timeToRecalcPath = 0;
            this.oldWaterCost = this.mob.getPathfindingMalus(PathType.WATER);
            this.mob.setPathfindingMalus(PathType.WATER, 0.0F);
        }

        public void stop()
        {
            this.navigation.stop();
            this.mob.setPathfindingMalus(PathType.WATER, this.oldWaterCost);
        }

        public void tick()
        {
            final var target = base.getTarget();
            if (target == null)
                return;
            final LivingEntity followingMob = ((CraftPlayer)target.getHandle()).getHandle();

            this.mob.getLookControl().setLookAt(followingMob, 10.0F, (float)this.mob.getMaxHeadXRot());
            if (--this.timeToRecalcPath > 0)
                return;

            this.timeToRecalcPath = this.adjustedTickDelay(10);
            double var0 = this.mob.getX() - followingMob.getX();
            double var2 = this.mob.getY() - followingMob.getY();
            double var4 = this.mob.getZ() - followingMob.getZ();
            double distSq = var0 * var0 + var2 * var2 + var4 * var4;
            if (!(distSq <= (double)(this.stopDistance * this.stopDistance)))
                this.navigation.moveTo(followingMob, this.speedModifier);
        }
    }

    @Override
    public Object newSeekerGoal(@NonNull SeekerSheep sheep) {
        return new SeekerGoal(sheep);
    }
}
