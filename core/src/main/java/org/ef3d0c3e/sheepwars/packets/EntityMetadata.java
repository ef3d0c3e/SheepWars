package org.ef3d0c3e.sheepwars.packets;

import com.github.retrooper.packetevents.protocol.entity.data.EntityData;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataType;
import com.github.retrooper.packetevents.protocol.entity.data.EntityDataTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;

import java.util.Optional;

public class EntityMetadata {
    public static class Status implements IntoEntityData {
        @Override
        public EntityData into() {
            return new EntityData(
                    0,
                    EntityDataTypes.BYTE,
                    value
            );
        }

        byte value;

        public Status() {
            this.value = 0;
        }

        public Status onFire(boolean v) {
            value = (byte)(value | (v ? 0b1 : 0b0));
            return this;
        }

        public Status isCrouching(boolean v) {
            value = (byte)(value | (v ? 0b10 : 0b0));
            return this;
        }

        public Status isSprinting(boolean v) {
            value = (byte)(value | (v ? 0b1000 : 0b0));
            return this;
        }

        public Status isSwimming(boolean v) {
            value = (byte)(value | (v ? 0b10000 : 0b0));
            return this;
        }

        public Status isInvisible(boolean v) {
            value = (byte)(value | (v ? 0b100000 : 0b0));
            return this;
        }

        public Status isGlowing(boolean v) {
            value = (byte)(value | (v ? 0b1000000 : 0b0));
            return this;
        }

        public Status isElytraFlying(boolean v) {
            value = (byte)(value | (v ? 0b10000000 : 0b0));
            return this;
        }
    }

    @AllArgsConstructor
    public static class AirTicks implements IntoEntityData {
        @Override
        public EntityData into() {
            return new EntityData(
                    1,
                    EntityDataTypes.INT,
                    value
            );
        }

        int value;
    }

    @AllArgsConstructor
    public static class CustomName implements IntoEntityData {
        @Override
        public EntityData into() {
            return new EntityData(
                    2,
                    EntityDataTypes.OPTIONAL_ADV_COMPONENT,
                    Optional.of(value)
            );
        }

        Component value;
    }

    @AllArgsConstructor
    public static class CustomNameVisible implements IntoEntityData {
        @Override
        public EntityData into() {
            return new EntityData(
                    3,
                    EntityDataTypes.BOOLEAN,
                    value
            );
        }

        boolean value;
    }

    @AllArgsConstructor
    public static class Silent implements IntoEntityData {
        @Override
        public EntityData into() {
            return new EntityData(
                    4,
                    EntityDataTypes.BOOLEAN,
                    value
            );
        }

        boolean value;
    }

    @AllArgsConstructor
    public static class NoGravity implements IntoEntityData {
        @Override
        public EntityData into() {
            return new EntityData(
                    5,
                    EntityDataTypes.BOOLEAN,
                    value
            );
        }

        boolean value;
    }
}
