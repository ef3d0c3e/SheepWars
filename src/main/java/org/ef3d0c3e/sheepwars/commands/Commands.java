package org.ef3d0c3e.sheepwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Sheep;
import org.ef3d0c3e.sheepwars.SheepWars;

import javax.annotation.processing.Completion;
import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter
{
	ArrayList<SWCommand> cmdList;

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		for (final SWCommand swcmd : cmdList)
		{
			if (!swcmd.getName().equals(cmd.getName()))
				continue;

			return swcmd.execute(sender, args);
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(final CommandSender sender, final Command cmd, final String label, final String[] args)
	{
		for (final SWCommand swcmd : cmdList)
		{
			if (!swcmd.getName().equals(cmd.getName()))
				continue;

			List<String> l = swcmd.completion(sender, args);
			if (args.length != 0)
			{
				final String arg = args[args.length - 1]; // Last

				// Remove non matching entries
				for (int i = 0; i < l.size(); )
				{
					// Entry too short
					if (arg.length() > l.get(i).length())
					{
						l.remove(i);
						continue;
					}

					// Entry does not match
					if (!l.get(i).substring(0, arg.length()).equalsIgnoreCase(arg))
					{
						l.remove(i);
						continue;
					}
					++i;
				}
			}

			return l;
		}

		return null;
	}

	/**
	 * Constructor
	 * @param cmds List of commands
	 */
	public Commands(final SWCommand... cmds)
	{
		cmdList = new ArrayList<>(cmds.length);
		for (final SWCommand cmd : cmds)
		{
			cmdList.add(cmd);

			((SheepWars)SheepWars.plugin).getCommand(cmd.getName()).setExecutor(this);
			((SheepWars)SheepWars.plugin).getCommand(cmd.getName()).setTabCompleter(this);
		}
	}
}
