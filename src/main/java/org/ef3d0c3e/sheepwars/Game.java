package org.ef3d0c3e.sheepwars;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.events.*;
import org.ef3d0c3e.sheepwars.items.ItemBase;
import org.ef3d0c3e.sheepwars.kits.ArcherKit;
import org.ef3d0c3e.sheepwars.kits.BuilderKit;
import org.ef3d0c3e.sheepwars.kits.TechnicianKit;
import org.ef3d0c3e.sheepwars.level.Lobby;
import org.ef3d0c3e.sheepwars.level.Map;
import org.ef3d0c3e.sheepwars.sheeps.Sheeps;
import org.ef3d0c3e.sheepwars.skins.Skin;
import org.ef3d0c3e.sheepwars.skins.SkinMenu;
import org.ef3d0c3e.sheepwars.stats.StatEvents;
import org.ef3d0c3e.sheepwars.stats.StatMenu;
import org.ef3d0c3e.sheepwars.stats.StatSave;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;

public class Game
{
	public static class Timer extends BukkitRunnable
	{
		@Getter
		private int ticks = 0;
		private int seconds = 0;

		@Override
		public void run()
		{
			if (ticks % 20 == 0)
			{
				// Update scoreboard every seconds
				CPlayer.forEach((cp) -> cp.updateScoreboard());
				++seconds;
			}

			++ticks;
		}

		/**
		 * Gets number of seconds
		 * @return Number of seconds [0-59]
		 */
		public int getSeconds()
		{
			return seconds % 60;
		}

		/**
		 * Gets number of minutes
		 * @return Number of minutes
		 */
		public int getMinutes()
		{
			return seconds / 60;
		}

		/**
		 * Gets total elapsed time (in seconds)
		 * @return Elasped time in seconds `getMinutes() * 60 + getSeconds()`
		 */
		public int getElapsedTime()
		{
			return seconds;
		}

		/**
		 * Gets times formated time as mm:ss
		 * @return Formated time
		 */
		public String getPrettyTime()
		{
			String r = MessageFormat.format("{0}:", getMinutes());
			if (getSeconds() < 10)
				r += MessageFormat.format("0{0}", getSeconds());
			else
				r += MessageFormat.format("{0}", getSeconds());

			return r;
		}
	}

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
					// Bonus wool
					if (ticks > 600 && ticks % 600 == 0 && BonusWool.BONUS_WOOLS.isEmpty())
					{
						final Vector pos = Util.getRandomWithinCuboid(gameMap.getLowestWool(), gameMap.getHighestWool());
						final Location loc = new Location(map, pos.getX(), pos.getY(), pos.getZ());
						new BonusWool(loc);

						Bukkit.broadcastMessage("§7Un bloc bonus vient d'apparaître!");
					}

					// Limbo
					if (ticks % 15 == 0)
						CPlayer.forEach(cp ->
						{
							if (!cp.isOnline() || !cp.isAlive())
								return;

							final float r = gameMap.limboStrength(cp.getHandle().getLocation().getY());

							if (r >= 0.05f)
								cp.getHandle().damage(3.f * r);
							if (r == 1.f)
								Bukkit.getPluginManager().callEvent(new CPlayerDeathEvent(cp, (LivingEntity)null));
						});

					// Safe mode
					if (Game.isSafe() && ticks % 20 == 0)
					{
						final int left = Game.getSafeTime() - getTimer().getElapsedTime();
						CPlayer.forEach(cp ->
						{
							if (left != 0)
							{
								cp.getHandle().sendTitle(
									"§9La partie commence dans",
									"§6" + String.valueOf(left) + "...",
									0,
									40,
									0
								);
								cp.getHandle().playSound(cp.getHandle().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 65536.f, 1.f);
							} else
							{
								cp.getHandle().sendTitle(
									"§9Bonne chance!",
									"",
									0,
									20,
									5
								);
								cp.getHandle().playSound(cp.getHandle().getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, SoundCategory.MASTER, 65536.f, 1.6f);
							}
						});
					}

					// Kits ticks
					CPlayer.forEach(p -> {
						p.getKit().tick(ticks);
					});

