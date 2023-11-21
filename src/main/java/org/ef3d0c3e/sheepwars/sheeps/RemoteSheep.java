package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Saddleable;
import net.minecraft.world.item.DyeColor;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

import javax.annotation.Nullable;

public class RemoteSheep extends BaseSheep implements PlayerRideableJumping, Saddleable
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.LIME_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#4AE84A>Télécommandé"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.LIME;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Télécommandé"));
		name.setStyle(Style.EMPTY.withColor(0x4AE84A));
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
		final World world = level().getWorld();
		world.spawnParticle(Particle.SOUL, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
		world.spawnParticle(Particle.SOUL, getX(), getY(), getZ(), 3, 0.2, 0.2, 0.2, 0.0);
	}

	int ticks = 0;
	Location ownerPos;


	public RemoteSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);

		ownerPos = owner.getHandle().getLocation();
		((CraftPlayer) owner.getHandle()).getHandle().startRiding(this, true);
	}

	// TODO: Bossbar timer

	public void ctick()
	{
		if (ticks > 200)
			remove(RemovalReason.DISCARDED);
		ticks++;
	}

	// PlayerRideableJumping
	@Override
	public void onPlayerJump(int i)
	{
		launch(new Vector(0, 1, 0), 1.0, 1);
	}

	@Override
	public boolean canJump()
	{
		return true;
	}

	@Override
	public void handleStartJump(int i)
	{

	}

	@Override
	public void handleStopJump()
	{

	}

	@Override
	public int getJumpCooldown()
	{
		return 1;
	}

	@Override
	public boolean isSaddleable()
	{
		return true;
	}

	@Override
	public void equipSaddle(@Nullable SoundSource soundSource)
	{

	}

	@Override
	public SoundEvent getSaddleSoundEvent()
	{
		return Saddleable.super.getSaddleSoundEvent();
	}

	@Override
	public boolean isSaddled()
	{
		return true;
	}

	@Override
	public boolean alwaysAccepts()
	{
		return true;
	}
}
