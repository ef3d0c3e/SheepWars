package org.ef3d0c3e.sheepwars.commands;

import com.google.common.collect.Lists;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CmdSheepWars extends Command {
    public CmdSheepWars()
    {
        super("sheepwars", "Plugin command", "/sheepwars", Arrays.asList("sw"));
        setPermission("sw.admin");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§cYou must be a player to execute this command.");
            return false;
        }

        final Player p = (Player)sender;
        final String category = args.length == 0 ? "help" : args[0];
        if (category.equals("help"))
        {
            sender.sendMessage(" - §ahelp §fDisplays this page");
            sender.sendMessage(" - §astart §fStarts the game");
            sender.sendMessage(" - §adebug §7<option> §fFor developers");
        }

        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String label, final @NotNull String[] args)
    {
        if (args.length == 0) return null;
        else if (args.length == 1)
        {
            return Lists.newArrayList(
                    "help",
                    "start",
                    "debug"
            );
        }
        else if (args[0].equals("debug"))
        {
            if (args.length == 2) return Lists.newArrayList("<poses>");
        }

        return null;
    }

}
