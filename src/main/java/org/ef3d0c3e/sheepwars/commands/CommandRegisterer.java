package org.ef3d0c3e.sheepwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.help.GenericCommandHelpTopic;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.IndexHelpTopic;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class CommandRegisterer implements CommandExecutor, TabCompleter
{
	private static HashMap<String, Command> COMMANDS;
	static
	{
		COMMANDS = new HashMap<>();

		// Find all commands and populate COMMANDS
		Reflections refl = new Reflections("org.ef3d0c3e.sheepwars.commands");
		Set<Class<? extends Command>> classes = refl.getSubTypesOf(Command.class);

		for (Class C : classes)
		{
			try
			{
				final Command cmd;
				cmd = (Command)(C.getDeclaredConstructor().newInstance());
				COMMANDS.put(cmd.getName(), cmd);
			}
			catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * @brief Registers all commands
	 */
	public static void registerCommands()
	{
		final CommandRegisterer executor = new CommandRegisterer();

		// Get command map
		CommandMap map = null;
		try
		{
			Field cmdMap = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
			cmdMap.setAccessible(true);
			map = (CommandMap)cmdMap.get(Bukkit.getPluginManager());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// Register commands
		for (final Command cmd : COMMANDS.values())
			map.register(cmd.getName(), cmd);

		// Generate help
		List<HelpTopic> cmdHelp = new ArrayList<>();
		COMMANDS.forEach((name, cmd) -> cmdHelp.add(new GenericCommandHelpTopic(cmd)));

		Bukkit.getHelpMap().addTopic(
			new IndexHelpTopic(
				"Sheepwars",
				"Sheepwars is a minigame plugin",
				"sw.help",
				cmdHelp,
				"Minigame plugin"
			)
		);
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		final Command bcmd = COMMANDS.get(cmd.getName().toLowerCase());
		if (bcmd == null)
			return false;

		bcmd.execute(sender, label, args);
		return true;
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		final Command bcmd = COMMANDS.get(cmd.getName().toLowerCase());
		if (bcmd == null)
			return null;

		return bcmd.tabComplete(sender, label, args);
	}
}
