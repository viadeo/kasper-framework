package com.viadeo.kasper.eventhandling.cluster.fixture;

import com.viadeo.kasper.event.EventListener;

public class ChildEventListener extends EventListener<UserEvent> {
    private Spy spy;

    public ChildEventListener(final Spy spy) {
        this.spy = spy;
    }

    @Override
    public void handle(UserEvent userEvent) {
        spy.handle(userEvent);
    }
}
