package com.viadeo.kasper.core.component.event.eventbus;

public class ExchangeDescriptor {
    public final String name;
    public final String version;

    public ExchangeDescriptor(String name, String version) {
        this.name = name;
        this.version = version;
    }
}
