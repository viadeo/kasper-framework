// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.event.Event;

public class ToDoItemCreatedV2Event implements Event {
    private final ID todoID;
    private final String description;

    public ToDoItemCreatedV2Event(ID todoID, String description) {
        this.todoID = todoID;
        this.description = description;
    }

    public ID getTodoID() {
        return todoID;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ToDoItemCreatedEvent(" + todoID + ", '" + description + "')";
    }
}
