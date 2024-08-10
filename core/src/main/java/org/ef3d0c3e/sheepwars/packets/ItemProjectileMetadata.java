package org.ef3d0c3e.sheepwars.packets;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.inventory.ItemStack;

public class ItemProjectileMetadata {
    public static class Item implements IntoEntityData {
        @Override
        public EntityData into() {
            return new EntityData(
                    8,
                    EntityDataTypes.ITEMSTACK,
                    SpigotConversionUtil.fromBukkitItemStack(value)
            );
        }

        ItemStack value;

        public Item(final ItemStack value) {
            this.value = value;
        }
    }
}
