package org.ef3d0c3e.sheepwars.view;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.ef3d0c3e.sheepwars.locale.Locale;

import java.util.ArrayList;

public class LocalizedHologram implements Viewable {
    private final Locale locale;

    public LocalizedHologram(Locale locale) {
        this.locale = locale;
    }

    @Override
    public ArrayList<PacketWrapper<?>> getSendPackets() {
        return null;
    }

    @Override
    public ArrayList<PacketWrapper<?>> getUnsendPackets() {
        return null;
    }
}
