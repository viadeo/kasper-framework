// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.command.listener;

import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.domain.sample.hello.api.command.NoticeTheWorldCommand;
import com.viadeo.kasper.domain.sample.hello.api.event.BuddyWasUnableToRespondErrorEvent;
import com.viadeo.kasper.domain.sample.hello.api.event.HelloCreatedEvent;

import javax.inject.Inject;

@XKasperEventListener(
        domain = HelloDomain.class,
        description = "Notice the world for each created Hello message"
)
public class NoticeTheWorldAboutCreatedHelloListener extends AutowiredEventListener<HelloCreatedEvent> {

    private CommandGateway commandGateway;
    private KasperEventBus eventBus;

    @Inject
    public NoticeTheWorldAboutCreatedHelloListener(CommandGateway commandGateway, KasperEventBus eventBus) {
        this.commandGateway = commandGateway;
        this.eventBus = eventBus;
    }

    @Override
    public EventResponse handle(final Context context, final HelloCreatedEvent event) {
        final String response = String.format(
            "Hi all, %s received an hello message",
            event.getForBuddy()
        );

        try {

            /**
             * Classical pattern : send a command from a command listener
             */
            commandGateway.sendCommand(
                    new NoticeTheWorldCommand(response),
                /* forward the current context */
                    context
            );

        } catch (final Exception e) {

            /**
             * Will perhaps be replaced by this.publish() in some time
             */
            eventBus.publish(
                    context,
                    new BuddyWasUnableToRespondErrorEvent(
                            event.getEntityId(),
                            response
                    )
            );

        }

        return EventResponse.success();
    }

}
