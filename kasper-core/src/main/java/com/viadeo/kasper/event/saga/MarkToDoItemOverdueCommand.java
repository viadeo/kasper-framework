// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import org.axonframework.commandhandling.annotation.TargetAggregateIdentifier;

/**
 * @author Allard Buijze
 */
public class MarkToDoItemOverdueCommand {
    @TargetAggregateIdentifier
    private final String todoId;

    public MarkToDoItemOverdueCommand(String todoId) {
        this.todoId = todoId;
    }

    public String getTodoId() {
        return todoId;
    }
}
