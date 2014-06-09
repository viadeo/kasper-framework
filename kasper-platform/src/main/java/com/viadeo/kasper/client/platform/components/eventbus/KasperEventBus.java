// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.event.IEvent;
import org.axonframework.domain.EventMessage;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.ClusterSelector;
import org.axonframework.eventhandling.ClusteringEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperEventBus extends ClusteringEventBus {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperEventBus.class);


    public KasperEventBus(ClusterSelector clusterSelector) {
        super(clusterSelector);
    }

    private final List<PublicationHandler> publicationHandlers = Lists.newLinkedList();

    public interface PublicationHandler {
        void handlePublication(EventMessage eventMessage);
        void shutdown();
    }

    public void onEventPublished(final PublicationHandler publicationHandler) {
        this.publicationHandlers.add(publicationHandler);
    }

    protected void noticePublicationHandlers(final EventMessage event) {
        for (final PublicationHandler publicationHandler : publicationHandlers) {
            publicationHandler.handlePublication(event);
        }
    }

    @Override
    public void publish(final EventMessage... messages) {
        final EventMessage[] newMessages;

        /* Add the context to messages if required */
        if (CurrentContext.value().isPresent()) {
            newMessages = new EventMessage[messages.length];
            for (int i = 0 ; i < messages.length ; i++) {
                final EventMessage message = messages[i];
                if ( ! message.getMetaData().containsKey(Context.METANAME)) {
                    final Map<String, Object> metaData = Maps.newHashMap();
                    metaData.put(Context.METANAME, CurrentContext.value().get());
                    newMessages[i] = message.andMetaData(metaData);
                } else {
                    newMessages[i] = message;
                }
            }
        } else {
            newMessages = messages;
        }

        /* Publish to Axon bus implementation */
        this.publishToSuper(newMessages);

        /* Notice handlers about event publication */
        for (final EventMessage message : newMessages) {
            this.noticePublicationHandlers(message);
        }
    }

    @VisibleForTesting
    void publishToSuper(final EventMessage... messages) {
        super.publish(messages);
    }

    public void publish(final IEvent event) {
        this.publish(GenericEventMessage.asEventMessage(event));
    }

    public void publishEvent(final Context context, final IEvent event) {
        this.publish(
            new GenericEventMessage<>(
                checkNotNull(event),
                new HashMap<String, Object>() {{
                    this.put(Context.METANAME, context);
                }}
            )
        );
    }

    public Optional<Runnable> getShutdownHook(){
        final KasperEventBus that = this;
        return Optional.<Runnable>of(new Runnable() {
            @Override
            public void run() {
                LOGGER.info("Starting shutdown : Publication handlers");
                System.out.println("Starting shutdown : Publication handlers");
                for (final PublicationHandler handler : that.publicationHandlers) {
                    handler.shutdown();
                }
                LOGGER.info("Shutdown complete : Publication handlers");
                System.out.println("Shutdown complete : Publication handlers");

                LOGGER.info("Starting shutdown : Event Processing");
                System.out.println("Starting shutdown : Event Processing");
            }
        });
    }

}
