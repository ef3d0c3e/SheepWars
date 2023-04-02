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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Util;

public class HealerSheep extends BaseSheep
{
	static ItemStack ITEM;
	static
	{
		ITEM = new ItemStack(Material.PINK_WOOL);
		ItemMeta meta = ITEM.getItemMeta();
		meta.setDisplayName(Util.getColored("<#ED9ED6>Soigneur"));
		ITEM.setItemMeta(meta);
	}

	public DyeColor getColor()
	{
		return DyeColor.PINK;
	}

	public MutableComponent getSheepName()
	{
		MutableComponent name = MutableComponent.create(new LiteralContents("Soigneur"));
		name.setStyle(Style.EMPTY.withColor(0xED9ED6));
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
		world.spawnParticle(Particle.HEART, getX(), getY(), getZ(), 1, 0.0, 0.0, 0.0, 0.0);
	}

	int grounded = 0;
	int lifeTime = 0;

	public HealerSheep(final Location loc, final CPlayer owner)
	{
		super(loc, owner);
	}

	public void ctick()
	{
		if (isOnGround())
			++grounded;

		if (grounded >= 5)
		{
			++lifeTime;

			if (grounded % 20 == 0)
			{
				// Particles
				final World world = (World)level.getWorld();
				final Location center = new Location(world, getX(), getY()+0.5, getZ());
				Util.runInCircle(center, new Vector(0, 1, 0), 5.5, 32, (loc, t, i) ->
				{
					world.spawnParticle(Particle.HEART, loc, 1, 0.0, 0.0, 0.0, 0.0);
				});

				// Effect
				CPlayer.forEach(cp ->
				{
					if (!cp.isAlive() || !cp.isOnline() || cp.getTeam() != owner.getTeam())
						return;
					if (cp.getHandle().getLocation().distance(center) >= 6)
						return;

					final PotionEffect effect = cp.getHandle().getPotionEffect(PotionEffectType.REGENERATION);
					if (effect != null && effect.getDuration() > 10) // Another healer
						cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30, 2));
					else
						cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 30, 1));
				});
			}

			if (lifeTime > 120)
			{
				if ((lifeTime / 8) % 2 == 0)
					setColor(DyeColor.WHITE);
				else
					setColor(getColor());

				if (lifeTime == 160)
					this.remove(RemovalReason.DISCARDED);
			}
		}
	}
}
