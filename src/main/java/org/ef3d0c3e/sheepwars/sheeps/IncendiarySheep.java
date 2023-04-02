package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Explosion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

public class IncendiarySheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.ORANGE_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#FF7E1F>Incendiaire"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.ORANGE;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Incendiaire"));
		name.setStyle(Style.EMPTY.withColor(0xFF7E1F));
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
		world.spawnParticle(Particle.FLAME, getX(), getY()+0.4, getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int grounded = 0;
	int fuse = 0;

	public IncendiarySheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
	}

	public void ctick()
	{
		if (isOnGround())
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
				final World world = (World)level.getWorld();
				this.remove(RemovalReason.DISCARDED);
				this.level.explode(this, getX(), getY(), getZ(), 5, true, Explosion.BlockInteraction.DESTROY);

				for (int i = 0; i < 20; ++i)
				{
					FallingBlock fb = world.spawnFallingBlock(new Location(world, getX(), getY(), getZ()), Material.FIRE.createBlockData());
					fb.setDropItem(false);
					fb.setVelocity(new Vector(
						(Math.random() - Math.random()) / 1.5,
						Math.random(),
						(Math.random() - Math.random()) / 1.5));
				}
			}
		}
	}
}
