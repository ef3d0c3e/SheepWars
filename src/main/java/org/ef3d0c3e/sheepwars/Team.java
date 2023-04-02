package org.ef3d0c3e.sheepwars;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.ef3d0c3e.sheepwars.events.CPlayerDeathEvent;
import org.ef3d0c3e.sheepwars.events.GameEndEvent;
import org.ef3d0c3e.sheepwars.events.GameStartEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Team from the game
 */
public class Team
{
	/**
	 * Color for a team
	 */
	static class Color
	{
		String colorName; ///< Color's name
		String colorCode; ///< Color's code (with delimiters)
		int colorValue; ///< Color's hex value
		Material wool; ///< Corresponding colored wool
		Material banner; ///< Corresponding colored banner

		/**
		 * Creates a color
		 * @param colorName Color's name
		 * @param colorCode Color's code (with delimiters)
		 * @param wool Corresponding colored wool
		 * @param banner Corresponding colored banner
		 */
		private Color(final String colorName, final String colorCode, final Material wool, final Material banner)
		{
			this.colorName = colorName;
			this.colorCode = colorCode;
			colorValue = 0;
			colorValue |= Integer.valueOf(colorCode.substring(2, 4), 16) << 16;
			colorValue |= Integer.valueOf(colorCode.substring(4, 6), 16) << 8;
			colorValue |= Integer.valueOf(colorCode.substring(6, 8), 16);
			this.wool = wool;
			this.banner = banner;
		}

		public final static Color ORANGE = new Color("Orange", "<#D09000>", Material.ORANGE_WOOL, Material.ORANGE_BANNER);
		public final static Color MAGENTA = new Color("Magenta", "<#D050A0>", Material.MAGENTA_WOOL, Material.MAGENTA_BANNER);
		public final static Color BLEUCLAIR = new Color("BleuClair", "<#40B0F0>", Material.LIGHT_BLUE_WOOL, Material.LIGHT_BLUE_BANNER);
		public final static Color JAUNE = new Color("Jaune", "<#D0F000>", Material.YELLOW_WOOL, Material.YELLOW_BANNER);
		public final static Color VERTCLAIR = new Color("VertClair", "<#20D050>", Material.LIME_WOOL, Material.LIME_BANNER);
		public final static Color ROSE = new Color("Rose", "<#F090D0>", Material.PINK_WOOL, Material.PINK_BANNER);
		public final static Color GRIS = new Color("Gris", "<#404040>", Material.GRAY_WOOL, Material.GRAY_BANNER);
		public final static Color GRISCLAIR = new Color("GrisClair", "<#909090>", Material.LIGHT_GRAY_WOOL, Material.LIGHT_GRAY_BANNER);
		public final static Color CYAN = new Color("Cyan", "<#20D0D0>", Material.CYAN_WOOL, Material.CYAN_BANNER);
		public final static Color VIOLET = new Color("Violet", "<#FF30B0>", Material.PURPLE_WOOL, Material.PURPLE_BANNER);
		public final static Color BLEU = new Color("Bleu", "<#1050ff>", Material.BLUE_WOOL, Material.BLUE_BANNER);
		public final static Color MARRON = new Color("Marron", "<#C06A00>", Material.BROWN_WOOL, Material.BROWN_BANNER);
		public final static Color VERT = new Color("Vert", "<#00FF20>", Material.GREEN_WOOL, Material.GREEN_BANNER);
		public final static Color ROUGE = new Color("Rouge", "<#F04040>", Material.RED_WOOL, Material.RED_BANNER);

		public final static Color list[] =
		{
			ORANGE,
			MAGENTA,
			BLEUCLAIR,
			JAUNE,
			VERTCLAIR,
			ROSE,
			GRIS,
			GRISCLAIR,
			CYAN,
			VIOLET,
			BLEU,
			MARRON,
			VERT,
			ROUGE,
		};
	}

