package com.viadeo.kasper.eventhandling.cluster.fixture;

import com.viadeo.kasper.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserEventListener extends EventListener<UserEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventListener.class);
    private Spy spy;


    public UserEventListener(final Spy spy) throws InterruptedException {
        this.spy = spy;
    }

    @Override
    public void handle(UserEvent userEvent) {
        spy.handle(userEvent);
    }
}
