package org.ef3d0c3e.sheepwars.sheeps;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

public class SlimeSheepSlime extends Mob
{
	private static final EntityDataAccessor<Integer> ID_SIZE = SynchedEntityData.defineId(Slime.class, EntityDataSerializers.INT);

	private CPlayer owner;
	private SlimeSheep parent;

	public SlimeSheepSlime(final SlimeSheep parent, final Location loc, final CPlayer owner)
	{
		super(EntityType.SLIME, ((CraftWorld)loc.getWorld()).getHandle());
		this.absMoveTo(loc.getX(), loc.getY(), loc.getZ());

		this.owner = owner;
		this.parent = parent;

		this.setInvulnerable(true);
		/*final MutableComponent name = parent.getSheepName();

		setCustomName(name.append(
			MutableComponent
				.create(new LiteralContents(" |" + owner.getTeam().getName() + "|"))
				.withStyle(Style.EMPTY
					.withColor(owner.getTeam().getColorValue())
					.withBold(true))));
		 */
	}

	public void spawn()
	{
		this.level().getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(ID_SIZE, 0);
	}

	@VisibleForTesting
	public void setSize(int i) {
		this.entityData.set(ID_SIZE, i);
		this.reapplyPosition();
		this.refreshDimensions();
	}

	public int getSize() {
		return (Integer)this.entityData.get(ID_SIZE);
	}

	public void addAdditionalSaveData(CompoundTag nbttagcompound) {
		super.addAdditionalSaveData(nbttagcompound);
		nbttagcompound.putInt("Size", this.getSize() - 1);
		nbttagcompound.putBoolean("wasOnGround", false);
	}

	public void readAdditionalSaveData(CompoundTag nbttagcompound) {
		this.setSize(nbttagcompound.getInt("Size") + 1);
		super.readAdditionalSaveData(nbttagcompound);
	}

	public void refreshDimensions() {
		double d0 = this.getX();
		double d1 = this.getY();
		double d2 = this.getZ();
		super.refreshDimensions();
		this.setPos(d0, d1, d2);
	}

	public void onSyncedDataUpdated(EntityDataAccessor<?> datawatcherobject) {
		if (ID_SIZE.equals(datawatcherobject)) {
			this.refreshDimensions();
			this.setYRot(this.yHeadRot);
			this.yBodyRot = this.yHeadRot;
			if (this.isInWater() && this.random.nextInt(20) == 0) {
				this.doWaterSplashEffect();
			}
		}

		super.onSyncedDataUpdated(datawatcherobject);
	}

	public EntityType<? extends Slime> getType() {
		return (EntityType<? extends Slime>) super.getType();
	}

	protected float getStandingEyeHeight(Pose entitypose, EntityDimensions entitysize) {
		return 0.625F * entitysize.height;
	}

	public int getMaxHeadXRot() {
		return 0;
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldaccess, DifficultyInstance difficultydamagescaler, MobSpawnType enummobspawn, @Nullable SpawnGroupData groupdataentity, @Nullable CompoundTag nbttagcompound) {
		this.setSize(this.getSize());
		return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
	}

	public EntityDimensions getDimensions(Pose entitypose) {
		return super.getDimensions(entitypose).scale(0.255F * (float)this.getSize());
	}

	int ticks = 0;
	CPlayer target = null;

	public void ctick()
	{
		if (!parent.isAlive())
			this.remove(RemovalReason.DISCARDED);

		if (ticks % 8 == 0) // Shoot
		{
			final Location loc = new Location(level().getWorld(), getX(), getY()+0.5, getZ());
			// Get new target
			if (target == null || !target.isOnline() || !target.isAlive()
				|| target.getHandle().getLocation().distanceSquared(loc) > 7.*7.)
			{
				AtomicReference<Double> closest = new AtomicReference<>(Double.MAX_VALUE);
				target = null;
				CPlayer.forEach(cp -> {
					if (!cp.isOnline() || !cp.isAlive() || cp.getTeam() == null || cp.getTeam() == owner.getTeam())
						return;

					final double distSq = cp.getHandle().getLocation().distanceSquared(loc);
					if (distSq > 7.*7.)
						return;

					if (distSq < closest.get()) // Select as new target
					{
						closest.set(distSq);
						target = cp;
					}
				});
			}

			// Shoot
			if (target != null)
			{
				Vector dir = target.getHandle().getLocation().clone().add(0, 1.5, 0).subtract(loc).toVector().normalize();
				Snowball s = (Snowball)level().getWorld().spawnEntity(loc.clone().add(dir.clone().multiply(0.2)), org.bukkit.entity.EntityType.SNOWBALL);
				s.setItem(new ItemStack(Material.SLIME_BALL));
				s.setVelocity(dir.multiply(1.0));

				final World w = level().getWorld();
				w.playSound(new Location(w, getX(), getY(), getZ()), Sound.ENTITY_SLIME_JUMP, 12.f, 1.f);
			}
		}

		++ticks;
	}
}
