package org.ef3d0c3e.sheepwars;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class Util {
    public static ArrayList<String> coloredLore(String color, List<String> lore)
    {
        ArrayList<String> colored = new ArrayList<>();
        for (String line : lore)
        {
            colored.add(color + line);
        }
        return colored;
    }


    /**
     * Gets a game profile associated with a texture (unsigned)
     * @param texture Profile's texture
     * @return Created profile
     */
    private static @NonNull PlayerProfile getProfile(final @NonNull String texture)
    {
        final PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        final PlayerTextures textures = profile.getTextures();

        final String decoded = new String(Base64.getDecoder().decode(texture));
        try
        {
            final URL urlObject = new URL(decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length()));
            textures.setSkin(urlObject);
            profile.setTextures(textures);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        return profile;
    }

    /**
     * Creates skull with custom meta and texture
     * @param name Skull name
     * @param lore Skull lore (null for none)
     * @param texture Skull texture
     * @return Created skull
     */
    public static @NonNull ItemStack createSkull(final @NonNull String name, final @Nullable List<String> lore, final @NonNull String texture)
    {
        final ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)skull.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);

        meta.setOwnerProfile(getProfile(texture));
        skull.setItemMeta(meta);

        return skull;
    }

    /**
     * Parses string to vector
     * @param text Text in the form ".5 7.5 -10"
     * @return Vector
     */
    public static Vector parseVector(final String text)
    {
        String[] split = text.split(" ", 3);

        return new Vector(Double.valueOf(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]));
    }

    /**
     * Operation to execute around a point
     */
    public interface RunInCircle
    {
        void operation(final Location loc, final double t, final int i);
    }

    /**
     * Execute operation in circle
     * @param center Circle's center
     * @param normal Circle's normal
     * @param radius Circle's radius
     * @param points Number of points to run it at
     * @param f Callback
     */
    public static void runInCircle(final Location center, final Vector normal, final double radius, final int points, final RunInCircle f)
    {
        Vector u = new Vector(1, 0, 0);
        if (normal.dot(u) != 0)
            u = new Vector(0, 1, 0);

        final Vector a = normal.clone().crossProduct(u);
        final Vector b = normal.clone().crossProduct(a);

        for (int i = 0; i < points; ++i)
        {
            final double t = Math.PI * 2 * Double.valueOf(i) / Double.valueOf(points);

            final Location loc = center.clone().add(
                    radius * Math.cos(t) * a.getX() + radius * Math.sin(t) * b.getX(),
                    radius * Math.cos(t) * a.getY() + radius * Math.sin(t) * b.getY(),
                    radius * Math.cos(t) * a.getZ() + radius * Math.sin(t) * b.getZ());

            f.operation(loc, t, i);
        }
    }

    /**
     * Operation to execute around a point
     */
    public interface RunInSphereBlock
    {
        void operation(final Block b);
    }

    /**
     * Executes operation over a block-perfect sphere
     * @param center Center block
     * @param radius Radius of the sphere
     * @param f Operation to execute
     */
    public static void runInSphereBlock(final Location center, final double radius, final RunInSphereBlock f)
    {
        final World world = center.getWorld();
        final double radiusSq = radius * radius;

        final int xMin = (int)((double)center.getBlockX() + 0.5 - radius);
        final int xMax = (int)((double)center.getBlockX() + 0.5 + radius);
        final int yMin = (int)((double)center.getBlockY() + 0.5 - radius);
        final int yMax = (int)((double)center.getBlockY() + 0.5 + radius);
        final int zMin = (int)((double)center.getBlockZ() + 0.5 - radius);
        final int zMax = (int)((double)center.getBlockZ() + 0.5 + radius);

        for (int x = xMin - center.getBlockX(); x <= xMax - center.getBlockX(); ++x)
        {
            for (int y = yMin - center.getBlockY(); y <= yMax - center.getBlockY(); ++y)
            {
                if (x * x + y * y > radiusSq)
                    continue;

                for (int z = zMin - center.getBlockZ(); z <= zMax - center.getBlockZ(); ++z)
                {
                    if (x * x + y * y + z * z > radiusSq)
                        continue;

                    f.operation(world.getBlockAt(
                            center.getBlockX() + x,
                            center.getBlockY() + y,
                            center.getBlockZ() + z));
                }
            }
        }
    }

}
