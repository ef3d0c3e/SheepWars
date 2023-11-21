package org.ef3d0c3e.sheepwars;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.ef3d0c3e.sheepwars.events.*;
import org.ef3d0c3e.sheepwars.kits.Kit;
import org.ef3d0c3e.sheepwars.kits.Kits;
import org.ef3d0c3e.sheepwars.locale.Locale;
import org.ef3d0c3e.sheepwars.stats.StatDouble;
import org.ef3d0c3e.sheepwars.stats.StatSave;
import org.ef3d0c3e.sheepwars.stats.StatValue;
import org.ef3d0c3e.sheepwars.stats.StatLong;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Custom class for player
 */
public class CPlayer
{
	static class Events implements Listener
	{
		@EventHandler(priority = EventPriority.LOW)
		public void onJoin(final CPlayerJoinEvent ev)
		{
			final CPlayer cp = ev.getPlayer();
			Bukkit.broadcastMessage(MessageFormat.format("§8[§a+§8] §7{0}", cp.getHandle().getName()));
			StatSave.load(cp);

			if (!Game.hasStarted())
			{
				// Default Team
				Team.setTeam(cp, Team.getTeamList().get(0));

				// Default Kit
				final Kit kit = Kits.list.get(Game.nextInt() % Kits.list.size());
				try
				{
					cp.setKit(kit.getClass().getDeclaredConstructor(CPlayer.class).newInstance(cp));
				}
				catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
				{
					e.printStackTrace();
				}
			}
		}

		@EventHandler
		public void onQuit(final CPlayerQuitEvent ev)
		{
			final CPlayer cp = ev.getPlayer();
			Bukkit.broadcastMessage(MessageFormat.format("§8[§c-§8] §7{0}", cp.getHandle().getName()));
			StatSave.save(cp);
			cp.lastLocation = cp.getHandle().getLocation();
		}

		@EventHandler
		public void onChat(final AsyncPlayerChatEvent ev)
		{
			String prefix = "", suffix;
			final CPlayer cp = CPlayer.getPlayer(ev.getPlayer());
			final Team team = cp.getTeam();

			suffix = "§8: §7";
			if (Game.hasStarted() && !cp.isAlive())
				prefix = "§7[MORT] ";

			if (team == null)
				prefix = "§e§o";
			else
				prefix = Util.getColored(team.getColorCode());

			ev.setFormat(prefix + cp.getHandle().getName() + suffix + "%2$s");

			if (cp.getHandle().isOp())
				ev.setMessage(Util.getColored(ev.getMessage()));
		}

		@EventHandler(priority = EventPriority.LOW)
		public void onCPlayerDeath(final CPlayerDeathEvent ev)
		{
			ev.getVictim().setAlive(false);
			ev.getVictim().getHandle().setGameMode(GameMode.SPECTATOR);
		}
	};

	private Player player;
	private OfflinePlayer offlinePlayer;
	private Location lastLocation;
	@Getter
	private boolean alive;
	@Getter
	private Team team;
	@Getter
	private Kit kit;

	private org.bukkit.scoreboard.Team nametagTeam;

	private Combat.Data combatData;
	private HashMap<String, StatValue> stats; ///< List of stats associated with player
	private int skin;

	@Getter @Setter
	private PlayerInteraction.Data interactionData;
	@Getter
	private Locale locale;
	@Getter
	private CosmeticManager cosmetics;

	/**
	 * Constructor
	 * @param p Player
	 */
	public CPlayer(final Player p)
	{
		player = p;
		offlinePlayer = Bukkit.getOfflinePlayer(p.getUniqueId());
		lastLocation = p.getLocation();
		alive = false;
		team = null;
		kit = null;

		combatData = new Combat.Data();
		stats = new HashMap<>();
		skin = -1;

		interactionData = new PlayerInteraction.Data();
		locale = SheepWars.getLocaleManager().getDefaultLocale();
		cosmetics = new CosmeticManager(this);
	}

	/**
	 * Gets native handle
	 * @return Bukkit player
	 */
	public Player getHandle()
	{
		return player;
	}

