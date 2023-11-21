package org.ef3d0c3e.sheepwars;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.ef3d0c3e.sheepwars.commands.CommandRegisterer;
import org.ef3d0c3e.sheepwars.items.ItemRegistry;
import org.ef3d0c3e.sheepwars.level.Biomes;
import org.ef3d0c3e.sheepwars.level.Lobby;
import org.ef3d0c3e.sheepwars.level.SWChunkGenerator;
import org.ef3d0c3e.sheepwars.locale.LocaleManager;

import java.io.*;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class SheepWars extends JavaPlugin
{
	@Getter
	private static Plugin plugin;
	@Getter
	private static ProtocolManager protocolManager;
	@Getter
	private static String versionString;
	@Getter
	private static LocaleManager localeManager;
	@Getter
	private static ItemRegistry itemRegistry;

	private void saveResources()
	{
		if (getDataFolder().exists())
			return;

		// Create 'plugins/SheepWars'
		Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Creating configuration directory");
		getDataFolder().mkdirs();

		// Iterate over plugin's jar
		try
		{
			final JarFile jar = new JarFile(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
			Enumeration<JarEntry> entries = (Enumeration<JarEntry>)jar.entries();
			while (entries.hasMoreElements())
			{
				JarEntry ent = entries.nextElement();
				if (!ent.getName().startsWith("resources")) continue;
				final String stripped = ent.getName().substring("resources/".length());
				if (stripped.isEmpty()) continue;

				final File file = new File(getDataFolder() + "/" + stripped);
				if (!file.getName().contains(".")) // Directory
					continue;

				// File
				if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
				file.createNewFile();

				OutputStream out = new FileOutputStream(file);
				InputStream in = jar.getInputStream(ent);
				in.transferTo(out);
				out.close();
				in.close();

				Bukkit.getConsoleSender().sendMessage(MessageFormat.format("§cSheepWars>§7 Created file ''{0}''", file.getName()));
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onLoad()
	{
		// Attempt to erase world
		final World erase = Bukkit.getWorld("sheepwars");
		if (erase != null)
		{
			Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Deleting world 'sheepwars'");
			CPlayer.forEach(cp -> { if (cp.getHandle().getWorld() == erase) cp.getHandle().kickPlayer(cp.getLocale().SYSTEM_RELOAD); });
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

		Bukkit.getConsoleSender().sendMessage(getServer().getVersion());
		versionString = getServer().getVersion();
	}

	@Override
	public void onEnable()
	{
		saveResources();

		plugin = this;
		protocolManager = ProtocolLibrary.getProtocolManager();
		localeManager = new LocaleManager(new File(getDataFolder() + "/locales"));
		itemRegistry = new ItemRegistry();

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
				ping.setVersionName(MessageFormat.format("§d<§e1.20.2§d> §6» §l{0}/{1}", ping.getPlayersOnline(), ping.getPlayersMaximum()));

				//// Custom player list
				ping.setPlayers(Arrays.asList(
					new WrappedGameProfile(UUID.randomUUID(), "§6§l:§6v)")
				));

				ev.getPacket().getServerPings().write(0, ping);
			}
		});

		// Register all commands
		CommandRegisterer.registerCommands();

		Biomes.loadRegistry();
		Bukkit.getPluginManager().registerEvents(new Events(), plugin);
		Game.init();

		// Fire an onJoin event, useful when developing
		for (Player p : Bukkit.getOnlinePlayers())
		{
			PlayerJoinEvent ev = new PlayerJoinEvent(p, "");
			Bukkit.getPluginManager().callEvent(ev);
		}

		Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Plugin successfully enabled");
	}

	@Override
	public void onDisable()
	{
		if (Game.hasStarted())
		{
			// Teleport all players to spawn
			CPlayer.forEach(cp -> {
				cp.getHandle().teleport(Lobby.getWorld().getSpawnLocation());
				cp.getHandle().kickPlayer(cp.getLocale().SYSTEM_RELOAD);
				/* Kick players to avoid
				 * [Render thread/ERROR]: Error executing task on Client
				 * java.lang.IllegalArgumentException: No value with id <ID>
				 */
			});
		}

		final World world = Lobby.getWorld();
		if (world != null)
		{
			Bukkit.unloadWorld(world, false);
			Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Deleting player data");
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

		Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Plugin disabled.");
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
