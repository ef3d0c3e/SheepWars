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

import java.util.*;

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

    @Override
    protected int getNetworkOffset() { return 1; }

    @Override
    protected @NonNull ArrayList<PacketWrapper<?>> build(final @NonNull Location location, final @NonNull List<Integer> networkIds, final @NonNull CPlayer cp)
    {
        // Spawn
        final Location loc = location.clone().add(getOffset());
        final WrapperPlayServerSpawnEntity spawn = new WrapperPlayServerSpawnEntity(
                networkIds.get(0), Optional.of(UUID.randomUUID()),
                EntityTypes.ITEM,
                new Vector3d(loc.getX(), loc.getY(), loc.getZ()),
                loc.getYaw(), 0.f, loc.getPitch(),
                0,
                Optional.empty()
        );

        // Metadata
        final WrapperPlayServerEntityMetadata meta = new WrapperPlayServerEntityMetadata(
                networkIds.get(0),
                Arrays.asList(
                        new EntityMetadata.NoGravity(true).into(),
                        new EntityMetadata.Silent(true).into(),
                        new ItemProjectileMetadata.Item(getItem(cp)).into()
                )
        );


        final var list = new ArrayList<PacketWrapper<?>>(2);
        list.add(spawn);
        list.add(meta);
        return list;
    }
}
