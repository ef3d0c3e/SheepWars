package org.ef3d0c3e.sheepwars.level.lobby;

import lombok.NonNull;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;
import org.bukkit.inventory.ItemStack;
import org.ef3d0c3e.sheepwars.events.WantsListen;
import org.ef3d0c3e.sheepwars.hologram.Hologram;
import org.ef3d0c3e.sheepwars.hologram.HologramComponent;
import org.ef3d0c3e.sheepwars.hologram.HologramItemComponent;
import org.ef3d0c3e.sheepwars.hologram.HologramTextComponent;
import org.ef3d0c3e.sheepwars.player.CPlayer;

public class LobbyHologram  extends Hologram {
    private static final int NETWORK_ID = 0xFF877710;
    private final Location location;

    public LobbyHologram(final Location location)
    {
        super(NETWORK_ID);
        this.location = location;

        // Center item
        addComponent(new HologramItemComponent(new Vector(0, 0, 0))
        {
            final ItemStack item = new ItemStack(Material.WHITE_WOOL);

            @Override
            protected @NonNull ItemStack getItem(@NonNull CPlayer cp)
            {
                return item;
            }
        });

        // Left item
        addComponent(new HologramItemComponent(new Vector(0.9, 0, 0))
        {
            final ItemStack item = new ItemStack(Material.IRON_SWORD);

            @Override
            protected @NonNull ItemStack getItem(@NonNull CPlayer cp)
            {
                return item;
            }
        });

        // Right item
        addComponent(new HologramItemComponent(new Vector(-0.9, 0, 0))
        {
            final ItemStack item = new ItemStack(Material.BOW);

            @Override
            protected @NonNull ItemStack getItem(@NonNull CPlayer cp)
            {
                return item;
            }
        });

        // Title
        addComponent(new HologramTextComponent(new Vector(0, 0.4, 0)) {
            final static Component title = Component.text("SheepWars")
                    .color(TextColor.color(140, 187, 64))
                    .decorate(TextDecoration.BOLD);

            @Override @NonNull
            protected Component getText(@NonNull CPlayer cp) {
                return title;
            }
        });
    }

    @Override
    public @NonNull Location getLocation(@NonNull CPlayer cp) {
        return location;
    }
}
