// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.query.listener;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.event.listener.EventMessage;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;
import com.viadeo.kasper.domain.sample.hello.api.event.BuddyChangedForHelloMessageEvent;
import com.viadeo.kasper.domain.sample.hello.api.query.results.HelloMessageResult;
import com.viadeo.kasper.domain.sample.hello.common.db.HelloMessagesIndexStore;
import com.viadeo.kasper.domain.sample.hello.common.db.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * A query listener generally have one goal : MAINTAIN A QUERY INDEX FRESHNESS
 */
@XKasperEventListener( /* Required annotation to define the sticked domain */
        domain = HelloDomain.class,
        description = "Update Hello indexes on change"
)
public class BuddyChangedForHelloMessageQueryListener extends AutowiredEventListener<BuddyChangedForHelloMessageEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuddyChangedForHelloMessageQueryListener.class);

    private static final KeyValueStore store = HelloMessagesIndexStore.db;

    @SuppressWarnings("unchecked")
    @Override
    public EventResponse handle(final EventMessage<BuddyChangedForHelloMessageEvent> message) {

        final BuddyChangedForHelloMessageEvent event = message.getEvent();
        final KasperID helloId = message.getEntityId().get();

        HelloMessageResult indexedHello = null;
        String forBuddy = event.getOriginalForBuddy().toLowerCase();

        /* Delete the message from the original buddy before applying it the the new one */
        Optional<Object> data = store.get(forBuddy);
        if (data.isPresent()) {
            final Map<KasperID, HelloMessageResult> mapData = (Map<KasperID, HelloMessageResult>) data.get();

            if (mapData.containsKey(helloId)) {

                indexedHello = mapData.get(helloId);
                mapData.remove(helloId);
                store.set(forBuddy, mapData);

            } else { /* OK, seems we were not up-to-date, just warn about */
                LOGGER.warn(
                    "We were requested to change the buddy name for hello message {} " +
                            "but this buddy has not this message",
                    helloId
                );
            }

        } else { /* OK, seems we were not up-to-date, just warn about */
            LOGGER.warn(
                "We were requested to change the buddy name for hello message {} " +
                            "but this buddy has no messages",
                helloId
            );
        }

        /* Assign the message to the new buddy */
        if (null != indexedHello) {
            forBuddy = event.getNewForBuddy().toLowerCase();

            /* Retrieve this buddy list of messages */
            data = store.get(forBuddy);

            /* Add the new message to this list */
            final Map<KasperID, HelloMessageResult> newData;
            if (data.isPresent()) {
                newData = (Map<KasperID, HelloMessageResult>) data.get();
            } else {
                newData = Maps.newHashMap();
                store.set(forBuddy, newData);
            }

            newData.put(helloId, new HelloMessageResult(
                    helloId,
                    message.getVersion(),
                    message.getTimestamp(),
                    indexedHello.getMessage()
            ));
        }

        return EventResponse.success();
    }

}
