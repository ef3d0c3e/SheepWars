package org.ef3d0c3e.sheepwars;

import org.ef3d0c3e.sheepwars.config.BaseConfig;

import java.io.File;

public class Config extends BaseConfig
{
    public Config(File path)
    {
        super(path);
    }

    public String LOBBY_WORLD;
    public String GAME_WORLD;
}
