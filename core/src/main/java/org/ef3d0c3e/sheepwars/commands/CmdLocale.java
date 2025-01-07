package org.ef3d0c3e.sheepwars.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CmdLocale extends Command {
    public CmdLocale()
    {
        super("locale", "Change locale", "/locale", List.of());
        setPermission("sw.locale");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player))
        {
            sender.sendMessage("§cYou must be a player to execute this command.");
            return false;
        }

        final Player p = (Player)sender;
        if (args.length == 0)
        {
            // TODO: Open menu
            sender.sendMessage("USAGE: /locale <LOCALE NAME>");
        }

        /* TODO
        final OldLocale loc = SheepWars.getOldLocaleManager().getByName(args[0]);
        if (loc == null)
        {
            sender.sendMessage("§cUnknown locale '" + args[0] + "'.");
            return false;
        }
        final CPlayer cp = CPlayer.get((Player)sender);
        cp.setOldLocale(loc);
         */

        return true;
    }

    @Override
    public List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String label, final @NotNull String[] args)
    {
        return List.of();
    }

}
