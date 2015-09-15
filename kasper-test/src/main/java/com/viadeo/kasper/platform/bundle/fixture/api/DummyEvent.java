package com.viadeo.kasper.platform.bundle.fixture.api;

import com.viadeo.kasper.api.component.event.Event;

public class DummyEvent implements Event {

    public String foo;

    public DummyEvent(String foo) {
        this.foo = foo;
    }
}
