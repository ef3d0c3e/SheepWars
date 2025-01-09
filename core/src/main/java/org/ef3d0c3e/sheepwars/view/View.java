package org.ef3d0c3e.sheepwars.view;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import oshi.util.tuples.Pair;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public abstract class View {
    protected static class ViewableData
    {
        final @NonNull Viewable object;
        @NonNull Instant lastUpdate;
        ArrayList<PacketWrapper<?>> sendPackets;
        ArrayList<PacketWrapper<?>> removePackets;

        public ViewableData(final @NonNull Viewable object) {
            this.object = object;
            update(Instant.now());
        }

        final void update(final @NonNull Instant time) {
            sendPackets = object.getSendPackets();
            removePackets = object.getUnsendPackets();
            lastUpdate = time;
        }
    }

    @AllArgsConstructor
    protected static class WrappedViewer
    {
        @Getter
        private final @NonNull Viewer viewer;
        @Getter @Setter
        private @NonNull Instant lastUpdate;
    }

    @Getter
    private HashSet<ViewableData> data = new HashSet<>();
    @Getter
    private final HashSet<WrappedViewer> viewers = new HashSet<>();

    /**
     * Adds data to the view
     * @param v Data to add to the view
     */
    public void addViewData(final @NonNull Viewable v)
    {
        data.add(new ViewableData(v));
    }

    /**
     * Adds a viewer to the view
     * @param viewer Viewer to add
     */
    final public void addViewer(final @NonNull Viewer viewer) {
        final var wrapped = new WrappedViewer(viewer, Instant.MIN);
        viewers.add(wrapped);
        processEvent(new ViewEvent.ViewerAddedEvent(wrapped));
    }

    /**
     * Removes a viewer from the view
     * @param viewer Viewer to remove
     */
    final public void removeViewer(final @NonNull Viewer viewer) {
        AtomicReference<WrappedViewer> wrapped = new AtomicReference<>();
        viewers.removeIf(v -> { if (v.viewer == viewer) { wrapped.set(v); return true; } return false; });
        processEvent(new ViewEvent.ViewerRemovedEvent(wrapped.get()));
    }

    /**
     * Resend the view to viewers that aren't up to date
     */
    public void update() {
        final var now = Instant.now();
        viewers.forEach(viewer -> update(now, viewer));
    }

    /**
     * Updates the view for a Viewer
     * @param viewer The viewer to update the view of
     */
    public void update(final @NonNull Instant now, final @NonNull WrappedViewer viewer) {
        data.forEach(viewableData -> {
            if (viewableData.lastUpdate.isBefore(viewer.getLastUpdate())) return;

            viewer.viewer.sendViewData(viewableData.sendPackets);
        });
        viewer.setLastUpdate(now);
    }

    /**
     * Remove the data of this view
     * @param viewer
     */
    public void unsendData(final @NonNull WrappedViewer viewer) {
        data.forEach(viewableData -> {
            viewer.getViewer().sendViewData(viewableData.removePackets);
        });
    }

    /**
     * Processes view events
     * @param event The view events to process
     */
    public abstract void processEvent(final @NonNull ViewEvent event);
}
