package org.ef3d0c3e.sheepwars;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.FileUtil;
import org.ef3d0c3e.sheepwars.commands.CmdChangelog;
import org.ef3d0c3e.sheepwars.commands.CmdSW;
import org.ef3d0c3e.sheepwars.commands.Commands;
import org.ef3d0c3e.sheepwars.level.Biomes;
import org.ef3d0c3e.sheepwars.level.Lobby;
import org.ef3d0c3e.sheepwars.level.Map;
import org.ef3d0c3e.sheepwars.level.SWChunkGenerator;
import org.ef3d0c3e.sheepwars.sheeps.Sheeps;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.UUID;

public final class SheepWars extends JavaPlugin
{
	public static Plugin plugin;
	public static Server server;
	public static ProtocolManager protocolManager;

	@Override
	public void onLoad()
	{
		// Attempt to erase world
		final World erase = Bukkit.getWorld("sheepwars");
		if (erase != null)
		{
			Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Suppression du monde 'sheepwars'");
			for (final Player p : erase.getPlayers())
				p.kickPlayer("§eReconnectez-vous");
			Bukkit.getServer().unloadWorld(erase, false);
			try
			{
				FileUtils.deleteDirectory(erase.getWorldFolder());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onEnable()
	{
		plugin = this;
		server = getServer();
		protocolManager = ProtocolLibrary.getProtocolManager();

		// Motd
		protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Status.Server.SERVER_INFO)
		{
			@Override
			public void onPacketSending(PacketEvent ev)
			{
				WrappedServerPing ping = ev.getPacket().getServerPings().read(0);

				// Motd
				String motd = "  §e§lSheepWars";
				if (!Game.hasStarted())
					motd += " §aEn attente";
				ping.setMotD(motd); //TODO: Display mapname

				// Version
				ping.setVersionProtocol(999); // Will display our custom version name
				ping.setVersionName(MessageFormat.format("§d<§e1.18+§d> §6» §l{0}/{1}", ping.getPlayersOnline(), ping.getPlayersMaximum()));

				//// Custom player list
				ping.setPlayers(Arrays.asList(
					new WrappedGameProfile(UUID.randomUUID(), "§6§l:§6v)")
				));

				ev.getPacket().getServerPings().write(0, ping);
			}
		});

		// Misc
		Misc.init();

		if (!getDataFolder().exists())
		{
			Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Création du dossier de configuration!");
			getDataFolder().mkdirs();
		}
		// TODO: Iterate over all resources
		saveResource("sw_lobby.schem", false);

		if (!Map.getMapFolder().exists())
		{

			Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Création du dossier des maps!");
			Map.getMapFolder().mkdirs();
		}

		// Commands
		new Commands(
			new CmdSW(),
			new CmdChangelog()
		);

		Biomes.loadBiomes();
		Bukkit.getPluginManager().registerEvents(new Events(), plugin);
		Game.init();

		// Fire an onJoin event, useful when developing
		for (Player p : Bukkit.getOnlinePlayers())
		{
			PlayerJoinEvent ev = new PlayerJoinEvent(p, "");
			Bukkit.getPluginManager().callEvent(ev);
		}

		Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Plugin activé!");
	}

	@Override
	public void onDisable()
	{
		if (Game.hasStarted())
		{
			// Teleport all players to spawn
			for (final Player p : Bukkit.getOnlinePlayers())
			{
				p.teleport(Game.getLobby().getSpawnLocation());
				p.kickPlayer("§eLe serveur reload...");
				/* Kick players to avoid
				 * [Render thread/ERROR]: Error executing task on Client
				 * java.lang.IllegalArgumentException: No value with id <ID>
				 */
			}
		}

		final World world = Bukkit.getWorld("world");
		if (world != null)
		{
			Bukkit.unloadWorld(world, false);
			Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Suppression des données des joueurs");
			try
			{
				final File pdata = new File(world.getWorldFolder().getAbsolutePath() + "/playerdata");
				final File stats = new File(world.getWorldFolder().getAbsolutePath() + "/stats");
				final File adv = new File(world.getWorldFolder().getAbsolutePath() + "/advancements");
				if (FileUtils.isDirectory(pdata))
					FileUtils.cleanDirectory(pdata);
				if (FileUtils.isDirectory(stats))
					FileUtils.cleanDirectory(stats);
				if (FileUtils.isDirectory(adv))
					FileUtils.cleanDirectory(adv);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		protocolManager.removePacketListeners(plugin);

		Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Plugin désactivé!");
	}

	/**
	 * Use void generator
	 */
	@Override
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
		return new SWChunkGenerator();
	}
}
