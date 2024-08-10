package org.ef3d0c3e.sheepwars.packets;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import lombok.Getter;

public class ArmorStandMetadata {
    public static class Status implements IntoEntityData {
        @Override
        public EntityData into() {
            return new EntityData(
                    15,
                    EntityDataTypes.BYTE,
                    value
            );
        }

        byte value;

        public Status() {
            this.value = 0;
        }

        public Status isSmall(boolean v) {
            value = (byte)(value | (v ? 0b1 : 0b0));
            return this;
        }

        public Status hasArms(boolean v) {
            value = (byte)(value | (v ? 0b100 : 0b0));
            return this;
        }

        public Status hasBasePlate(boolean v) {
            value = (byte)(value | (v ? 0b1000 : 0b0));
            return this;
        }

        public Status isMarker(boolean v) {
            value = (byte)(value | (v ? 0b10000 : 0b0));
            return this;
        }
    }
}
