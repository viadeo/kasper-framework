package com.viadeo.kasper.eventhandling.fixture;

import com.viadeo.kasper.event.EventListener;

public class ChildEventListener extends EventListener<UserEvent> {
    private Spy<UserEvent> spy;

    public ChildEventListener(final Spy<UserEvent> spy) {
        this.spy = spy;
    }

    @Override
    public void handle(UserEvent userEvent) {
        spy.handle(userEvent);
    }
}
