package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;

public abstract class BaseSheep extends Animal
{
	private static final EntityDataAccessor<Byte> DATA_WOOL_ID = SynchedEntityData.defineId(BaseSheep.class, EntityDataSerializers.BYTE);
	protected CPlayer owner;

	public BaseSheep(final Location loc, final CPlayer owner)
	{
		super(EntityType.SHEEP, ((CraftWorld)loc.getWorld()).getHandle());
		this.absMoveTo(loc.getX(), loc.getY(), loc.getZ());

		this.owner = owner;
		final MutableComponent name = getSheepName();

		setCustomName(name.append(
			MutableComponent
				.create(new LiteralContents(" |" + owner.getTeam().getName() + "|"))
				.withStyle(Style.EMPTY
					.withColor(owner.getTeam().getColorValue())
					.withBold(true))));
	}

	public void spawn()
	{
		this.level().getWorld().addEntity(this, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
	}

	int launch = 0;
	Vec3 launchDir = null;

	public void launch(final Vector direction, double strength, int duration)
	{
		final Vector pdir = direction.normalize().multiply(strength);

		launch = duration;
		launchDir = new Vec3(pdir.getX(), pdir.getY(), pdir.getZ());
	}

	protected void registerGoals()
	{
	}

	protected void customServerAiStep()
	{
		super.customServerAiStep();
	}

	public static AttributeSupplier.Builder createAttributes()
	{
		return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D);
	}

	protected void defineSynchedData()
	{
		super.defineSynchedData();
		this.entityData.define(DATA_WOOL_ID, (byte)0);
	}

	public ResourceLocation getDefaultLootTable()
	{
		return this.getType().getDefaultLootTable();
	}

	public void handleEntityEvent(byte b0)
	{
	}

	public InteractionResult mobInteract(Player entityhuman, InteractionHand enumhand)
	{
		return InteractionResult.SUCCESS;
	}

	public void addAdditionalSaveData(CompoundTag nbttagcompound)
	{
		super.addAdditionalSaveData(nbttagcompound);
		nbttagcompound.putByte("Color", (byte)this.getColor().getId());
	}

	public void readAdditionalSaveData(CompoundTag nbttagcompound)
	{
		super.readAdditionalSaveData(nbttagcompound);
	}

	protected SoundEvent getAmbientSound()
	{
		return SoundEvents.SHEEP_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource damagesource)
	{
		return SoundEvents.SHEEP_HURT;
	}

	protected SoundEvent getDeathSound()
	{
		return SoundEvents.SHEEP_DEATH;
	}

	protected void playStepSound(BlockPos blockposition, BlockState iblockdata)
	{
		this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
	}

	public Sheep getBreedOffspring(ServerLevel worldserver, AgeableMob entityageable)
	{
		return null;
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldaccess, DifficultyInstance difficultydamagescaler, MobSpawnType enummobspawn, @Nullable SpawnGroupData groupdataentity, @Nullable CompoundTag nbttagcompound)
	{
		setColor(getColor());
		return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
	}

	protected float getStandingEyeHeight(Pose entitypose, EntityDimensions entitysize)
	{
		return 0.95F * entitysize.height;
	}

	protected void setColor(DyeColor color)
	{
		byte b0 = (Byte)this.entityData.get(DATA_WOOL_ID);
		this.entityData.set(DATA_WOOL_ID, (byte)(b0 & 240 | color.getId() & 15));
	}

	public int getAmbientSoundInterval()
	{
		return Integer.MAX_VALUE;
	}

	abstract public DyeColor getColor();

	abstract public MutableComponent getSheepName();

	public CPlayer getOwner()
	{
		return owner;
	}

	static public ItemStack getItem()
	{
		return null;
	}

	abstract public ItemStack getDrop();

	abstract protected void spawnParticles(final int time);

	public void aiStep()
	{
		if (getY() <= Sheeps.getDispawnLayer())
			this.remove(RemovalReason.DISCARDED);
		else
		{
			super.aiStep();
			if (launch > 0)
			{
				this.setDeltaMovement(launchDir);
				this.setYRot(-(float)(Math.atan2(launchDir.x, launchDir.z) * 57.2957763671875D));
				spawnParticles(launch);

				if (onGround())
					launch = (launch * 17)/20;
				launchDir = launchDir.add(0.0, -0.040, 0.0);
				--launch;
			}
		}
	}

	abstract public void ctick();
}
