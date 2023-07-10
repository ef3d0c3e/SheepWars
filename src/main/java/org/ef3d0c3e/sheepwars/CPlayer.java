package org.ef3d0c3e.sheepwars;

import fr.mrmicky.fastboard.FastBoard;
import locale.Locale;
import locale.LocaleManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerDeathEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerQuitEvent;
import org.ef3d0c3e.sheepwars.kits.Kit;
import org.ef3d0c3e.sheepwars.kits.Kits;
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

			cp.fb = new FastBoard(cp.getHandle());
			CPlayer.forEach((cp2) -> cp2.updateScoreboard()); // Update for everyone
			cp.updateTablist();
			cp.updateTabname();
			cp.updateNametag();
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
	private FastBoard fb;
	private boolean alive;
	private Team team;
	private Kit kit;

	private org.bukkit.scoreboard.Team nametagTeam;

	private Combat.Data combatData;
	private HashMap<String, StatValue> stats; ///< List of stats associated with player
	private int skin;

	@Getter @Setter
	private PlayerInteraction.Data interactionData;

	@Getter @Setter
	private Locale locale;

	/**
	 * Constructor
	 * @param p Player
	 */
	public CPlayer(final Player p)
	{
		player = p;
		offlinePlayer = Bukkit.getOfflinePlayer(p.getUniqueId());
		lastLocation = p.getLocation();
		fb = null;
		alive = false;
		team = null;
		kit = null;

		combatData = new Combat.Data();
		stats = new HashMap<>();
		skin = -1;

		interactionData = new PlayerInteraction.Data();

		//locale = LocaleManager.getDefaultLocale();
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
	 * Gets whether player is alive
	 * @return True if player is alive, false otherwise
	 */
	public boolean isAlive()
	{
		return alive;
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
	 * Gets player's team
	 * @return Player's team
	 */
	public Team getTeam()
	{
		return team;
	}

	/**
	 * Adds player to team
	 * @param team Player's team
	 * @note Use Team.setTeam() instead
	 */
	public void setTeam(final Team team)
	{
		this.team = team;
	}

	/**
	 * Gets player's kit
	 * @return Player's kit
	 */
	public Kit getKit()
	{
		return kit;
	}

	/**
	 * Sets player's kit
	 * @param kit Player's kit
	 */
	public void setKit(final Kit kit)
	{
		this.kit = kit;
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
	 * Update player's scoreboard
	 */
	public void updateScoreboard()
	{
		ArrayList<String> l = new ArrayList<>();
		if (!Game.hasStarted()) // Lobby
		{
			fb.updateTitle(" §f● §d§lSHEEPWARS§f ● ");

			l.add("§0");
			l.add("§d|§f §lKit");
			l.add(getKit().getColoredName());

			l.add("§0");
			l.add("§d|§f §lÉquipes");
			Game.forEachTeam((team) ->
			{
				if (team == getTeam())
					l.add(MessageFormat.format("{0}§7: §6»§e{1}§6«",
						Util.getColored(team.getColoredName()), team.getPlayerList().size()));
				else
					l.add(MessageFormat.format("{0}§7:§e {1}",
						Util.getColored(team.getColoredName()), team.getPlayerList().size()));
			});

			l.add("§0");
			l.add(Util.getColored("   <#87EDAB>pundalik.org"));
		}
		else
		{
			fb.updateTitle(" §f● §d§lSHEEPWARS§f ● ");

			l.add("§0");
			l.add("§d|§f §lDurée");
			l.add("§e" + Game.getTimer().getPrettyTime());

			if (isAlive())
			{
				l.add("§0");
				l.add("§d|§f §lKit");
				l.add(getKit().getColoredName());
			}

			l.add("§0");
			l.add("§d|§f §lÉquipes");
			Game.forEachTeam((team) ->
			{
				if (team == getTeam())
					l.add(MessageFormat.format("{0}§7: §6»§e{1}§6«",
						Util.getColored(team.getColoredName()), team.getAliveCount()));
				else
					l.add(MessageFormat.format("{0}§7:§e {1}",
						Util.getColored(team.getColoredName()), team.getAliveCount()));
			});

			l.add("§0");
			l.add(Util.getColored("   <#87EDAB>pundalik.org"));
		}

		fb.updateLines(l);
	}

	/**
	 * Updates tab list header/footer
	 */
	public void updateTablist()
	{
		String header, footer;

		header = " §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l §l \n"
			+ "§6§l<§f§l°§6§l)))>< §a §l✤ §e§lSHEEPWARS §a§l✤ §6 §l><(((§f§l°§6§l>\n";
		footer = "\n"
			+ "§b§lVersion:§e 1.4 §7§o[/changelog]\n"
			+ "§c§lSite internet:§d pundalik.org/sheepwars\n";
		if (Game.hasStarted())
			footer += MessageFormat.format("\n§e§lCarte: §a{0}\n", Game.getGameMap().getDisplayName());

		player.setPlayerListHeaderFooter(header, footer);
	}

	/**
	 * Updates player's name in tab
	 */
	public void updateTabname()
	{
		String color, prefix, suffix;
		if (getTeam() == null)
			color = "§7";
		else
			color = Util.getColored(getTeam().getColorCode());

		prefix = suffix = "";
		if (!Game.hasStarted() || isAlive())
			suffix = " §7: " + getKit().getColoredName();

		player.setPlayerListName(MessageFormat.format("{1}{2}{0}{3}", player.getName(), prefix, color, suffix));
	}

	/**
	 * Updates player's nametag (prefix & suffix)
	 * @note Also display health
	 */
	public void updateNametag()
	{
		if (nametagTeam == null)
		{
			nametagTeam = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(player.getName());
			if (nametagTeam == null)
				nametagTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(player.getName());
			nametagTeam.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, org.bukkit.scoreboard.Team.OptionStatus.NEVER); // Just set this once
		}

		nametagTeam.setPrefix("");
		nametagTeam.setSuffix("");
		nametagTeam.setColor(ChatColor.DARK_GRAY);
		if (getTeam() != null)
		{
			nametagTeam.setPrefix(Util.getColored(getTeam().getColorCode()) + "|" + getTeam().getName() + "| ");
		}

		// TODO: Health

		if (nametagTeam.getEntries().isEmpty()) // Should only ever contain a single player
			nametagTeam.addEntry(player.getName());
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