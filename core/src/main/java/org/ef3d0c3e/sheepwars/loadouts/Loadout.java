package org.ef3d0c3e.sheepwars.loadouts;

import io.github.retrooper.packetevents.adventure.serializer.legacy.LegacyComponentSerializer;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Loadout for players
 */
public abstract class Loadout {
    @Getter
    final private @NonNull CPlayer player;

    public Loadout(final @NonNull CPlayer player) { this.player = player; }

    /**
     * Applies the loadout to a player
     * @param cp The player to apply the loadout to
     */
    public void apply(final @NonNull CPlayer cp) {
        final var inv = cp.getHandle().getInventory();

        final var armor = armor();
        inv.setArmorContents(armor.toArray(new ItemStack[0]));

        inv.setItem(0, sword());
        inv.setItem(1, bow());

        final var items = items();
        for (final var item : items) {
            inv.addItem(item);
        }
    }

    public @NonNull List<@NonNull ItemStack> armor() {
        final var ser = LegacyComponentSerializer.legacy('§');
        final var color = Color.fromRGB(player.getTeam().getColor().value());
        final List<ItemStack> armor = new ArrayList<>();

        // Boots
        {
            final var item = new ItemStack(Material.LEATHER_BOOTS);
            final var meta = (LeatherArmorMeta)item.getItemMeta();
            meta.setUnbreakable(true);
            meta.setColor(color);
            meta.setDisplayName(ser.serialize(Component.text(player.getLocale().LOADOUT_ARMOR_BOOTS).color(player.getTeam().getColor())));
            item.setItemMeta(meta);
            armor.add(item);
        }

        // Leggings
        {
            final var item = new ItemStack(Material.LEATHER_LEGGINGS);
            final var meta = (LeatherArmorMeta)item.getItemMeta();
            meta.setUnbreakable(true);
            meta.setColor(color);
            meta.setDisplayName(ser.serialize(Component.text(player.getLocale().LOADOUT_ARMOR_LEGGINGS).color(player.getTeam().getColor())));
            item.setItemMeta(meta);
            armor.add(item);
        }

        // Body
        {
            final var item = new ItemStack(Material.LEATHER_CHESTPLATE);
            final var meta = (LeatherArmorMeta)item.getItemMeta();
            meta.setUnbreakable(true);
            meta.setColor(color);
            meta.setDisplayName(ser.serialize(Component.text(player.getLocale().LOADOUT_ARMOR_BODY).color(player.getTeam().getColor())));
            item.setItemMeta(meta);
            armor.add(item);
        }

        // Helmet
        {
            final var item = new ItemStack(Material.LEATHER_HELMET);
            final var meta = (LeatherArmorMeta)item.getItemMeta();
            meta.setUnbreakable(true);
            meta.setColor(color);
            meta.setDisplayName(ser.serialize(Component.text(player.getLocale().LOADOUT_ARMOR_HEAD).color(player.getTeam().getColor())));
            item.setItemMeta(meta);
            armor.add(item);
        }

        return armor;
    }

    public @NonNull ItemStack sword() {
        final var ser = LegacyComponentSerializer.legacy('§');

        final var item = new ItemStack(Material.WOODEN_SWORD);
        final var meta = item.getItemMeta();
        meta.setUnbreakable(true);
        meta.setDisplayName(ser.serialize(Component.text(player.getLocale().LOADOUT_SWORD).color(TextColor.color(120, 40, 200))));
        item.setItemMeta(meta);

        return item;
    }

    public @NonNull ItemStack bow() {
        final var ser = LegacyComponentSerializer.legacy('§');

        final var item = new ItemStack(Material.BOW);
        final var meta = item.getItemMeta();
        meta.setUnbreakable(true);
        meta.setDisplayName(ser.serialize(Component.text(player.getLocale().LOADOUT_BOW).color(TextColor.color(180, 60, 100))));
        item.setItemMeta(meta);

        return item;
    }

    public int arrowCount() { return 3; }

    public @NonNull List<ItemStack> items() { return List.of(); };

}
