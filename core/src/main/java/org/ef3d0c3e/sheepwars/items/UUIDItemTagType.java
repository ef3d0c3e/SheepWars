package org.ef3d0c3e.sheepwars.items;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDItemTagType implements PersistentDataType<byte[], UUID>
{
    @Override
    public @NotNull Class<byte[]> getPrimitiveType()
    {
        return byte[].class;
    }

    @Override
    public @NotNull Class<UUID> getComplexType()
    {
        return UUID.class;
    }

    @Override
    public byte[] toPrimitive(UUID uuid, @NotNull PersistentDataAdapterContext context)
    {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(uuid.getLeastSignificantBits());
        buffer.putLong(uuid.getMostSignificantBits());
        return buffer.array();
    }

    @Override
    public @NotNull UUID fromPrimitive(byte @NotNull [] bytes, @NotNull PersistentDataAdapterContext context)
    {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long leastBits = buffer.getLong();
        long mostBits = buffer.getLong();
        return new UUID(mostBits, leastBits);
    }
}

