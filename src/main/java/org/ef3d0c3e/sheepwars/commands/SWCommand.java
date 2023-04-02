package org.ef3d0c3e.sheepwars.commands;

import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public abstract class SWCommand
{
	abstract public String getName();
	abstract public boolean execute(final CommandSender sender, final String[] args);
	public List<String> completion(final CommandSender sender, final String[] args)
	{
		return new ArrayList<>();
	}
}
