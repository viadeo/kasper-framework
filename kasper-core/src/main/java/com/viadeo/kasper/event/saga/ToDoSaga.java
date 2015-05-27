// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.time.Duration;

import javax.annotation.Resource;

/**
 * @author Allard Buijze
 */
public class ToDoSaga extends AbstractAnnotatedSaga {

    private static final long serialVersionUID = 1798051388403504162L;

    private transient CommandGateway commandGateway;
    private transient EventScheduler eventScheduler;

    private ScheduleToken deadline;

    @StartSaga
    @SagaEventHandler(associationProperty = "todoId")
    public void onToDoItemCreated(ToDoItemCreatedEvent event) {
        deadline = eventScheduler.schedule(Duration.standardSeconds(2),
                new ToDoItemDeadlineExpiredEvent(event.getTodoId()));
    }

    @SagaEventHandler(associationProperty = "todoId")
    public void onDeadlineExpired(ToDoItemDeadlineExpiredEvent event) {
        commandGateway.send(new MarkToDoItemOverdueCommand(event.getTodoId()));
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "todoId")
    public void onToDoItemCompleted(ToDoItemCompletedEvent event) {
        if (deadline != null) {
            eventScheduler.cancelSchedule(deadline);
        }
    }

    @Resource
    public void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @Resource
    public void setEventScheduler(EventScheduler eventScheduler) {
        this.eventScheduler = eventScheduler;
    }
}
