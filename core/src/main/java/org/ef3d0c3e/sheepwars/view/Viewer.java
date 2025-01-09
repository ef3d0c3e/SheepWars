package org.ef3d0c3e.sheepwars.view;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import lombok.NonNull;

import java.util.List;

public interface Viewer {
    public void sendViewData(final @NonNull List<PacketWrapper<?>> packets);
}
