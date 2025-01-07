package org.ef3d0c3e.sheepwars.commands;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.maps.MapManager;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.sheeps.BaseSheep;
import org.ef3d0c3e.sheepwars.sheeps.sheep.ExplosiveSheep;
import org.jetbrains.annotations.NotNull;
import org.ef3d0c3e.sheepwars.game.Game;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
            sender.sendMessage(" - §awool §fGets a list of wools");
            sender.sendMessage(" - §adebug §7<option> §fFor developers");
        }
        else if (category.equals("debug"))
        {
            final var sh = new ExplosiveSheep(CPlayer.get(p));
            sh.spawn(p.getLocation(), true);

        }
        else if (category.equals("wools"))
        {
            final var inv = Bukkit.createInventory(null, 54, "All wools");
            final var cp = CPlayer.get(p);

            final Reflections refl = new Reflections("org.ef3d0c3e.sheepwars");

            // Classes
            final Set<Class<? extends BaseSheep>> classes = refl.getSubTypesOf(BaseSheep.class);
            for (final Class<? extends BaseSheep> clz : classes) {
                if (Modifier.isAbstract(clz.getModifiers()))
                    continue;
                try {
                    inv.addItem((ItemStack) clz.getDeclaredMethod("getItem", CPlayer.class).invoke(null, cp));
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }

            p.openInventory(inv);
        }
        else if (category.equals("start"))
        {
            final var map = MapManager.getVoteWinner();

            Game.start(map);
        }

        return true;
    }

    @Override
    public List<String> tabComplete(final @NotNull CommandSender sender, final @NotNull String label, final @NotNull String[] args)
    {
        if (args.length == 0) return null;
        else if (args.length == 1)
        {
            return Lists.newArrayList(
                    "help",
                    "start",
                    "wools",
                    "debug"
            );
        }
        else if (args[0].equals("debug"))
        {
            if (args.length == 2) return Lists.newArrayList("<map>");
        }

        return List.of();
    }

}
