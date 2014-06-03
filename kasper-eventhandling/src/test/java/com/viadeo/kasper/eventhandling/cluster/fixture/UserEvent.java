package com.viadeo.kasper.eventhandling.cluster.fixture;

import com.google.common.base.Objects;
import com.viadeo.kasper.event.IEvent;

public class UserEvent implements IEvent {
    public String firstName;
    public String lastName;
    public Integer age;

    public UserEvent(String firstName, String lastName, Integer age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(firstName, lastName, age);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final UserEvent other = (UserEvent) obj;
        return Objects.equal(this.firstName, other.firstName) && Objects.equal(this.lastName, other.lastName) && Objects.equal(this.age, other.age);
    }
}