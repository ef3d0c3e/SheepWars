package org.ef3d0c3e.sheepwars.commands;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.CPlayer;
import org.ef3d0c3e.sheepwars.Game;
import org.ef3d0c3e.sheepwars.Util;
import org.ef3d0c3e.sheepwars.level.Map;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.Sheeps;
import oshi.util.tuples.Pair;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CmdSW extends Command
{
	String map = null; ///< Currently selected map

	public CmdSW()
	{
		super("sheepwars", "Main plugin command", "/sheepwars", Arrays.asList("sw"));
		setPermission("sheepwars.admin");
		setPermissionMessage("§cSheepWars§8>§7 Vous n'avez pas la permission.");
	}


	@Override
	public boolean execute(final CommandSender sender, final String label, final String[] args)
	{
		String category;
		if (args.length != 0)
			category = args[0];
		else
			category = "help";

		if (category.equals("help"))
		{
			sender.sendMessage("§8§m §m §m §m §m §m §m §e SheepWars §8§m §m §m §m §m §m §m ");
			sender.sendMessage(" §7§l╸§r §amapinfo <nom>§r Affiche des informations sur une map");
			sender.sendMessage(" §7§l╸§r §aspawn <mouton>§r Fait spawn un mouton");
			sender.sendMessage(" §7§l╸§r §awool §r Vous donnes les laines");
			sender.sendMessage(" §7§l╸§r §astart§r Lance la partie");
			sender.sendMessage("§8§m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m §m ");
		}
		else if (category.equals("mapinfo"))
		{
			if (args.length != 2)
			{
				Util.errorMessage(sender, "§7Veuillez spécifier le nom d'une carte");
				return true;
			}

			final Map map = Map.getMaps().get(args[1]);
			if (map == null)
			{
				Util.errorMessage(sender, MessageFormat.format("§7La carte ''{0}'' n'existe pas", args[1]));
				return true;
			}

			sender.sendMessage(MessageFormat.format("§7 - §fNom: §a{0}", map.getDisplayName()));
			sender.sendMessage(MessageFormat.format("§7 - §fOffset: §a{0}", map.getOffset()));
			sender.sendMessage("§7 - §fSpawns Rouge:");
			for (final Vector spawn : map.getRedSpawns())
				sender.sendMessage(MessageFormat.format("§7 §7 - §a{0}", spawn));
			sender.sendMessage("§7 - §fSpawns Bleu:");
			for (final Vector spawn : map.getBlueSpawns())
				sender.sendMessage(MessageFormat.format("§7 §7 - §a{0}", spawn));
		}
		else if (category.equals("spawn"))
		{
			if (!(sender instanceof Player))
			{
				Util.errorMessage(sender, "§7Vous devez être un joueur!");
				return true;
			}
			final CPlayer cp = CPlayer.getPlayer((Player)sender);
			if (args.length != 2)
			{
				Util.errorMessage(sender, "§7Veuillez spécifier le nom d'un mouton");
				return true;
			}

			Class<? extends BaseSheep> SheepClass = Sheeps.list.get(args[1]).getB();
			if (SheepClass == null)
			{
				Util.errorMessage(sender, MessageFormat.format("§7Le mouton ''{0}'' n'existe pas", args[1]));
				return true;
			}
			try
			{
				BaseSheep sheep = (BaseSheep)SheepClass.getDeclaredConstructor(Location.class, CPlayer.class).newInstance(cp.getHandle().getLocation(), cp);
				sheep.spawn();
			}
			catch (InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
			}

		}
		else if (category.equals("wools"))
		{
			if (!(sender instanceof Player))
			{
				Util.errorMessage(sender, "§7Vous devez être un joueur!");
				return true;
			}

			Inventory inv = Bukkit.createInventory(null, 54, "§9Laines");
			for (Pair<ItemStack, Class<? extends BaseSheep>> pair : Sheeps.list.values())
			{
				try
				{
					inv.addItem((ItemStack) pair.getB().getDeclaredMethod("getItem").invoke(null));
				}
				catch (NoSuchMethodException e)
				{
					e.printStackTrace();
				}
				catch (InvocationTargetException e)
				{
					e.printStackTrace();
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
			((Player)sender).openInventory(inv);
		}
		else if (category.equals("start"))
		{
			if (Game.hasStarted())
			{
				Util.errorMessage(sender, "§7 La partie a déjà commencée!");
				return true;
			}

			int maxVote = 0;
			for (final Map map : Map.getMaps().values())
				maxVote = Math.max(map.getVote(), maxVote);
			final ArrayList<Map> chosen = new ArrayList<>();
			for (final Map map : Map.getMaps().values())
				if (map.getVote() == maxVote)
					chosen.add(map);
			final Map map = chosen.get(Game.nextInt() % chosen.size());

			Bukkit.broadcastMessage(MessageFormat.format("§8'{'§6☀§8'}' §7Carte choisie: §a{0}", map.getDisplayName()));

			Game.start(map);
		}

		return true;
	}

	@Override
	public List<String> tabComplete(final CommandSender sender, final String label, final String[] args)
	{
		ArrayList<String> l = new ArrayList<>();
		if (args.length == 1)
		{
			l.add("help");
			l.add("mapinfo");
			l.add("spawn");
			l.add("wools");
			l.add("start");
		}
		else if (args.length == 2 && args[0].equals("mapinfo"))
		{
			for (final Map map : Map.getMaps().values())
				l.add(map.getName());
		}
		else if (args.length == 2 && args[0].equals("spawn"))
		{
			for (final String name : Sheeps.list.keySet())
				l.add(name);
		}

		return l;
	}
}
