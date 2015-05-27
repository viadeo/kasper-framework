// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.annotation.*;
import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

import static com.viadeo.kasper.KasperResponse.Status;

@XKasperSaga(domain = TestDomain.class)
public class ToDoSagaTest extends KasperSaga<ToDoItemCreatedV2Event> {

    private DateTime creationDate;
    private ID todoID;

    @XKasperSagaStart(getter="getTodoID")
    public void start(ToDoItemCreatedV2Event event) {
        creationDate = DateTime.now();
        todoID = event.getTodoID();
    }

    @XKasperSagaSchedule(unit = TimeUnit.SECONDS, duration = 2)
    public void onTodoDeadlineExpired() {
        if ( this.isActive()) {
            // si besoin d'une commande : this.send(..Command);
            // this.publish(send Event new TodoDeadlineExpirad(...))
        }
    }

    @XKasperSagaEnd(getter = "getMySuperTodoID") // Optional if the same
    public Status onToDoItemCompleted(ToDoItemCompletedEvent event) {
        if (this.isActive()) {
            return Status.OK;
        }
        return Status.FAILURE;
    }

    @XKasperSagaStep(getter = "getMySuperTodoID") // Optional if the same
    public void onToDoItemCompletedV2(ToDoItemCompletedEvent event) {
        if (this.isActive()) {
            this.end();
        }
    }

}
