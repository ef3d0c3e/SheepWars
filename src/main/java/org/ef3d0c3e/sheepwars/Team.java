package org.ef3d0c3e.sheepwars;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ef3d0c3e.sheepwars.events.CPlayerDeathEvent;
import org.ef3d0c3e.sheepwars.events.CPlayerTeamChangeEvent;
import org.ef3d0c3e.sheepwars.events.GameEndEvent;
import org.ef3d0c3e.sheepwars.events.GameStartEvent;
import org.ef3d0c3e.sheepwars.items.ItemBase;
import org.ef3d0c3e.sheepwars.level.MapMenu;

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
		private Color(final String colorCode, final Material wool, final Material banner)
		{
			this.colorCode = colorCode;
			colorValue = 0;
			colorValue |= Integer.valueOf(colorCode.substring(2, 4), 16) << 16;
			colorValue |= Integer.valueOf(colorCode.substring(4, 6), 16) << 8;
			colorValue |= Integer.valueOf(colorCode.substring(6, 8), 16);
			this.wool = wool;
			this.banner = banner;
		}

		public final static Color BLUE = new Color("<#1050ff>", Material.BLUE_WOOL, Material.BLUE_BANNER);
		public final static Color RED = new Color("<#F04040>", Material.RED_WOOL, Material.RED_BANNER);
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
	 * Gets the base name for the team
	 * @return Team's base name
	 */
	public String getBaseName()
	{
		return this.name;
	}

	/**
	 * Gets team's name
	 * @return Team's name
	 */
	public String getName(CPlayer cp)
	{
		if (name.equals("red"))
			return cp.getLocale().TEAM_RED;
		else if (name.equals("blue"))
			return cp.getLocale().TEAM_BLUE;
		return "<unknown>";
	}


	/**
	 * Gets team's name as chat
	 * @return Team's name as chat
	 */
	public WrappedChatComponent getTeamName(CPlayer cp)
	{
		return WrappedChatComponent.fromText(Util.getColored(getColorCode()) + getName(cp));
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
		final Team oldTeam = cp.getTeam();
		if (oldTeam != null) // Erase from team
			oldTeam.playerList.remove(cp);

		if (team != null) // null on disconnect
			team.playerList.add(cp);
		cp.setTeam(team);
	}


	public static class TeamChangeItem extends ItemBase
	{
		public TeamChangeItem()
		{
			super();
		}

		@Override
		protected boolean onInteract(Player p, ItemStack item, Action action, EquipmentSlot hand, Block clicked, BlockFace clickedFace)
		{
			if (Game.hasStarted() || (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK))
				return true;

			final CPlayer cp = CPlayer.getPlayer(p);
			// Change team (get next team)
			for (int i = 0; i < Team.getTeamList().size(); ++i)
			{
				if (cp.getTeam() != Team.getTeamList().get(i))
					continue;

				Team.setTeam(cp, Team.getTeamList().get( (i+1) % Team.getTeamList().size() )); // Fires a CPlayerTeamChangeEvent
				break;
			}

			// Change wool type
			p.getInventory().setItem(hand, getItem(cp));

			return true;
		}

		@Override
		protected boolean onDrop(Player p, ItemStack item)
		{
			return true;
		}
	}

	static final private TeamChangeItem TeamItem = new TeamChangeItem();
	public static ItemStack getItem(final CPlayer cp)
	{
		final ItemStack item = new ItemStack(cp.getTeam().getColoredBanner());
		final ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(Util.getColored(cp.getTeam().getColorCode()) + MessageFormat.format(cp.getLocale().ITEM_TEAM, cp.getTeam().getName(cp)) + " " + cp.getLocale().ITEM_RIGHTCLICK);
		meta.setLore(cp.getLocale().ITEM_TEAMLORE);
		item.setItemMeta(meta);

		SheepWars.getItemRegistry().registerItem(TeamItem);
		return TeamItem.apply(item);
	}
}