	public static class Events implements Listener
	{
		@EventHandler
		public void onCPlayerDeath(final CPlayerDeathEvent ev)
		{
			// Check if team is still alive
			final Team team = ev.getVictim().getTeam();
			team.aliveCount = 0;
			team.forEach((cp) -> team.aliveCount += cp.isAlive() ? 1 : 0 );
			if (!team.isAlive())
				Bukkit.getPluginManager().callEvent(new GameEndEvent(team == Game.RED_TEAM ? Game.BLUE_TEAM : Game.RED_TEAM));

			CPlayer.forEach(cp -> cp.updateScoreboard());
		}

		@EventHandler
		public void onGameStart(final GameStartEvent ev)
		{
			Game.forEachTeam((team) ->
			{
				team.aliveCount = 0;
				team.forEach((cp) ->
				{
					++team.aliveCount;
				});
			});
		}
	}

	private static ArrayList<Team> teamList = new ArrayList<>();

	/**
	 * Gets team list
	 * @return Team list
	 */
	public static ArrayList<Team> getTeamList()
	{
		return teamList;
	}

	/**
	 * Adds a team
	 * @param team Team to add
	 */
	public static void addTeam(final Team team)
	{
		teamList.add(team);
	}

	private Color color; ///< Team's color
	private String name; ///< Team's name
	private Vector<CPlayer> playerList; ///< List of all team members

	public int aliveCount;

	/**
	 * Constructs a new team
	 * @param color Team's color
	 * @param name Team's name
	 */
	public Team(Color color, String name)
	{
		this.color = color;
		this.name = name;
		playerList = new Vector<>();
		aliveCount = 0;
	}

	/**
	 * Gets team's name
	 * @return Team's name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets team's color code
	 * @return Team's color code
	 */
	public String getColorCode()
	{
		return color.colorCode;
	}

	/**
	 * Gets color value
	 * @return Value has 24bit RGB
	 */
	public int getColorValue()
	{
		return color.colorValue;
	}

	/**
	 * Gets colored version of team's name
	 * @return Team's name colored using team's color
	 */
	public String getColoredName()
	{
		return Util.getColored(color.colorCode + name);
	}

	/**
	 * Gets corresponding wool color
	 * @return Wool color
	 */
	public Material getColoredWool()
	{
		return color.wool;
	}

	/**
	 * Gets corresponding banner color
	 * @return banner color
	 */
	public Material getColoredBanner()
	{
		return color.banner;
	}

	/**
	 * Gets corresponding color for armors
	 * @return Color
	 */
	public org.bukkit.Color getArmorColor()
	{
		return org.bukkit.Color.fromRGB(color.colorValue);
	}

	/**
	 * Gets playerList
	 * @return List of players in team
	 */
	public Vector<CPlayer> getPlayerList()
	{
		return playerList;
	}

	/**
	 * Gets the number of alive player
	 * @return Number of alive player
	 */
	public int getAliveCount()
	{
		return aliveCount;
	}

	/**
	 * Gets whether team is still alive
	 * @return True if team is still alive (i.e at least one player remains), false otherwise
	 */
	public boolean isAlive()
	{
		return aliveCount != 0;
	}

	/**
	 * Interface for `forEach` parameters
	 */
	public interface ForEach
	{
		public void operation(CPlayer cp);
	}

	/**
	 * Execute lambda for all players in team
	 * @param f Lambda expressiopn to execute for all players
	 */
	public void forEach(ForEach f)
	{
		for (CPlayer cp : playerList)
			f.operation(cp);
	}

	/**
	 * Sets player team
	 * @param cp Player
	 * @param team Team (or null)
	 */
	public static void setTeam(final CPlayer cp, final Team team)
	{
		if (cp.getTeam() != null) // Erase from team
			cp.getTeam().playerList.remove(cp);

		cp.setTeam(team);
		if (team != null)
			team.playerList.add(cp);
	}
}
