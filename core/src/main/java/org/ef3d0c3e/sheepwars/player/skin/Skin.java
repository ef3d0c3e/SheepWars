package org.ef3d0c3e.sheepwars.player.skin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.ef3d0c3e.sheepwars.player.CPlayer;
import org.ef3d0c3e.sheepwars.versions.AutoWrapper;
import org.ef3d0c3e.sheepwars.versions.SkinVersionWrapper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Skin {
    @AutoWrapper(name = "Skin")
    public static SkinVersionWrapper WRAPPER;

    @Getter
    private String name;
    @Getter
    private ItemStack displayItem;
    @Getter
    private String texture;
    @Getter
    private String signature;

    public Skin(final String texture, final String signature, final String name) {
        this.name = name;
        this.displayItem = new ItemStack(Material.PLAYER_HEAD);
        final SkullMeta meta = (SkullMeta) displayItem.getItemMeta();
        meta.setDisplayName("§9" + name);

        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", texture));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
            displayItem.setItemMeta(meta);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        this.texture = texture;
        this.signature = signature;
    }

    /**
     * Gets skin as property
     *
     * @return Skin as property
     */
    public Property toProperty() {
        return new Property("textures", texture, signature);
    }

    /**
     * Gets current skin of player
     *
     * @param cp Player to get skin of
     * @return Skin of player
     */
    public static Skin fromPlayer(final @NonNull CPlayer cp) {
        return WRAPPER.fromPlayer(cp);
    }

    /**
     * Changes player skin
     *
     * @param cp Player to change the skin of
     */
    public static void updateSkin(final @NonNull CPlayer cp) {
        WRAPPER.updateSkin(cp);
    }

    protected static List<Skin> skinList = new ArrayList<>();
}