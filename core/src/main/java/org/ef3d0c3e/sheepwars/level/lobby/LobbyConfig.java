package org.ef3d0c3e.sheepwars.level.lobby;

import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.config.AnyLocation;
import org.ef3d0c3e.sheepwars.config.BaseConfig;

import java.io.File;

public class LobbyConfig extends BaseConfig
{
    public LobbyConfig()
    {
        super(new File(SheepWars.getPlugin().getDataFolder() + "/lobby.yml"));
    }

    public Vector OFFSET;
    public AnyLocation SPAWN;
    public int LIMBO;
    public AnyLocation INFO;
    public AnyLocation SKIN;
    public AnyLocation TEAM;
    public AnyLocation VOTE;
    public AnyLocation KIT;
}

