package org.ef3d0c3e.sheepwars.kits;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.Game;
import org.ef3d0c3e.sheepwars.items.ItemBase;

public class ItemWand extends ItemBase
{
	private int cooldown = 0;
	private int lastUsed = 0;

	public ItemWand()
	{
		super();
	}

	private void setCooldown(final Player p, final ItemStack item, int amt)
	{
		lastUsed = Game.getTimer().getTicks();
		cooldown = amt;
		p.setCooldown(item.getType(), cooldown);
	}

	private static void drawLine(final Location start, final Vector direction, final double distance)
	{
		final World w = start.getWorld();
		for (int i = 1; i <= distance; ++i)
		{
			w.spawnParticle(Particle.BUBBLE_POP,
				start.clone().add(direction.normalize().multiply(i)),
				1, 0, 0, 0
			);
		}
	}

	@Override
	protected boolean onInteract(Player p, ItemStack item, Action action, EquipmentSlot hand, Block clicked, BlockFace clickedFace)
	{
		if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK)
			return true;

		if (Game.getTimer().getTicks() - lastUsed <= cooldown)
			return true;

		final RayTraceResult trace = p.getWorld().rayTrace(
			p.getEyeLocation(), p.getLocation().getDirection(),
			128.0, FluidCollisionMode.NEVER,
			true, 0.1,
			(ent) -> ent instanceof LivingEntity && ent != p
		);


		// Make block levitate
		if (trace != null && trace.getHitBlock() != null)
		{
			final Block b = trace.getHitBlock();

			final FallingBlock fb = p.getWorld().spawnFallingBlock(b.getLocation(), b.getBlockData());
			fb.setVelocity(new Vector(Math.random()*0.1 - 0.05, 0.5, Math.random()*0.1 - 0.05));
			fb.setDropItem(false);
			b.setType(Material.AIR);
			setCooldown(p, item, 10);
		}
		else if (trace != null && trace.getHitEntity() != null) // Make entity levitate
		{
			final LivingEntity ent = (LivingEntity)trace.getHitEntity();
			ent.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 0, false, true, true));
			setCooldown(p, item, 80);
		}

		// Particle & cooldown
		if (trace == null)
		{
			drawLine(p.getEyeLocation(), p.getLocation().getDirection(), 128.0);
			setCooldown(p, item, 10);
		}
		else
			drawLine(p.getEyeLocation(), p.getLocation().getDirection(), trace.getHitPosition().distance(p.getEyeLocation().toVector()));

		// Sound
		p.getWorld().playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_SHOOT, 12.f, 1.f);

		return true;
	}

	@Override
	protected boolean onDrop(Player p, ItemStack item)
	{
		return true;
	}
}
