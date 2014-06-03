package com.viadeo.kasper.eventhandling.cluster.fixture;


public class ChildEvent extends UserEvent {

    public ChildEvent(String firstName, String lastName, Integer age) {
        super(firstName, lastName, age);
    }
}