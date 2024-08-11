package org.ef3d0c3e.sheepwars;

import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
    //static private UUID PROFILE_UUID = UUID.randomUUID();
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
}
