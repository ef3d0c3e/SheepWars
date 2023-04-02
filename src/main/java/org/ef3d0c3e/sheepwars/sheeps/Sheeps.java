package org.ef3d0c3e.sheepwars.sheeps;

import org.bukkit.*;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Game;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.events.SheepDeathEvent;
import org.ef3d0c3e.sheepwars.events.SheepFireEvent;
import org.ef3d0c3e.sheepwars.level.Lobby;
import oshi.util.tuples.Pair;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class Sheeps
{
	static public class Events implements Listener
	{
		public Events()
		{
			new BukkitRunnable()
			{
				int ticks = 0;

				@Override
				public void run()
				{
					World world = null;
					if (Game.hasStarted())
						world = Game.getMap();
					else
						world = Game.getLobby();
					if (world == null)
						return;

					for (final Entity ent : world.getEntities())
					{
						if (!(((CraftEntity)ent).getHandle() instanceof BaseSheep))
							continue;

						((BaseSheep)((CraftEntity)ent).getHandle()).ctick();
					}

					if (ticks % 400 == 0)
					{
						CPlayer.forEach(cp ->
						{
							if (!cp.isAlive() || !cp.isOnline())
								return;

							final ArrayList<ItemStack> sheeps = cp.getKit().getSheeps();

							cp.getHandle().getInventory().addItem(sheeps.get( Game.nextInt() % sheeps.size() ));
							if (Game.nextInt() % 10 + 1 <= (int)(cp.getKit().getAdditionalSheepChance() * 10))
								cp.getHandle().getInventory().addItem(sheeps.get( Game.nextInt() % sheeps.size() ));
						});
					}

					++ticks;
				}
			}.runTaskTimer(SheepWars.plugin, 0, 1);

			// Register listeners for all sheeps
			list.forEach((name, pair) ->
			{
				Class<? extends BaseSheep> SheepClass = pair.getB();
				try
				{
					Class<?>[] SubClasses = SheepClass.getDeclaredClasses();
					for (Class<?> SubClass : SubClasses)
					{
						if (!SubClass.getName().endsWith("$Events"))
							continue;

						final Listener listener = (Listener)SubClass.getDeclaredConstructor().newInstance();
						Bukkit.getPluginManager().registerEvents(listener, SheepWars.plugin);
					}
				}
				catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
				{
					e.printStackTrace();
				}
			});
		}

		@EventHandler
		public void onSheepFallDamage(final EntityDamageEvent ev)
		{
			if (!(((CraftEntity)ev.getEntity()).getHandle() instanceof BaseSheep) || ev.getCause() != EntityDamageEvent.DamageCause.FALL)
				return;
			ev.setCancelled(true);
		}

		@EventHandler
		public void onWoolUse(final PlayerInteractEvent ev)
		{
			if (ev.getPlayer().getVehicle() != null &&
				((CraftEntity)ev.getPlayer().getVehicle()).getHandle() instanceof RemoteSheep)
				return;

			if (ev.getAction() != Action.RIGHT_CLICK_AIR && ev.getAction() != Action.RIGHT_CLICK_BLOCK)
				return;
			if (ev.getItem() == null)
				return;

			list.forEach((name, pair) ->
			{
				if (!ev.getItem().isSimilar(pair.getA()))
					return;
				ev.setCancelled(true);
				ev.getItem().setAmount(ev.getItem().getAmount()-1);
				final CPlayer cp = CPlayer.getPlayer(ev.getPlayer());

				Class<? extends BaseSheep> SheepClass = pair.getB();
				try
				{
					BaseSheep sheep = SheepClass.getDeclaredConstructor(Location.class, CPlayer.class).newInstance(cp.getHandle().getLocation(), cp);
					sheep.spawn();
					sheep.launch(cp.getHandle().getLocation().getDirection(), 1.5, 30);
					cp.getHandle().getWorld().playSound(cp.getHandle().getLocation(), Sound.ENTITY_CHICKEN_EGG, SoundCategory.MASTER, 1, 1.f);

					Bukkit.getPluginManager().callEvent(new SheepFireEvent(
						sheep, cp, true, name
					));
				}
				catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
				{
					e.printStackTrace();
				}
			});
		}

		@EventHandler
		public void onWoolDrop(final PlayerDropItemEvent ev)
		{
			if (ev.getPlayer().getVehicle() != null &&
				((CraftEntity)ev.getPlayer().getVehicle()).getHandle() instanceof RemoteSheep)
				return;

			list.forEach((name, pair) ->
			{
				if (!ev.getItemDrop().getItemStack().isSimilar(pair.getA()))
					return;
				ev.setCancelled(true);
				ev.getItemDrop().getItemStack().setAmount(ev.getItemDrop().getItemStack().getAmount()-1);
				final CPlayer cp = CPlayer.getPlayer(ev.getPlayer());

				Class<? extends BaseSheep> SheepClass = pair.getB();
				try
				{
					BaseSheep sheep = SheepClass.getDeclaredConstructor(Location.class, CPlayer.class).newInstance(cp.getHandle().getLocation(), cp);
					sheep.spawn();

					Bukkit.getPluginManager().callEvent(new SheepFireEvent(
						sheep, cp, false, name
					));
				}
				catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
				{
					e.printStackTrace();
				}
			});
		}

		@EventHandler
		public void onSheepDeath(final EntityDeathEvent ev)
		{
			if (!(((CraftEntity)ev.getEntity()).getHandle() instanceof BaseSheep))
				return;

			BaseSheep sheep = (BaseSheep)((CraftEntity)ev.getEntity()).getHandle();
			ev.setDroppedExp(0);
			ev.getDrops().clear();
			ev.getDrops().add(sheep.getDrop());

			final SheepDeathEvent deathEv = new SheepDeathEvent(sheep, ev.getDrops(), ev.getEntity().getKiller() != null ? CPlayer.getPlayer(ev.getEntity().getKiller()) : null);
			Bukkit.getPluginManager().callEvent(deathEv);
		}
	}

	static public HashMap<String, Pair<ItemStack, Class<? extends BaseSheep>>> list;

	static
	{
		list = new HashMap<>();

		list.put("explosive", new Pair(ExplosiveSheep.getItem(), ExplosiveSheep.class));
		list.put("boarding", new Pair(BoardingSheep.getItem(), BoardingSheep.class));
		list.put("dark", new Pair(DarkSheep.getItem(), DarkSheep.class));
		list.put("distortion", new Pair(DistortionSheep.getItem(), DistortionSheep.class));
		list.put("earthquake", new Pair(EarthQuakeSheep.getItem(), EarthQuakeSheep.class));
		list.put("fragmentation", new Pair(FragmentationSheep.getItem(), FragmentationSheep.class));
		list.put("frozen", new Pair(FrozenSheep.getItem(), FrozenSheep.class));
		list.put("shield", new Pair(ShieldSheep.getItem(), ShieldSheep.class));
		list.put("healer", new Pair(HealerSheep.getItem(), HealerSheep.class));
		list.put("incendiary", new Pair(IncendiarySheep.getItem(), IncendiarySheep.class));
		list.put("seeker", new Pair(SeekerSheep.getItem(), SeekerSheep.class));
		list.put("swap", new Pair(SwapSheep.getItem(), SwapSheep.class));
		//list.put("remote", new Pair(RemoteSheep.getItem(), RemoteSheep.class));
		list.put("lightning", new Pair(LightningSheep.getItem(), LightningSheep.class));
	}

	static public int getDispawnLayer()
	{
		if (Game.hasStarted())
		{
			return Game.getGameMap().getLimboEnd();
		}
		else
		{
			return Lobby.getLimboHeight();
		}
	}
}