package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

public class BoardingSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.WHITE_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#F0F0F0>Abordage"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.WHITE;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Abordage"));
		name.setStyle(Style.EMPTY.withColor(0xF0F0F0));
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
		world.spawnParticle(Particle.END_ROD, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int grounded = 0;
	int flyTime = 0;

	public BoardingSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
	}

	public void launch(final Vector direction, final double strength, final int duration)
	{
		super.launch(direction, strength, duration);

		((CraftPlayer) owner.getHandle()).getHandle().startRiding(this, true);

	}

	public void ctick()
	{
		if (isOnGround())
			++grounded;

		if (grounded >= 5 || flyTime >= 120)
		{
			this.remove(RemovalReason.DISCARDED);
		}

		++flyTime;
	}
}
