// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.event.annotation.XKasperSaga;
import org.joda.time.DateTime;

import java.util.concurrent.TimeUnit;

import static com.viadeo.kasper.KasperResponse.Status;

@XKasperSaga(domain = TestDomain.class)
public class ToDoSagaTest extends KasperSaga {

    private DateTime creationDate;
    private ID todoID;

    @XKasperSaga.Start(getter="getTodoID")
    public void start(ToDoItemCreatedV2Event event) {
        creationDate = DateTime.now();
        todoID = event.getTodoID();
    }

    @XKasperSaga.Schedule(getter="getTodoID", unit = TimeUnit.SECONDS, delay = 2)
    public void onTodoDeadlineExpired(ToDoItemCreatedV2Event event) {
        if ( this.isActive()) {
            // si besoin d'une commande : this.send(..Command);
            // this.publish(send Event new TodoDeadlineExpirad(...))
        }
    }

    @XKasperSaga.End(getter = "getMySuperTodoID") // Optional if the same
    public Status onToDoItemCompleted(ToDoItemCompletedEvent event) {
        if (this.isActive()) {
            return Status.OK;
        }
        return Status.FAILURE;
    }

    @XKasperSaga.BasicStep(getter = "getMySuperTodoID") // Optional if the same
    public void onToDoItemCompletedV2(ToDoItemCompletedEvent event) {
        if (this.isActive()) {
            this.end();
        }
    }

}
