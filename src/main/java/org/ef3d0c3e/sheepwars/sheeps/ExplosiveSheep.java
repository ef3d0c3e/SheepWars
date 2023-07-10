package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

public class ExplosiveSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.RED_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#FF0000>Explosif"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.RED;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Explosif"));
		name.setStyle(Style.EMPTY.withColor(0xFF0000));
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
		final World world = (World)level().getWorld();
		world.spawnParticle(Particle.SMOKE_NORMAL, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
		world.spawnParticle(Particle.SMOKE_NORMAL, getX(), getY(), getZ(), 3, 0.2, 0.2, 0.2, 0.0);
	}

	int grounded = 0;
	int fuse = 0;

	public ExplosiveSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
	}

	public void ctick()
	{
		if (onGround())
			++grounded;

		if (grounded >= 5)
		{
			++fuse;
			if ((fuse / 8) % 2 == 0)
				setColor(DyeColor.WHITE);
			else
				setColor(getColor());

			if (fuse == 40)
			{
				remove(RemovalReason.DISCARDED);
				level().explode(this, getX(), getY(), getZ(), 6, false, Level.ExplosionInteraction.BLOCK);
			}
		}
	}
}
