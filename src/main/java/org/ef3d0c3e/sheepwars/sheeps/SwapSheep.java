package org.ef3d0c3e.sheepwars.sheeps;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.world.item.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

public class SwapSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.MAGENTA_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#B888EC>Échange"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.MAGENTA;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Échange"));
		name.setStyle(Style.EMPTY.withColor(0xB888EC));
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
		world.spawnParticle(Particle.SOUL, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int grounded = 0;
	int fuse = 0;

	public SwapSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
	}

	public void ctick()
	{
		if (onGround())
			++grounded;

		if (grounded >= 5)
		{
			// Particles
			if (fuse % 10 == 0)
			{
				final World world = (World)level().getWorld();
				final Location center = new Location(world, getX(), getY()+0.5, getZ());
				Util.runInCircle(center, new Vector(0, 1, 0), 9.0, 24, (loc, t, i) ->
				{
					world.spawnParticle(Particle.SOUL, loc, 1, 0.0, 0.0, 0.0, 0.0);
				});
				Util.runInCircle(center, new Vector(0, 1, 0), 6.0, 12, (loc, t, i) ->
				{
					world.spawnParticle(Particle.SOUL, loc, 1, 0.0, 0.0, 0.0, 0.0);
				});
			}

			// Color
			if ((fuse / 8) % 2 == 0)
				setColor(DyeColor.WHITE);
			else
				setColor(getColor());

			// Effect
			if (fuse == 40)
			{
				final Location loc = new Location((World)level().getWorld(), getX(), getY(), getZ());
				this.remove(RemovalReason.DISCARDED);
				if (!owner.isAlive() || !owner.isOnline())
					return;

				final CPlayer[] target = {null};
				CPlayer.forEach(cp ->
				{
					if (!cp.isOnline() || !cp.isAlive() || cp.getTeam() == owner.getTeam())
						return;

					if (target[0] == null)
						target[0] = cp;
					else if (loc.distanceSquared(cp.getHandle().getLocation()) <= loc.distanceSquared(target[0].getHandle().getLocation()))
						target[0] = cp;
				});
				if (target[0] != null && target[0].getHandle().getLocation().distance(loc) <= 9)
				{
					final Location targetLoc = target[0].getHandle().getLocation();
					target[0].getHandle().teleport(owner.getHandle().getLocation());
					owner.getHandle().teleport(targetLoc);

					target[0].getCombatData().damageNow(owner);
				}
			}
			++fuse;
		}
	}
}
