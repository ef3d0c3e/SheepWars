package org.ef3d0c3e.sheepwars.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

public abstract class ViewEvent {
    @AllArgsConstructor
    public static class ViewerAddedEvent extends ViewEvent
    {
        @Getter
        private final @NonNull View.WrappedViewer viewer;
    }

    @AllArgsConstructor
    public static class ViewerRemovedEvent extends ViewEvent
    {
        @Getter
        private final @NonNull View.WrappedViewer viewer;
    }
}
