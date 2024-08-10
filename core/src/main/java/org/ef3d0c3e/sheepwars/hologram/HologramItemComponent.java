package org.ef3d0c3e.sheepwars.hologram;

import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.packets.EntityMetadata;
import org.ef3d0c3e.sheepwars.packets.ItemProjectileMetadata;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Represent a text component
 */
public abstract class HologramItemComponent extends HologramComponent
{
    protected HologramItemComponent(@NonNull Vector offset)
    {
        super(offset);
    }

    protected abstract @NonNull ItemStack getItem(final @NonNull CPlayer cp);

    protected int getNetworkOffset() { return 1; }

    protected @NonNull List<PacketWrapper<?>> build(final @NonNull Location location, final int networkId, final @NonNull CPlayer cp)
    {
        // Spawn
        final Location loc = location.clone().add(getOffset());
        final WrapperPlayServerSpawnEntity spawn = new WrapperPlayServerSpawnEntity(
                networkId, Optional.of(UUID.randomUUID()),
                EntityTypes.ITEM,
                new Vector3d(loc.getX(), loc.getY(), loc.getZ()),
                loc.getYaw(), 0.f, loc.getPitch(),
                0,
                Optional.empty()
        );

        // Metadata
        final WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata(
                networkId,
                Arrays.asList(
                        new EntityMetadata.NoGravity(true).into(),
                        new EntityMetadata.Silent(true).into(),
                        new ItemProjectileMetadata.Item(getItem(cp)).into()
                )
        );


        return List.of(spawn, meta);
    }
}
