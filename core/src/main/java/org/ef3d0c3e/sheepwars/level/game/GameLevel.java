package org.ef3d0c3e.sheepwars.level.game;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.level.Level;
import org.ef3d0c3e.sheepwars.maps.Map;

public class GameLevel extends Level {
    @Getter
    final Map map;

    public GameLevel(final Map map)
    {
        super(SheepWars.getSheepWarsConfig().GAME_WORLD);
        this.map = map;
    }

    @Override
    protected @NonNull World generate() throws Exception {
        return map.generate();
    }

    @Override
    protected void onLoad(Chunk chunk, boolean newChunk) {
        getHandle().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        getHandle().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        getHandle().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
        getHandle().setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        getHandle().setGameRule(GameRule.DO_MOB_SPAWNING, false);
        getHandle().setGameRule(GameRule.DO_INSOMNIA, false);
        getHandle().setGameRule(GameRule.SPAWN_RADIUS, 0);
        getHandle().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        getHandle().setGameRule(GameRule.NATURAL_REGENERATION, false);
        getHandle().setGameRule(GameRule.DO_TILE_DROPS, false);
        getHandle().setTime(map.getWorldTime());
        getHandle().setWeatherDuration(0);
        // TODO: Request a spectator spawn location in the config
        getHandle().setSpawnLocation(0, getHandle().getHighestBlockYAt(0, 0), 0);
    }
}
