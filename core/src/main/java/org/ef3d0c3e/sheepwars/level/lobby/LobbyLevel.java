package org.ef3d0c3e.sheepwars.level.lobby;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.*;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.hologram.HologramFactory;
import org.ef3d0c3e.sheepwars.level.Level;
import org.ef3d0c3e.sheepwars.level.VoidBiomeProvider;
import org.ef3d0c3e.sheepwars.level.VoidChunkGenerator;
import org.ef3d0c3e.sheepwars.maps.VoteNPC;
import org.ef3d0c3e.sheepwars.npc.NPCFactory;
import org.ef3d0c3e.sheepwars.player.skin.SkinNPC;
import org.ef3d0c3e.sheepwars.teams.TeamNPC;

import java.io.*;

public class LobbyLevel extends Level
{
    @Getter
    private final LobbyConfig config;
    @Getter
    private Location spawn;

    //@Getter
    private LobbyHologram hologram;
    private SkinNPC skinNpc;
    private TeamNPC teamNpc;
    private VoteNPC voteNpc;
    //private KitNPC kitNpc;

    /**
     * Constructor
     */
    public LobbyLevel()
    {
        super(SheepWars.getSheepWarsConfig().LOBBY_WORLD);
        config = new LobbyConfig();
    }

    @Override
    protected void postWorld()
    {
        spawn = config.SPAWN.getLocation(getHandle());

        hologram = new LobbyHologram(config.INFO.getLocation(getHandle()));
        HologramFactory.register(hologram);

        skinNpc = new SkinNPC(config.SKIN.getLocation(getHandle()));
        NPCFactory.register(skinNpc);

        teamNpc = new TeamNPC(config.TEAM.getLocation(getHandle()));
        NPCFactory.register(teamNpc);

        voteNpc = new VoteNPC(config.VOTE.getLocation(getHandle()));
        NPCFactory.register(voteNpc);

        /*
        kitNpc = new KitNPC(config.KIT.getLocation(getHandle()));
        NPCFactory.register(kitNpc);
        */
    }

    @Override
    protected @NonNull World generate()
    {
        final WorldCreator creator = new WorldCreator(getWorldName())
                .generator(new VoidChunkGenerator())
                .biomeProvider(new VoidBiomeProvider())
                .seed(0)
                .environment(World.Environment.NORMAL)
                .generateStructures(false)
                .keepSpawnInMemory(false)
                .type(WorldType.NORMAL);

        return Bukkit.createWorld(creator);
    }

    @Override
    protected void onLoad(final Chunk chunk, boolean newChunk)
    {
        if (!newChunk ||
                chunk.getX() != Math.round(config.SPAWN.getX()/16) ||
                chunk.getZ() != Math.round(config.SPAWN.getZ()/16))
            return;

        // Paste schematic
        SheepWars.consoleMessage("Pasting lobby schematic...");
        try
        {
            File lobby = new File(SheepWars.getPlugin().getDataFolder().getAbsolutePath() + "/lobby.schem");
            ClipboardFormat format = ClipboardFormats.findByFile(lobby);
            ClipboardReader reader = format.getReader(new FileInputStream(lobby));
            Clipboard clipboard = reader.read();

            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(getHandle());
            EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld);
            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession)
                    .to(BlockVector3.at(config.OFFSET.getX(), config.OFFSET.getY(), config.OFFSET.getZ()))
                    .ignoreAirBlocks(true)
                    .build();

            Operations.complete(operation);
            editSession.close();

            // Biome TODO
            //final Location origin = new Location(ev.getWorld(), config.OFFSET.getX(), config.OFFSET.getY()+64, config.OFFSET.getZ());
            //Biomes.setBiome(origin, clipboard, Biomes.lobbyBiome);
        }
        catch (IOException | WorldEditException e)
        {
            e.printStackTrace();
        }
        SheepWars.consoleMessage("Lobby generated!");

        getHandle().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        getHandle().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        getHandle().setGameRule(GameRule.SEND_COMMAND_FEEDBACK, false);
        getHandle().setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
        getHandle().setGameRule(GameRule.DO_MOB_SPAWNING, false);
        getHandle().setGameRule(GameRule.DO_INSOMNIA, false);
        getHandle().setGameRule(GameRule.SPAWN_RADIUS, 0);
        getHandle().setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        getHandle().setTime(6000);
        getHandle().setWeatherDuration(0);
        getHandle().setSpawnLocation((int)config.SPAWN.getX(), (int)config.SPAWN.getY(), (int)config.SPAWN.getZ());
    }
}

