// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.exposition.alias.AliasRegistry;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.beans.Introspector;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * WARNING:
 * <p>
 * Domain events exposing is an anti-pattern of the platform's spirit in itself
 * this endpoint is provided as a migration helper when dealing with a
 * legacy platform allowing a smooth decoupling : the legacy platform can
 * then send domain events in place of the not-yet-implemented platform's
 * domain to come
 * </p>
 */
public class HttpEventExposer extends HttpExposer<Event, KasperResponse> {

    private static final long serialVersionUID = 3099102125586430908L;

    private final transient List<ExposureDescriptor<Event, EventListener>> descriptors;
    private final transient KasperEventBus eventBus;

    private final ObjectToHttpServletResponse objectToHttpResponse;
    private final HttpServletRequestToObject httpRequestToObject;

    // ------------------------------------------------------------------------

    public HttpEventExposer(final Platform platform, final List<ExposureDescriptor<Event, EventListener>> descriptors) {
        this(
                platform.getEventBus(),
                platform.getMeta(),
                descriptors,
                new HttpContextDeserializer(),
                ObjectMapperProvider.INSTANCE.mapper()
        );
    }


    public HttpEventExposer(final KasperEventBus eventBus,
                            final Meta meta,
                            final List<ExposureDescriptor<Event, EventListener>> descriptors,
                            final HttpContextDeserializer contextDeserializer,
                            final ObjectMapper mapper) {
        super(contextDeserializer, meta);
        this.eventBus = checkNotNull(eventBus);
        this.descriptors = checkNotNull(descriptors);

        this.httpRequestToObject = new HttpServletRequestToObject.JsonToObjectMapper(mapper);
        this.objectToHttpResponse = new ObjectToHttpServletResponse(mapper);
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing events ===============");

        final MultiValueMap<Class<? extends Event>, String> aliasesByEventClasses = CollectionUtils.toMultiValueMap(Maps.<Class<? extends Event>, List<String>>newHashMap());
        final Set<Class<? extends Event>> eventClasses = Sets.newHashSet();

        for (final ExposureDescriptor<Event, EventListener> descriptor : descriptors) {
            for (final String alias : AliasRegistry.aliasesFrom(descriptor.getInput())) {
                aliasesByEventClasses.add(descriptor.getInput(), alias);
            }
            eventClasses.add(descriptor.getInput());
        }

        for (final Class<? extends Event> eventClass : eventClasses) {
            List<String> aliases = aliasesByEventClasses.get(eventClass);
            if (null == aliases) {
                aliases = Lists.newArrayList();
            }
            expose(eventClass, aliases);
        }

        LOGGER.info("Total exposed {} events.", getExposedInputs().size());
        LOGGER.info("=================================================\n");
    }

    // ------------------------------------------------------------------------

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(httpRequestToObject, objectToHttpResponse, req, resp);
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest(httpRequestToObject, objectToHttpResponse, req, resp);
    }

    @Override
    protected KasperResponse createErrorResponse(final CoreReasonCode code, final List<String> reasons) {
        return new KasperResponse(KasperResponse.Status.ERROR, new KasperReason(code, reasons));
    }

    @Override
    protected KasperResponse createRefusedResponse(final CoreReasonCode code, final List<String> reasons) {
        return new KasperResponse(KasperResponse.Status.REFUSED, new KasperReason(code, reasons));
    }

    @Override
    public KasperResponse doHandle(final Event event, final Context context) throws Exception {
        eventBus.publishEvent(context, event);
        return new KasperResponse();
    }

    @Override
    protected Response.Status getStatusFrom(final KasperResponse response) {
        if (response.isOK()) {
            return Response.Status.ACCEPTED;
        }
        return super.getStatusFrom(response);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    HttpExposer expose(final Class<? extends Event> eventClass, final List<String> aliases) {
        checkNotNull(eventClass);
        checkNotNull(aliases);

        final String eventPath = toPath(eventClass);
        final String eventName = eventClass.getSimpleName();

        LOGGER.info("-> Exposing event[{}] at path[/{}]",
                eventName,
                getServletContext().getContextPath() + eventPath);

        for (final String alias : aliases) {
            LOGGER.info("-> Exposing event[{}] at path[/{}]",
                    eventName,
                    getServletContext().getContextPath() + alias);
        }

        checkAvailabilityOfResourcePath(eventPath);

        getExposedInputs().put(eventPath, (Class<Event>) eventClass);

        getAliasRegistry().register(eventPath, aliases);

        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    protected String toPath(final Class<? extends Event> exposedEvent) {
        return Introspector.decapitalize(exposedEvent.getSimpleName().replaceAll("Event", ""));
    }
}