	/**
	 * Sets underlying player
	 * @param player Player
	 */
	public void setHandle(final Player player)
	{
		this.offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
		this.player = player;
	}

	/**
	 * Gets offline player
	 * @return
	 */
	public OfflinePlayer getOfflinePlayer()
	{
		return offlinePlayer;
	}

	/**
	 * Gets whether player is online or not
	 * @return
	 */
	public boolean isOnline()
	{
		return offlinePlayer.isOnline();
	}

	/**
	 * Sets whether player is alive
	 * @param alive Whether player is alive
	 */
	public void setAlive(final boolean alive)
	{
		this.alive = alive;
	}

	/**
	 * Adds player to team
	 * @note Fires a CPlayerTeamChangeEvent
	 * @param team Player's team
	 * @note Use Team.setTeam() instead
	 */
	public void setTeam(final Team team)
	{
		final Team oldTeam = this.team;
		this.team = team;

		if (oldTeam != null && team != null) // Do not fire on connect/disconnect
			Bukkit.getPluginManager().callEvent(new CPlayerTeamChangeEvent(this, oldTeam, team));
	}

	/**
	 * Sets player's kit
	 * @note Fires a CPlayerKitChangeEvent
	 * @param kit Player's kit
	 */
	public void setKit(final Kit kit)
	{
		final Kit oldKit = kit;
		this.kit = kit;
		Bukkit.getServer().getPluginManager().callEvent(new CPlayerKitChangeEvent(this, oldKit, kit));
	}

	/**
	 * Sets player locale
	 * @note Fires a CPlayerSetLocaleEvent
	 * @brief locale New locale
	 */
	public void setLocale(final Locale locale)
	{
		this.locale = locale;
		Bukkit.getServer().getPluginManager().callEvent(new CPlayerSetLocaleEvent(this, locale));
	}

	/**
	 * Gets combat data
	 * @return Combat data
	 */
	public Combat.Data getCombatData()
	{
		return combatData;
	}

	/**
	 * Sets combat data
	 * @param combatData Data
	 */
	public void setCombatData(final Combat.Data combatData)
	{
		this.combatData = combatData;
	}


	static HashMap<String, CPlayer> playerList = new HashMap<>();

	/**
	 * Adds a player to the player list
	 * @param p Player to add
	 * @return Added player
	 * @note Won't add player if already present
	 */
	public static CPlayer addPlayer(final Player p)
	{
		CPlayer cp;
		cp = playerList.get(p.getName());
		if (cp != null) // Already in playerList
			return cp;

		cp = new CPlayer(p);
		playerList.put(p.getName(), cp);
		return cp;
	}

	/**
	 * Gets CPlayer from bukkit player
	 * @param p Bukkit player
	 * @return CPlayer corresponding to p
	 * @note Will return null if p has no associated CPlayer
	 */
	public static CPlayer getPlayer(final Player p)
	{
		return playerList.get(p.getName());
	}

	/**
	 * Interface for `forEachPlayer` parameters
	 */
	public interface ForEachPlayer
	{
		public void operation(CPlayer cp);
	}

	/**
	 * Execute lambda for all players
	 * @param f Lambda expressiopn to execute for all players
	 */
	public static void forEach(ForEachPlayer f)
	{
		for (CPlayer cp : playerList.values())
			f.operation(cp);
	}

	/**
	 * Gets stats from key name
	 * @param key Key of stat
	 * @return StatValue if found, null otherwise
	 */
	public StatValue getStat(final String key)
	{
		return stats.get(key);
	}

	/**
	 * Sets stats from key name
	 * @param key Key of stat
	 * @param value Value of stat
	 */
	public void setStat(final String key, final StatValue value)
	{
		stats.put(key, value);
	}

	public void incrementStat(final String key)
	{
		final StatLong s = (StatLong)stats.get(key);
		++s.value;
	}

	public void incrementStat(final String key, final double amt)
	{
		final StatDouble s = (StatDouble)stats.get(key);
		s.value += amt;
	}

	public int getSkin()
	{
		return skin;
	}

	public void setSkin(final int skin)
	{
		this.skin = skin;
	}
}