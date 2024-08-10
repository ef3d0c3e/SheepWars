package org.ef3d0c3e.sheepwars;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.ef3d0c3e.sheepwars.commands.CommandFactory;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.ef3d0c3e.sheepwars.events.CPlayerJoinEvent;
import org.ef3d0c3e.sheepwars.events.EventListenerFactory;
import org.ef3d0c3e.sheepwars.game.Game;
import org.ef3d0c3e.sheepwars.level.VoidChunkGenerator;
import org.ef3d0c3e.sheepwars.locale.LocaleManager;
import org.ef3d0c3e.sheepwars.packets.PacketListenerFactory;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.versions.WrapperFactory;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class SheepWars extends JavaPlugin
{
    @Getter
    private static Plugin plugin;
    @Getter
    private static Config sheepWarsConfig;
    @Getter
    private static LocaleManager localeManager;

    @Override
    public void onLoad() {
        consoleMessage("--[ Setting up... ]--");
        // Extract resources
        if (!saveResources())
            return;

        // Load configurations
        sheepWarsConfig = new Config(new File(getDataFolder() + "/config.yml"));
        localeManager = new LocaleManager(new File(getDataFolder() + "/locales"));

        // Modify the server's config
        boolean needRestart = false;
        // server.properties
        {
            consoleMessage("Overwriting 'server.properties'...");

            final File properties = new File("server.properties");
            final StringBuffer buffer = new StringBuffer();
            try
            {
                // Modify properties line by line
                final BufferedReader reader = new BufferedReader(new FileReader(properties));
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    if (line.startsWith("level-name=world"))
                    {
                        needRestart = true;
                        buffer.append("level-name=" + sheepWarsConfig.LOBBY_WORLD);
                    }
                    else if (line.startsWith("allow-nether=true"))
                    {
                        buffer.append("allow-nether=false");
                        needRestart = true;
                    }
                    else
                        buffer.append(line);

                    buffer.append('\n');
                }
                reader.close();

                // Write buffer
                final FileOutputStream out = new FileOutputStream(properties);
                out.write(buffer.toString().getBytes());
                out.close();
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        {
            consoleMessage("Overwriting 'bukkit.yml'...");

            final File bukkit = new File("bukkit.yml");
            YamlConfiguration bukkitYml = new YamlConfiguration();

            // Modify using YML
            try
            {
                bukkitYml.load(bukkit);
                bukkitYml.set("settings.allow-end", false);

                if (bukkitYml.get("worlds." + sheepWarsConfig.LOBBY_WORLD + ".generator") == null)
                {
                    bukkitYml.set("worlds." + sheepWarsConfig.LOBBY_WORLD + ".generator", "SheepWars");
                    needRestart = true;
                }
                if (bukkitYml.get("worlds." + sheepWarsConfig.GAME_WORLD + ".generator") == null)
                {
                    bukkitYml.set("worlds." + sheepWarsConfig.GAME_WORLD + ".generator", "SheepWars");
                }

                bukkitYml.save(bukkit);
            }
            catch (IOException | InvalidConfigurationException e)
            {
                throw new RuntimeException(e);
            }
        }

        if (needRestart)
        {
            consoleMessage("Server restart needed. Please restart your server");
            Bukkit.shutdown();
        }

        // Resolve version wrappers for current Minecraft version
        try
        {
            WrapperFactory.resolveWrappers();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();

        consoleMessage("--[ Setup done ]--");
    }

    @Override
    public void onDisable() {
        PacketEvents.getAPI().terminate();
        consoleMessage("Plugin Disabled!");
    }

    @Override
    public void onEnable() {
        plugin = this;

        PacketEvents.getAPI().getSettings()
                .debug(true)
                .reEncodeByDefault(true);
        PacketEvents.getAPI().init();

        // Factories
        try {
            CommandFactory.registerCommands();
            EventListenerFactory.create();
            PacketListenerFactory.create();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        Game.init();

        // Useful when /reloading
        for (final Player p : Bukkit.getOnlinePlayers())
        {
            CPlayer.add(p);
        }
        CPlayer.forEach(cp -> {
            getServer().getPluginManager().callEvent(new CPlayerJoinEvent(cp, true));
        });

        consoleMessage("Plugin Enabled!");
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id)
    {
        return new VoidChunkGenerator();
    }


    /**
     * Writes message to console using plugin's prefix
     * @param message Message to write
     */
    public static void consoleMessage(final String message)
    {
        Bukkit.getConsoleSender().sendMessage("§8[§9§lSheepWars§8]:§7 " + message);
    }

    /**
     * Writes a debug message to the console
     * @param message Message to write
     */
    public static void debugMessage(final String message)
    {
        Bukkit.getConsoleSender().sendMessage("§d§lSW/Debug:§f " + message);
    }

    /**
     * Extract all resources from the jar into the 'plugins/SheepWars' directory
     */
    boolean saveResources()
    {
        if (getDataFolder().exists())
        {
            consoleMessage("Using resources from 'SheepWars'");
            return true;
        }

        // Create 'plugins/SheepWars'
        consoleMessage("Creating resource directory...");
        try {
            if (!getDataFolder().mkdirs())
                    return false;
        }
        catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }

        // Iterate over plugin's jar
        try
        {
            final JarFile jar = new JarFile(getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
            Bukkit.getConsoleSender().sendMessage(jar.toString());
            Enumeration<JarEntry> entries = (Enumeration<JarEntry>)jar.entries();
            while (entries.hasMoreElements())
            {
                JarEntry ent = entries.nextElement();
                if (!ent.getName().startsWith("exports/")) continue;
                Bukkit.getConsoleSender().sendMessage(ent.getName());
                final String stripped = ent.getName().substring("exports/".length());
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

                consoleMessage(MessageFormat.format("Created file ''{0}''.", file.getAbsolutePath()));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
