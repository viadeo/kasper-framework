// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.AbstractContext;
import org.axonframework.domain.GenericEventMessage;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class EventUtils {

    private EventUtils() { /* Utility class */ }

    public static GenericEventMessage<Event> KasperEvent2AxonMessage(final Event event) {
        checkNotNull(event);
        Preconditions.checkState(event.getContext().isPresent(), "Context must be present !");

        final Context context = event.getContext().get();

        /* Sets a valid Kasper correlation id if required */
        if (AbstractContext.class.isAssignableFrom(context.getClass())) {
            final AbstractContext kasperContext = (AbstractContext) context;
            kasperContext.setValidKasperCorrelationId();
        }

        final Map<String, Object> metaData = Maps.newHashMap();
        metaData.put(Context.METANAME, checkNotNull(context));

        final GenericEventMessage<Event> eventMessageAxon = new GenericEventMessage<>(event, metaData);
        return eventMessageAxon;
    }

}
