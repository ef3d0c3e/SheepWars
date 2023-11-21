package org.ef3d0c3e.sheepwars;

import com.comphenix.packetwrapper.wrappers.play.clientbound.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import fr.mrmicky.fastboard.FastBoard;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.world.scores.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.eclipse.sisu.Priority;
import org.ef3d0c3e.sheepwars.events.*;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import com.comphenix.packetwrapper.wrappers.play.clientbound.WrapperPlayServerScoreboardTeam;

import javax.annotation.Nullable;

public class CosmeticManager
{
	final CPlayer cp;
	public FastBoard fb;

	private static WrapperPlayServerScoreboardTeam getTeamPacket(final CPlayer of, final CPlayer to)
	{
		WrapperPlayServerScoreboardTeam packet = new WrapperPlayServerScoreboardTeam();

		packet.setMethodEnum(WrapperPlayServerScoreboardTeam.Method.CREATE_TEAM);
		packet.setName(of.getHandle().getName());
		packet.setPlayers(Collections.singletonList(of.getHandle().getName()));


		packet.setParameters(new WrapperPlayServerScoreboardTeam.WrappedParameters(
			WrappedChatComponent.fromText(""),
			WrappedChatComponent.fromLegacyText(Util.getColored(of.getTeam().getColorCode()) + "|" +  of.getTeam().getName(to) + "| "),
			WrappedChatComponent.fromText(""),
			Team.Visibility.ALWAYS.toString(),
			Team.CollisionRule.NEVER.toString(),
			ChatColor.GRAY,
			0));

		return packet;
	}

	private static WrappedChatComponent getTabNames(final CPlayer of, final CPlayer to)
	{
		final String color = of.getTeam() == null ? "§7" : Util.getColored(of.getTeam().getColorCode());
		final String suffix = (!Game.hasStarted() || of.isAlive()) ? (" §7: " + of.getKit().getColoredName(to)) : "";

		return WrappedChatComponent.fromLegacyText(MessageFormat.format("{1}{0}{2}", of.getHandle().getName(), color, suffix));
	}

	private static WrapperPlayServerPlayerInfo getTabNamesPacket(final CPlayer of, final CPlayer to)
	{
		final WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
		packet.setActions(Collections.singleton(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME));



		PlayerInfoData data = new PlayerInfoData(
			of.getHandle().getUniqueId(),
			of.getHandle().getPing(),
			true,
			EnumWrappers.NativeGameMode.fromBukkit(of.getHandle().getGameMode()),
			new WrappedGameProfile(of.getHandle().getUniqueId(), of.getHandle().getName()),
			getTabNames(of, to)
		);
		packet.setEntries(Collections.singletonList(data));

		return packet;
	}

	public CosmeticManager(final CPlayer cp)
	{
		this.cp = cp;
		this.fb = null;
	}

	/**
	 * Updates tablist footer & header
	 */
	public void updateTablist()
	{
		String footer = MessageFormat.format(cp.getLocale().TAB_FOOTER, SheepWars.getPlugin().getDescription().getVersion());
		if (Game.hasStarted())
			footer += MessageFormat.format(cp.getLocale().TAB_FOOTERMAP, Game.getGameMap().getDisplayName());

		cp.getHandle().setPlayerListHeaderFooter(cp.getLocale().TAB_HEADER, footer);
	}

	/**
	 * Update players's tab name
	 */
	public void updateTabNames()
	{
		WrapperPlayServerPlayerInfo remove = new WrapperPlayServerPlayerInfo();
		remove.setActions(Collections.singleton(EnumWrappers.PlayerInfoAction.UPDATE_LISTED));
		CPlayer.forEach(o -> {
			SheepWars.getProtocolManager().sendServerPacket(cp.getHandle(), getTabNamesPacket(o, cp).getHandle());
		});
	}

	/**
	 * Update players' nametag
	 */
	public void updateNameTags()
	{
		final WrapperPlayServerScoreboardTeam removeTeam = new WrapperPlayServerScoreboardTeam();
		removeTeam.setMethodEnum(WrapperPlayServerScoreboardTeam.Method.REMOVE_TEAM);
		CPlayer.forEach(o -> {
			removeTeam.setName(o.getHandle().getName());
			SheepWars.getProtocolManager().sendServerPacket(cp.getHandle(), removeTeam.getHandle());
			SheepWars.getProtocolManager().sendServerPacket(cp.getHandle(), getTeamPacket(o, cp).getHandle());
		});
	}

