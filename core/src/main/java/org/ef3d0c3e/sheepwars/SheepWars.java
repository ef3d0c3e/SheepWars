package org.ef3d0c3e.sheepwars;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SheepWars extends JavaPlugin
{
    @Override
    public void onLoad() {
    }

    @Override
    public void onDisable() {
        Bukkit.getServer().getConsoleSender().sendMessage("§8[§aSheepWars§8]:§7 Plugin disabled");
    }

    @Override
    public void onEnable() {
        Bukkit.getServer().getConsoleSender().sendMessage("§8[§aSheepWars§8]:§7 Plugin enabled!");
    }
}
