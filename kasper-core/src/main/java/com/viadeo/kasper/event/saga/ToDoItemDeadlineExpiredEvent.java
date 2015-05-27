// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

public class ToDoItemDeadlineExpiredEvent {
    private final String todoId;

    public ToDoItemDeadlineExpiredEvent(String todoId) {
        this.todoId = todoId;
    }

    public String getTodoId() {
        return todoId;
    }

    @Override
    public String toString() {
        return "ToDoItemDeadlineExpiredEvent(" + todoId + ")";
    }

}