	public void updateScoreboard()
	{
		if (fb == null)
			fb = new FastBoard(cp.getHandle());

		ArrayList<String> l = new ArrayList<>();
		if (!Game.hasStarted()) // Lobby
		{
			fb.updateTitle(cp.getLocale().SCOREBOARD_TITLE);

			l.add("§0");
			l.add(cp.getLocale().SCOREBOARD_KIT);
			l.add(cp.getKit().getColoredName(cp));

			l.add("§0");
			l.add(cp.getLocale().SCOREBOARD_TEAM);
			Game.forEachTeam((team) ->
			{
				if (team == cp.getTeam())
					l.add(MessageFormat.format("{0}§7: §6»§e{1}§6«",
						Util.getColored(team.getColorCode()) + team.getName(cp), team.getPlayerList().size()));
				else
					l.add(MessageFormat.format("{0}§7:§e {1}",
						Util.getColored(team.getColorCode()) + team.getName(cp), team.getPlayerList().size()));
			});

			l.add("§0");
			l.add(Util.getColored(cp.getLocale().SCOREBOARD_FOOTER));
		}
		else
		{
			fb.updateTitle(cp.getLocale().SCOREBOARD_TITLE);

			l.add("§0");
			l.add(cp.getLocale().SCOREBOARD_DURATION);
			l.add("§e" + Game.getTimer().getPrettyTime());

			if (cp.isAlive())
			{
				l.add("§0");
				l.add(cp.getLocale().SCOREBOARD_KIT);
				l.add(cp.getKit().getColoredName(cp));
			}

			l.add("§0");
			l.add(cp.getLocale().SCOREBOARD_TEAM);
			Game.forEachTeam((team) ->
			{
				if (team == cp.getTeam())
					l.add(MessageFormat.format("{0}§7: §6»§e{1}§6«",
						Util.getColored(team.getColorCode()) + team.getName(cp), team.getAliveCount()));
				else
					l.add(MessageFormat.format("{0}§7:§e {1}",
						Util.getColored(team.getColorCode()) + team.getName(cp), team.getAliveCount()));
			});

			l.add("§0");
			l.add(Util.getColored(cp.getLocale().SCOREBOARD_FOOTER));
		}

		fb.updateLines(l);
	}

	public static class Events implements Listener
	{
		public Events()
		{
			SheepWars.getProtocolManager().addPacketListener(new PacketListener()
			{
				@Override
				public void onPacketSending(final PacketEvent packetEvent)
				{
					final WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo(packetEvent.getPacket());
					if (!packet.getActions().contains(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME))
						return;

					for (PlayerInfoData data : packet.getEntries())
					{

						final CPlayer to = CPlayer.getPlayer(packetEvent.getPlayer());
						final CPlayer of = CPlayer.getPlayer(Bukkit.getPlayer(data.getProfileId()));

						final PlayerInfoData info = new PlayerInfoData(
							data.getProfileId(),
							data.getLatency(),
							data.isListed(),
							data.getGameMode(),
							data.getProfile(),
							CosmeticManager.getTabNames(of, to),
							data.getRemoteChatSessionData()
						);
						packet.setEntries(Collections.singletonList(info));
						packetEvent.setPacket(packet.getHandle());
					}


				}

				@Override
				public void onPacketReceiving(PacketEvent packetEvent) {}

				final static ListeningWhitelist listen_whitelist = ListeningWhitelist.newBuilder()
					.gamePhase(GamePhase.PLAYING)
					.types(PacketType.Play.Server.PLAYER_INFO)
					.build();

				@Override
				public ListeningWhitelist getSendingWhitelist()
				{
					return listen_whitelist;
				}

				@Override
				public ListeningWhitelist getReceivingWhitelist() { return ListeningWhitelist.EMPTY_WHITELIST; }

				@Override
				public Plugin getPlugin() { return SheepWars.getPlugin(); }
			});
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onJoin(final CPlayerJoinEvent ev)
		{
			ev.getPlayer().getCosmetics().updateTablist();
			CPlayer.forEach(cp -> {
				final CosmeticManager cosmetics = cp.getCosmetics();

				cosmetics.updateTabNames();
				cosmetics.updateNameTags();
				cosmetics.updateScoreboard();
			});
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onQuit(final CPlayerQuitEvent ev)
		{
			final CosmeticManager cosmetics = ev.getPlayer().getCosmetics();
			if (cosmetics.fb != null)
			{
				cosmetics.fb.delete();
				cosmetics.fb = null;
			}
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onTeamChange(final CPlayerTeamChangeEvent ev)
		{
			CPlayer.forEach(cp -> {
				final CosmeticManager cosmetics = cp.getCosmetics();

				cosmetics.updateTabNames();
				cosmetics.updateNameTags();
				cosmetics.updateScoreboard();
			});
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onKitChange(final CPlayerKitChangeEvent ev)
		{
			CPlayer.forEach(cp -> {
				final CosmeticManager cosmetics = cp.getCosmetics();

				cosmetics.updateTabNames();
				cosmetics.updateNameTags();
			});

			ev.getPlayer().getCosmetics().updateTablist();
			ev.getPlayer().getCosmetics().updateScoreboard();
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onDeath(final CPlayerDeathEvent ev)
		{
			CPlayer.forEach(cp -> {
				final CosmeticManager cosmetics = cp.getCosmetics();

				cosmetics.updateTabNames();
				cosmetics.updateNameTags();
				cosmetics.updateScoreboard();
			});
		}

		@EventHandler(priority = EventPriority.HIGHEST)
		public void onLocaleChange(final CPlayerSetLocaleEvent ev)
		{
			final CosmeticManager cosmetics = ev.getPlayer().getCosmetics();
			cosmetics.updateTabNames();
			cosmetics.updateNameTags();
			cosmetics.updateScoreboard();
			cosmetics.updateTablist();
		}
	}
}
