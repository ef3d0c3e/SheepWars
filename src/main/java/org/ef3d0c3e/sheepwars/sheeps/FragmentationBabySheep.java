package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

public class FragmentationBabySheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.GRAY_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#373737>Fragmentation"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.GRAY;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Fragmentation"));
		name.setStyle(Style.EMPTY.withColor(0x373737));
		return name;
	}

	static public ItemStack getItem()
	{
		return ITEM;
	}

	public ItemStack getDrop()
	{
		return null;
	}

	protected void spawnParticles(final int time)
	{
		final World world = (World)level().getWorld();
		world.spawnParticle(Particle.EXPLOSION_NORMAL, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int lifeTime = 0;

	public FragmentationBabySheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
		setBaby(true);
	}

	public void ctick()
	{
		if (onGround())
		{
			this.remove(RemovalReason.DISCARDED);
			this.level().explode(this, getX(), getY(), getZ(), 3, false, Level.ExplosionInteraction.BLOCK);
		}
		else if (lifeTime == 160)
		{
			this.remove(RemovalReason.DISCARDED);
		}

		++lifeTime;
	}
}
