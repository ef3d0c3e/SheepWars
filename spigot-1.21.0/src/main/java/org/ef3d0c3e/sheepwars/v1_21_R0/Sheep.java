package org.ef3d0c3e.sheepwars.v1_21_R0;

import lombok.NonNull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftEntity;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.SheepVersionWrapper;

import javax.annotation.Nullable;


public class Sheep implements SheepVersionWrapper {
    public static class CustomSheep extends net.minecraft.world.entity.animal.Sheep
    {
        private static final EntityDataAccessor<Byte> DATA_WOOL_ID;

        static {
            DATA_WOOL_ID = SynchedEntityData.defineId(net.minecraft.world.entity.animal.Sheep.class, EntityDataSerializers.BYTE);
        }


        private @NonNull BaseSheep base;
        private int launch = 0;
        private Vec3 launchDir = null;


        public CustomSheep(final @NonNull BaseSheep base, final @NonNull Location loc, final boolean baby)
        {
            super(EntityType.SHEEP, ((CraftWorld)loc.getWorld()).getHandle());
            this.setBaby(baby);
            this.base = base;
            this.absMoveTo(loc.getX(), loc.getY(), loc.getZ());
        }

        public void spawn()
        {
            this.level().getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
        }

        public void launch(final Vector direction, double strength, int duration)
        {
            final Vector pdir = direction.normalize().multiply(strength);

            launch = duration;
            launchDir = new Vec3(pdir.getX(), pdir.getY(), pdir.getZ());
        }

        @Nullable
        public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldaccess, DifficultyInstance difficultydamagescaler, MobSpawnType enummobspawn, @Nullable SpawnGroupData groupdataentity) {
            final var data = super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity);
            this.setColor(this.getColor());
            return data;
        }

        @Override
        public DyeColor getColor() {
            return DyeColor.byId((byte)base.getDyeColor().ordinal());
        }


        @Override
        public void addAdditionalSaveData(CompoundTag nbttagcompound) {
            super.addAdditionalSaveData(nbttagcompound);
            nbttagcompound.putByte("Color", (byte)this.getColor().getId());
        }

        @Override
        protected void defineSynchedData(SynchedEntityData.Builder datawatcher_a) {
            super.defineSynchedData(datawatcher_a);
            datawatcher_a.define(DATA_WOOL_ID, (byte)0);
        }

        @Override
        public void aiStep()
        {
            if (getY() <= base.getDispawnHeight()) {
                this.remove(RemovalReason.DISCARDED);
                return;
            }
            super.aiStep();
            if (launch > 0)
            {
                this.setDeltaMovement(launchDir);
                this.setYRot(-(float)(Math.atan2(launchDir.x, launchDir.z) * 57.2957763671875D));
                base.launchTrail(launch);

                if (onGround())
                    launch = (launch * 17)/20;
                launchDir = launchDir.add(0.0, -0.040, 0.0);
                --launch;
            }

            base.setLifetime(base.getLifetime() + 1);
            base.tick();
        }

        @Override
        protected void registerGoals()
        {
        }

        @Override
        protected void customServerAiStep()
        {
        }

        @Override
        public void remove(RemovalReason reason)
        {
            base.onRemove();
            super.remove(reason);
        }

        @Override
        public void remove(RemovalReason reason, EntityRemoveEvent.Cause cause)
        {
            base.onRemove();
            super.remove(reason, cause);
        }

        @Override
        public ResourceKey<LootTable> getDefaultLootTable() {
            return BuiltInLootTables.EMPTY;
        }

        @Override
        public void shear(SoundSource src)
        {

        }

        @Override
        public int getBaseExperienceReward() { return 0; }
    }

    @Override
    public @Nullable BaseSheep getInstance(final @NonNull org.bukkit.entity.Entity entity) {
        if (!(((CraftEntity) entity).getHandle() instanceof CustomSheep handle))
            return null;

        return handle.base;
    }

    @Override
    public void spawn(final @NonNull BaseSheep sheep, final @NonNull Location location, final boolean baby) {
        final var handle = new CustomSheep(sheep, location, baby);
        sheep.setHandle(handle);
        sheep.setBukkitHandle((org.bukkit.entity.LivingEntity) handle.getBukkitEntity());
        handle.spawn();
    }

    @Override
    public void remove(final @NonNull BaseSheep sheep) {
        final var handle = (CustomSheep)sheep.getHandle();
        handle.remove(Entity.RemovalReason.DISCARDED);
    }

    @Override
    public void launch(final @NonNull BaseSheep sheep, final Vector direction, final double strength, final int duration) {
        ((CustomSheep)sheep.getHandle()).launch(direction, strength, duration);
    }

    @Override
    public @NonNull Location getLocation(final @NonNull BaseSheep sheep) {
        final var handle = (CustomSheep)sheep.getHandle();
        return new Location(handle.level().getWorld(), handle.getX(), handle.getY(), handle.getZ());
    }

    @Override
    public boolean onGround(final @NonNull BaseSheep sheep) {
        final var handle = (CustomSheep)sheep.getHandle();
        return handle.onGround();
    }

    @Override
    public void setColor(final @NonNull BaseSheep sheep, final @NonNull org.bukkit.DyeColor color) {
        final var handle = (CustomSheep)sheep.getHandle();
        handle.setColor(DyeColor.byId(color.ordinal()));
    }

    @Override
    public int getNetworkId(final @NonNull BaseSheep sheep)
    {
        final var handle = (CustomSheep)sheep.getHandle();
        return handle.getId();
    }
}
