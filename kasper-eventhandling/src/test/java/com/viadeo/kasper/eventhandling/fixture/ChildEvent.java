package com.viadeo.kasper.eventhandling.fixture;


public class ChildEvent extends UserEvent {

    public ChildEvent(String firstName, String lastName, Integer age) {
        super(firstName, lastName, age);
    }
}