					++ticks;
				}
			}.runTaskTimer(SheepWars.getPlugin(), 0, 1);
		}

		/**
		 * Stops water from flowing outside of world
		 * @param ev Event
		 */
		@EventHandler
		public void onWaterFlow(final BlockFromToEvent ev)
		{
			if (ev.getBlock().getWorld() != Game.getMap())
				return;

			final Map map = Game.getGameMap();
			//Bukkit.getConsoleSender().sendMessage(MessageFormat.format("{0} {1}", map.getLowestPoint(), map.getHighestPoint()));
			if (!map.isBounded(ev.getToBlock().getLocation(), true))
				ev.setCancelled(true);
		}

		/**
		 * Prevents snow update
		 * @param ev Event
		 */
		@EventHandler
		public void onSnowUpdate(final BlockPhysicsEvent ev)
		{
			if (ev.getSourceBlock().getType() != Material.SNOW)
				return;

			ev.setCancelled(true);
		}

		@EventHandler
		public void onJoin(final CPlayerJoinEvent ev)
		{
			final CPlayer cp = ev.getPlayer();

			if (cp.isAlive())
				cp.getHandle().setGameMode(GameMode.SURVIVAL);

			if (ev.isNewPlayer() || !cp.isAlive())
			{
				// TODO: Spectator
				cp.setAlive(false);
				cp.getHandle().setGameMode(GameMode.SPECTATOR);
				cp.getHandle().teleport(map.getSpawnLocation());
			}
		}

		@EventHandler
		public void onMove(final PlayerMoveEvent ev)
		{
			final CPlayer cp = CPlayer.getPlayer(ev.getPlayer());
			if (Game.isSafe() && cp.isAlive())
				ev.setCancelled(true);
		}

		@EventHandler
		public void onGameEnd(final GameEndEvent ev)
		{
			final Team winner = ev.getWinner();

			Game.getTimer().cancel();

			CPlayer.forEach(cp ->
			{
				if (!cp.isOnline())
					return;

				cp.updateScoreboard();
				cp.getHandle().sendTitle(
					MessageFormat.format("{0}L''équipe {1} a gagné!", Util.getColored(winner.getColorCode()), winner.getName()),
					"",
					5, 40, 10
				);

				if (cp.isAlive())
				{
					// TODO: Flying
				}
			});
		}
	}

	private static Random random;
	@Getter
	private static Timer timer;
	private static boolean started = false;

	private static StatMenu.Events statMenuListener;
	private static SkinMenu.Events skinMenuListener;
	private static Lobby.Events lobbyListener;
	@Getter
	private static World map;
	@Getter @Setter
	private static Map gameMap;

	public static Team RED_TEAM;
	public static Team BLUE_TEAM;



	/**
	 * Call before game starts
	 */
	public static void init()
	{
		random = new Random(System.currentTimeMillis());

		// Init lobby and create world
		Lobby.init();

		// Loas maps and maps configs
		Map.reloadMaps();

		Items.init();
		Bukkit.getPluginManager().registerEvents(new IGui.Events(), SheepWars.getPlugin());

		StatMenu.init();
		StatSave.init();
		statMenuListener = new StatMenu.Events();
		Bukkit.getPluginManager().registerEvents(statMenuListener, SheepWars.getPlugin());

		skinMenuListener = new SkinMenu.Events();
		Bukkit.getPluginManager().registerEvents(skinMenuListener, SheepWars.getPlugin());
		Bukkit.getPluginManager().registerEvents(new Skin.Events(), SheepWars.getPlugin());
		Bukkit.getPluginManager().registerEvents(new ItemBase.Events(SheepWars.getItemRegistry()), SheepWars.getPlugin());

		Bukkit.getPluginManager().registerEvents(new Sheeps.Events(), SheepWars.getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new CPlayer.Events(), SheepWars.getPlugin());
		lobbyListener = new Lobby.Events();
		Bukkit.getServer().getPluginManager().registerEvents(lobbyListener, SheepWars.getPlugin());


		// Teams
		RED_TEAM = new Team(Team.Color.ROUGE, "Rouge");
		BLUE_TEAM = new Team(Team.Color.BLEU, "Bleu");
		Team.addTeam(RED_TEAM);
		Team.addTeam(BLUE_TEAM);
	}


	/**
	 * Starts the game
	 * @param map Map to start on
	 */
	public static void start(final Map map)
	{
		GameStartEvent ev = new GameStartEvent();
		Bukkit.getPluginManager().callEvent(ev);

		timer = new Timer();
		timer.runTaskTimer(SheepWars.getPlugin(), 0, 1);
		started = true;
		gameMap = map;

		HandlerList.unregisterAll(statMenuListener);
		HandlerList.unregisterAll(skinMenuListener);
		HandlerList.unregisterAll(lobbyListener);

		Bukkit.getServer().getPluginManager().registerEvents(new Events(), SheepWars.getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteraction.Events(), SheepWars.getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new StatEvents(), SheepWars.getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new Combat.Events(), SheepWars.getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new Team.Events(), SheepWars.getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new RefilledResource.Events(), SheepWars.getPlugin());
		Bukkit.getServer().getPluginManager().registerEvents(new BonusWool.Events(), SheepWars.getPlugin());

		Game.map = map.generate();
		CPlayer.forEach((cp) -> {
			if (!cp.isOnline()) // All online are not spectators at this point
				return;
			cp.setAlive(true);
			cp.getHandle().teleport(Game.getRandomSpawn(cp.getTeam()));
			cp.getHandle().getInventory().clear();
			cp.getHandle().setGameMode(GameMode.SURVIVAL);
			cp.updateTablist();

			// Gear
			cp.getKit().setLoadout(cp.getHandle().getInventory(), cp.getTeam());
		});
		RefilledResource.addResource(new RefilledResource(40, new ItemStack(Material.ARROW),
			cp -> cp.getKit() instanceof ArcherKit ? 5 : 3,
			cp -> true,
			8
		));
		RefilledResource.addResource(new RefilledResource(100, new ItemStack(Material.BRICKS),
			cp -> 8,
			cp -> cp.getKit() instanceof BuilderKit
		));
		RefilledResource.addResource(new RefilledResource(100, new ItemStack(Material.SAND),
			cp -> 24,
			cp -> cp.getKit() instanceof BuilderKit
		));
		RefilledResource.addResource(new RefilledResource(100, new ItemStack(Material.ANVIL),
			cp -> 4,
			cp -> cp.getKit() instanceof BuilderKit
		));
		RefilledResource.addResource(new RefilledResource(100, new ItemStack(Material.COBWEB),
			cp -> 8,
			cp -> cp.getKit() instanceof TechnicianKit
		));

		Bukkit.getPluginManager().callEvent(new GameStartEvent());
	}

	/**
	 * Gets a random integer
	 * @returns A random positive integer
	 */
	public static int nextInt()
	{
		return random.nextInt(65536);
	}

	/**
	 * Gets whether game has started
	 * @returns True if game has started, false otherwise
	 */
	public static boolean hasStarted()
	{
		return started;
	}

	/**
	 * Gets safe time in seconds
	 * @return Safe time in seconds
	 */
	public static int getSafeTime()
	{
		return 10;
	}

	/**
	 * Gets whether safe mode is on
	 * @return True if safe mode is on
	 * @note Can't be called before game has started
	 */
	public static boolean isSafe()
	{
		return timer.seconds <= getSafeTime();
	}

	/**
	 * Interface for `forEachTeam` parameters
	 */
	public interface ForEachTeam
	{
		public void operation(Team team);
	}

	/**
	 * Execute lambda for all teams
	 * @param f Lambda expressiopn to execute for all teams
	 */
	public static void forEachTeam(ForEachTeam f)
	{
		for (Team team : Team.getTeamList())
			f.operation(team);
	}

	public static Location getRandomSpawn(final Team team)
	{
		List<Vector> spawns = null;
		float yaw = 0.f;
		if (team == RED_TEAM)
		{
			spawns = Game.getGameMap().getRedSpawns();
			yaw = Game.getGameMap().getRedYaw();
		}
		else if (team == BLUE_TEAM)
		{
			spawns = Game.getGameMap().getBlueSpawns();
			yaw = Game.getGameMap().getBlueYaw();
		}
		final Vector spawn = spawns.get(Game.nextInt() % spawns.size());

		return new Location(Game.getMap(), spawn.getX(), spawn.getY(), spawn.getZ(), yaw, 0.f);
	}
}
