package org.ef3d0c3e.sheepwars.packets;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import lombok.Getter;

public class PlayerMetadata {
    public static class SkinParts implements IntoEntityData {
        @Override
        public EntityData into() {
            return new EntityData(
                    17,
                    EntityDataTypes.BYTE,
                    value
            );
        }

        byte value;

        public SkinParts() {
            this.value = 0;
        }

        public SkinParts cape(boolean v) {
            value = (byte)(value | (v ? 0b1 : 0b0));
            return this;
        }

        public SkinParts jacket(boolean v) {
            value = (byte)(value | (v ? 0b10 : 0b0));
            return this;
        }

        public SkinParts leftSleeve(boolean v) {
            value = (byte)(value | (v ? 0b100 : 0b0));
            return this;
        }

        public SkinParts rightSleeve(boolean v) {
            value = (byte)(value | (v ? 0b1000 : 0b0));
            return this;
        }

        public SkinParts leftPants(boolean v) {
            value = (byte)(value | (v ? 0b10000 : 0b0));
            return this;
        }

        public SkinParts rightPants(boolean v) {
            value = (byte)(value | (v ? 0b100000 : 0b0));
            return this;
        }

        public SkinParts hat(boolean v) {
            value = (byte)(value | (v ? 0b1000000 : 0b0));
            return this;
        }

        public SkinParts all() {
            value = (byte)0xFF;
            return this;
        }
    }
}
