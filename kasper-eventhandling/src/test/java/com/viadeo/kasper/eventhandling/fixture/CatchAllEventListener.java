package com.viadeo.kasper.eventhandling.fixture;

import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.IEvent;

public class CatchAllEventListener extends EventListener<IEvent> {

    @Override
    public void handle(IEvent event) {
    }
}