package org.ef3d0c3e.sheepwars.hologram;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import com.github.retrooper.packetevents.protocol.entity.type.EntityTypes;
import com.github.retrooper.packetevents.util.Vector3d;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityMetadata;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnEntity;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSpawnLivingEntity;
import lombok.NonNull;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.ef3d0c3e.sheepwars.packets.EntityMetadata;
import org.ef3d0c3e.sheepwars.player.CPlayer;

import java.util.*;

/**
 * Represent a text component
 */
public abstract class HologramTextComponent extends HologramComponent
{
    protected HologramTextComponent(@NonNull Vector offset)
    {
        super(offset);
    }

    protected abstract @NonNull Component getText(final @NonNull CPlayer cp);

    protected int getNetworkOffset() { return 1; }

    protected @NonNull List<PacketWrapper<?>> build(final @NonNull Location location, final int networkId, final @NonNull CPlayer cp)
    {
        // Spawn
        final Location loc = location.clone().add(getOffset());
        final WrapperPlayServerSpawnEntity spawn = new WrapperPlayServerSpawnEntity(
                networkId, Optional.of(UUID.randomUUID()),
                EntityTypes.ARMOR_STAND,
                new Vector3d(loc.getX(), loc.getY(), loc.getZ()),
                loc.getYaw(), 0.f, loc.getPitch(),
                0,
                Optional.empty()
        );
        // Metadata
        final WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata(
                networkId,
                Arrays.asList(
                        new EntityMetadata.Status()
                                .isInvisible(true)
                                .into(),
                        new EntityMetadata.NoGravity(true).into(),
                        new EntityMetadata.CustomNameVisible(true).into(),
                        new EntityMetadata.CustomName(getText(cp)).into()
                )
        );


        return List.of(spawn, meta);
    }
}
