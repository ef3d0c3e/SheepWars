package org.ef3d0c3e.sheepwars.view;

import lombok.NonNull;

import java.time.Instant;

public class BaseView extends View {
    protected BaseView(@NonNull Viewable viewable) {
    }

    @Override
    public void processEvent(@NonNull ViewEvent event) {
        if (event instanceof ViewEvent.ViewerAddedEvent add)
            update(Instant.now(), add.getViewer());
        else if (event instanceof ViewEvent.ViewerRemovedEvent rem) {
            unsendData(rem.getViewer());
        }
    }
}
