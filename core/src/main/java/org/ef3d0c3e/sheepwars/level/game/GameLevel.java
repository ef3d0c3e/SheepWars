package org.ef3d0c3e.sheepwars.level.game;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.game.Game;
import org.ef3d0c3e.sheepwars.level.Level;
import org.ef3d0c3e.sheepwars.maps.Map;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import static org.ef3d0c3e.sheepwars.teams.Team.BLUE;

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

    /**
     * Gets a spawnpoint in the map for a player
     * @param cp Player to get spawnpoint of
     * @return Spawnpoint of the player
     */
    public @NonNull Location getSpawnLocation(final @NonNull CPlayer cp)
    {
        if (cp.getTeam() == null)
            return this.getHandle().getSpawnLocation();
        final var spawns = cp.getTeam() == BLUE ? map.getBlueSpawns() : map.getRedSpawns();
        final var yaw = cp.getTeam() == BLUE ? map.getBlueYaw() : map.getRedYaw();
        final var loc = spawns.get(Game.nextInt(spawns.size()));

        return new Location(getHandle(), loc.getX(), loc.getY(), loc.getZ(), yaw, 0.f);
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
