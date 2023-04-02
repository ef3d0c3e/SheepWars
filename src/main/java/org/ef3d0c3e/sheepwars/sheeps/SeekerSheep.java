package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftLivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.function.Predicate;

public class SeekerSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.GREEN_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#32775B>Tête-Chercheuse"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.GREEN;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Tête-Chercheuse"));
		name.setStyle(Style.EMPTY.withColor(0x32775B));
		return name;
	}

	static public ItemStack getItem()
	{
		return ITEM;
	}

	public ItemStack getDrop()
	{
		return getItem();
	}

	protected void spawnParticles(final int time)
	{
		final World world = (World)level.getWorld();
		world.spawnParticle(Particle.SNEEZE, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int grounded = 0;
	int fuse = 0;
	CPlayer target = null;

	public SeekerSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);

		this.moveControl = new MoveControl(this);
		this.lookControl = new LookControl(this);
		this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, -1.0F);
		this.setPathfindingMalus(BlockPathTypes.WATER_BORDER, 16.0F);
	}

	protected void registerGoals()
	{
		this.goalSelector.addGoal(0, new SeekerGoal(this));
	}

	@Override
	public LivingEntity getTarget()
	{
		if (target == null)
			return null;
		return ((CraftLivingEntity)target.getHandle()).getHandle();
	}

	public void ctick()
	{
		if (isOnGround())
			++grounded;

		if (grounded >= 5)
		{
			++fuse;
			if ((fuse / 8) % 2 == 0 && fuse < 40)
				setColor(DyeColor.WHITE);
			else
				setColor(getColor());

			if (fuse >= 40 && fuse % 40 == 0)
			{
				final Location loc = new Location((World)level.getWorld(), getX(), getY(), getZ());
				target = null;
				CPlayer.forEach(cp ->
				{
					if (!cp.isAlive() || !cp.isOnline() || cp.getTeam() == owner.getTeam())
						return;
					if (target == null)
						target = cp;
					else if (loc.distanceSquared(cp.getHandle().getLocation()) <= loc.distanceSquared(target.getHandle().getLocation()))
						target = cp;
				});
			}
		}
	}

	public static class SeekerGoal extends Goal
	{
		private final SeekerSheep mob;
		private final Predicate<Mob> followPredicate;
		@Nullable
		private final double speedModifier;
		private final PathNavigation navigation;
		private int timeToRecalcPath;
		private final float stopDistance;
		private float oldWaterCost;

		public SeekerGoal(SeekerSheep sheep)
		{
			this.mob = sheep;
			this.followPredicate = (var1x) ->
			{
				return var1x != null && sheep.getClass() != var1x.getClass();
			};
			this.speedModifier = 1.3;
			this.navigation = sheep.getNavigation();
			this.stopDistance = 0.f;
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
			if (!(sheep.getNavigation() instanceof GroundPathNavigation) && !(sheep.getNavigation() instanceof FlyingPathNavigation))
				throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
		}

		public boolean canUse()
		{
			return mob.navigation.isDone();
		}

		public boolean canContinueToUse()
		{
			return mob.getTarget() != null &&
				!this.navigation.isDone() &&
				this.mob.distanceToSqr(mob.getTarget()) > (double)(this.stopDistance * this.stopDistance);
		}

		public void start()
		{
			this.timeToRecalcPath = 0;
			this.oldWaterCost = this.mob.getPathfindingMalus(BlockPathTypes.WATER);
			this.mob.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);
		}

		public void stop()
		{
			this.navigation.stop();
			this.mob.setPathfindingMalus(BlockPathTypes.WATER, this.oldWaterCost);
		}

		public void tick()
		{
			final LivingEntity followingMob = mob.getTarget();
			if (followingMob == null)
				return;

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

			if (distSq - 2.0 <= (double)(this.stopDistance * this.stopDistance))
			{
				mob.remove(RemovalReason.DISCARDED);
				mob.level.explode(mob, mob.getX(), mob.getY(), mob.getZ(), 5.f, false, Explosion.BlockInteraction.DESTROY);
			}
		}
	}

	public static class Events implements Listener
	{
		/**
		 * Reduce explosion damage from seekers
		 */
		@EventHandler
		public void onSeekerDamage(final EntityDamageByEntityEvent ev)
		{
			if (!(((CraftEntity)ev.getDamager()).getHandle() instanceof SeekerSheep)
				|| ev.getCause() != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
				return;
			ev.setDamage(ev.getDamage() * 0.65);
		}
	}
}
