// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.common.annotation.MetaData;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public abstract class KasperSaga<E extends Event> extends org.axonframework.saga.annotation.AbstractAnnotatedSaga {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperSaga.class);

    /**
     * Generic parameter position for Event Object
     */
    public static final int PARAMETER_EVENT_POSITION = 0;

    private transient CommandGateway commandGateway;
    private transient EventBus eventBus;

    private final Class<E> eventClass;

    // ------------------------------------------------------------------------

    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = checkNotNull(commandGateway);
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }

    // ------------------------------------------------------------------------

    protected Optional<CommandGateway> getCommandGateway() {
        return Optional.fromNullable(this.commandGateway);
    }

    protected Optional<EventBus> getEventBus() {
        return Optional.fromNullable(this.eventBus);
    }

    // ------------------------------------------------------------------------

    /**
     * Publish an event on the event bus
     *
     * @param event The event
     */
    public void publish(final Event event) {
        checkNotNull(event, "The specified event must be non null");
        checkState(eventBus != null, "Unable to publish the specified event : the event bus is null");
        final EventMessage eventMessage = GenericEventMessage.asEventMessage(event);
        this.eventBus.publish(eventMessage);
    }

    /**
     * Send the command to the gateway
     *
     * @param command
     * @param context
     */
    public void sendCommand(Command command, @MetaData(Context.METANAME) Context context){
        checkNotNull(command, "The specified command must be non null");
        checkNotNull(context, "The specified context must be non null");
        checkState(commandGateway != null, "Unable to send the specified command : the command gateway is null");
        this.commandGateway.sendCommand(command, context);
    }

    // ------------------------------------------------------------------------



    protected KasperSaga() {

        @SuppressWarnings("unchecked")
        final Optional<Class<E>> eventClassOpt =
                (Optional<Class<E>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                this.getClass(),
                                KasperSaga.class,
                                KasperSaga.PARAMETER_EVENT_POSITION
                        );

        if (eventClassOpt.isPresent()) {
            this.eventClass = eventClassOpt.get();
        } else {
            throw new KasperException("Unable to identify event class for " + this.getClass());
        }

    }
}
