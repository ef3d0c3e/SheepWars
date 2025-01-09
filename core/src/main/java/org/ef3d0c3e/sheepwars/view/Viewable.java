package org.ef3d0c3e.sheepwars.view;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;

import java.util.ArrayList;

public interface Viewable {
    ArrayList<PacketWrapper<?>> getSendPackets();
    ArrayList<PacketWrapper<?>> getUnsendPackets();
}
