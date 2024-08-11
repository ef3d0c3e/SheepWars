package org.ef3d0c3e.sheepwars.maps;

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
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.SheepWars;
import org.ef3d0c3e.sheepwars.level.VoidChunkGenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Map {
    @Getter
    String name; /// Map's internal name
    @Getter
    protected String displayName; /// Map's display name (unlocalized)
    @Getter
    protected Material icon; /// Map's display icon
    @Getter
    protected File schematic; /// Map's schematic file
    @Getter
    protected Clipboard mapContent; /// Map's schematic contnet

    @Getter
    protected int nbVote; /// Number of votes for this map

    @Getter
    protected Vector offset;
    @Getter
    protected Vector lowestPoint;
    @Getter
    protected Vector highestPoint;
    @Getter
    protected int limboBegin;
    @Getter
    protected int limboEnd;
    @Getter
    protected int worldTime;
    @Getter
    protected Vector lowestWool;
    @Getter
    protected Vector highestWool;
    @Getter
    protected ArrayList<Vector> redSpawns;
    @Getter
    protected float redYaw;
    @Getter
    protected ArrayList<Vector> blueSpawns;
    @Getter
    protected float blueYaw;

    /**
     * Generate map in world
     * @return World the map is in
     */
    public World generate()
    {
        try
        {
            //final World erase = Bukkit.getWorld("sheepwars");
            //if (erase != null)
            //{
            //	Bukkit.getConsoleSender().sendMessage("§cSheepWars>§7 Suppression du monde 'sheepwars'");
            //	Bukkit.getServer().unloadWorld(erase, false);
            //	FileUtils.deleteDirectory(erase.getWorldFolder());
            //}

            SheepWars.consoleMessage("Creating game world...");
            final WorldCreator wc = new WorldCreator(SheepWars.getSheepWarsConfig().GAME_WORLD);
            wc.generator(new VoidChunkGenerator());
            final World world = wc.createWorld();

            ClipboardFormat format = ClipboardFormats.findByFile(schematic);
            ClipboardReader reader = format.getReader(new FileInputStream(schematic));
            mapContent = reader.read();

            com.sk89q.worldedit.world.World adaptedWorld = BukkitAdapter.adapt(world);
            EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld);
            Operation operation = new ClipboardHolder(mapContent).createPaste(editSession)
                    .to(BlockVector3.at(offset.getX(), offset.getY()+64, offset.getZ()))
                    .ignoreAirBlocks(true)
                    .build();

            lowestPoint = new Vector(
                    mapContent.getOrigin().x() - mapContent.getMaximumPoint().x() + offset.getX(),
                    mapContent.getMinimumPoint().y() - mapContent.getOrigin().y() + offset.getY() + 64,
                    mapContent.getOrigin().z() - mapContent.getMaximumPoint().z() + offset.getZ()+1
            );
            highestPoint = new Vector(
                    mapContent.getOrigin().x() - mapContent.getMinimumPoint().x() + offset.getX()-1,
                    lowestPoint.getY() + mapContent.getDimensions().y() - 1,
                    mapContent.getOrigin().z() - mapContent.getMinimumPoint().z() + offset.getZ()
            );

            Operations.complete(operation);
            editSession.close();

            return world;
        }
        catch (IOException | WorldEditException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
