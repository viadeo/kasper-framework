package com.viadeo.kasper.eventhandling.fixture;

import com.viadeo.kasper.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserEventListener extends EventListener<UserEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventListener.class);
    private Spy<UserEvent> spy;


    public UserEventListener(final Spy<UserEvent> spy) throws InterruptedException {
        this.spy = spy;
    }

    @Override
    public void handle(UserEvent userEvent) {
        spy.handle(userEvent);
    }
}
