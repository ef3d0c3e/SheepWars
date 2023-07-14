package org.ef3d0c3e.sheepwars;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.ef3d0c3e.sheepwars.sheeps.BoardingSheep;

import java.util.ArrayList;
import java.util.HashMap;

public class BonusWool
{
	public interface WoolEffect
	{
		void operation(final Team buffed);
	}

	private static Material[] COLORS = {
		Material.LIGHT_BLUE_WOOL, Material.GREEN_WOOL, Material.PURPLE_WOOL, Material.PINK_WOOL, Material.CYAN_WOOL, Material.YELLOW_WOOL, Material.WHITE_WOOL
	};

	private static WoolEffect[] EFFECTS = {
		// Bonus sheep arrows
		buffed -> buffed.forEach(cp ->
		{
			if (!cp.isOnline() || !cp.isAlive())
				return;

			cp.getHandle().getInventory().addItem(cp.getKit().getWoolRandomizer().getNextWool());

			cp.getHandle().sendMessage("§7Vous obtenez un mouton bonus!");
		}),
		// Nausea
		buffed -> {
			CPlayer.forEach(cp ->
			{
				if (!cp.isOnline() || !cp.isAlive() || cp.getTeam() == null || cp.getTeam() == buffed)
					return;

				cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 160, 0, false, true, true));
			});

			buffed.forEach(cp -> cp.getHandle().sendMessage("§7L'équipe ennemi obtient Nausée!"));
		},
		// Poison
		buffed -> {
			CPlayer.forEach(cp -> {
				if (!cp.isOnline() || !cp.isAlive() || cp.getTeam() == null || cp.getTeam() == buffed)
					return;

				cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 120, 0, false, true, true));
			});

			buffed.forEach(cp -> cp.getHandle().sendMessage("§7L'équipe ennemi obtient Poison!"));
		},
		// Regeneration
		buffed -> buffed.forEach(cp -> {
			if (!cp.isOnline() || !cp.isAlive())
				return;

			cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1, false, true, true));
			cp.getHandle().sendMessage("§7Vous obtenez Régénération!");
		}),
		// Resistance
		buffed -> buffed.forEach(cp -> {
			if (!cp.isOnline() || !cp.isAlive())
				return;

			cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0, false, true, true));
			cp.getHandle().sendMessage("§7Vous obtenez Résistance!");
		}),
		// Glowing
		buffed -> {
			CPlayer.forEach(cp -> {
				if (!cp.isOnline() || !cp.isAlive() || cp.getTeam() == null || cp.getTeam() == buffed)
					return;

				cp.getHandle().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0, false, true, true));
			});

			buffed.forEach(cp -> cp.getHandle().sendMessage("§7L'équipe ennemi brille!"));
		},
		// Get boarding
		buffed -> buffed.forEach(cp -> {
			if (!cp.isOnline() || !cp.isAlive())
				return;

			cp.getHandle().getInventory().addItem(BoardingSheep.getItem());
			cp.getHandle().sendMessage("§7Vous obtenez un Mouton Abordage!");
		})
	};


	public static HashMap<Block, BonusWool> BONUS_WOOLS = new HashMap<>();
	private Block block;
	private int color = 0;

	/**
	 * Constructor
	 * @param loc Location to create the wool at
	 */
	public BonusWool(final Location loc)
	{
		block = loc.getBlock();
		block.setType(COLORS[color]);

		BONUS_WOOLS.put(block, this);
	}

	public static class Events implements Listener
	{
		@EventHandler
		public void onBonusWoolHit(final ProjectileHitEvent ev)
		{
			if (!(ev.getEntity() instanceof Arrow) || ev.getHitBlock() == null)
				return;
			if (!(ev.getEntity().getShooter() instanceof Player))
				return;
			final BonusWool wool = BONUS_WOOLS.get(ev.getHitBlock());
			if (wool == null)
				return;

			// Remove wool
			BONUS_WOOLS.remove(ev.getHitBlock());
			ev.getHitBlock().setType(Material.AIR);

			// Remove arrow
			ev.getEntity().remove();

			final CPlayer shooter = CPlayer.getPlayer((Player)ev.getEntity().getShooter());
			EFFECTS[wool.color].operation(shooter.getTeam()); // Apply buff
		}

		@EventHandler
		public void onBlockDestroy(final BlockBreakEvent ev)
		{
			if (!BONUS_WOOLS.containsKey(ev.getBlock()))
				return;

			ev.setCancelled(true);
		}

		@EventHandler
		public void onBlockBurn(final BlockBurnEvent ev)
		{
			if (!BONUS_WOOLS.containsKey(ev.getBlock()))
				return;

			ev.setCancelled(true);
		}

		@EventHandler
		public void onBlockExplode(final BlockExplodeEvent ev)
		{
			if (!BONUS_WOOLS.containsKey(ev.getBlock()))
				return;

			ev.setCancelled(true);
		}

		public Events()
		{
			// Cycle colors & Spawn particles
			new BukkitRunnable()
			{
				int ticks = 0;
				@Override
				public void run()
				{
					for (final BonusWool wool : BONUS_WOOLS.values())
					{
						// Cycle color
						if (ticks % 30 == 0)
						{
							wool.color = (wool.color+1)%COLORS.length;
							wool.block.setType(COLORS[wool.color]);
						}

						// Particles
						final Location loc = wool.block.getLocation();
						for (int i = 0; i < 8; ++i)
						{
							Particle.DustTransition opts = new Particle.DustTransition(
								Color.fromRGB((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)),
								Color.fromRGB((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255)),
								1.f);
							loc.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, loc.clone().add(Math.random()*1.5-.25, Math.random()*1.5-.25, Math.random()*1.5-.25),
								2, 0.0, 0.0, 0.0, opts);
						}
					}

					ticks += 5;
				}
			}.runTaskTimer(SheepWars.getPlugin(), 0, 5);
		}
	}
}
