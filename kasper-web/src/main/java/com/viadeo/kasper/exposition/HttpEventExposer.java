// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.codahale.metrics.Timer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.HttpContextHeaders;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.exposition.alias.AliasRegistry;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.beans.Introspector;
import java.io.IOException;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * WARNING:
 *
 * Domain events exposing is an anti-pattern of the platform's spirit in itself
 * this endpoint is provided as a migration helper when dealing with a
 * legacy platform allowing a smooth decoupling : the legacy platform can
 * then send domain events in place of the not-yet-implemented platform's
 * domain to come
 *
 */
public class HttpEventExposer extends HttpExposer {
    private static final long serialVersionUID = 3099102125586430908L;

    protected static final transient Logger REQUEST_LOGGER = LoggerFactory.getLogger(HttpEventExposer.class);

    private static final String GLOBAL_TIMER_REQUESTS_TIME_NAME = name(HttpEventExposer.class, "requests-time");
    private static final String GLOBAL_TIMER_REQUESTS_HANDLE_TIME_NAME = name(HttpEventExposer.class, "requests-handle-time");
    private static final String GLOBAL_METER_REQUESTS_NAME = name(HttpEventExposer.class, "requests");
    private static final String GLOBAL_METER_ERRORS_NAME = name(HttpEventExposer.class, "errors");

    private final Map<String, Class<? extends Event>> exposedEvents = new HashMap<>();
    private final transient KasperEventBus eventBus;
    private final List<ExposureDescriptor<Event,EventListener>> descriptors;
    private final ObjectMapper mapper;
    private final transient HttpContextDeserializer contextDeserializer;
    private final AliasRegistry aliasRegistry;

    // ------------------------------------------------------------------------

    public HttpEventExposer(final KasperEventBus eventBus, final List<ExposureDescriptor<Event,EventListener>> descriptors) {
        this(
                eventBus,
                descriptors,
                new HttpContextDeserializer(),
                ObjectMapperProvider.INSTANCE.mapper()
        );
    }


    public HttpEventExposer(final KasperEventBus eventBus,
                            final List<ExposureDescriptor<Event,EventListener>> descriptors,
                            final HttpContextDeserializer contextDeserializer,
                            final ObjectMapper mapper) {
        this.eventBus = checkNotNull(eventBus);
        this.descriptors = checkNotNull(descriptors);
        this.contextDeserializer = checkNotNull(contextDeserializer);
        this.mapper = checkNotNull(mapper);
        this.aliasRegistry = new AliasRegistry();
    }

    // ------------------------------------------------------------------------

    @Override
    public void init() throws ServletException {
        LOGGER.info("=============== Exposing events ===============");

        final MultiValueMap<Class<? extends Event>, String> aliasesByEventClasses = CollectionUtils.toMultiValueMap(Maps.<Class<? extends Event>, List<String>>newHashMap());
        final Set<Class<? extends Event>> eventClasses = Sets.newHashSet();

        for (final ExposureDescriptor<Event, EventListener> descriptor : descriptors) {
            for (final String alias : AliasRegistry.aliasesFrom(descriptor.getHandler())) {
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

        if (exposedEvents.isEmpty()) {
            LOGGER.warn("No Event has been exposed.");
        } else {
            LOGGER.info("Total exposed " + exposedEvents.size() + " events.");
        }

        LOGGER.info("=================================================\n");
    }

    // ------------------------------------------------------------------------

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        handleEvent(req, resp);

        /*
         * must be last call to ensure that everything is sent to the client
         * (even if an error occurred)
         */
        try {
            resp.flushBuffer();
        } catch (final IOException e) {
            LOGGER.warn("Error when trying to flush output buffer", e);
        }
    }

    @Override
    protected void doPut(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {

        handleEvent(req, resp);

        /*
         * must be last call to ensure that everything is sent to the client
         * (even if an error occurred)
         */
        try {
            resp.flushBuffer();
        } catch (final IOException e) {
            LOGGER.warn("Error when trying to flush output buffer", e);
        }
    }

    // ------------------------------------------------------------------------

    private void handleEvent(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        /* Start request timer */
        final Timer.Context classTimer = getMetricRegistry().timer(GLOBAL_TIMER_REQUESTS_TIME_NAME).time();

        /* Create a request correlation id */
        final UUID kasperCorrelationUUID = UUID.randomUUID();
        resp.addHeader("kasperCorrelationId", kasperCorrelationUUID.toString());

        resp.addHeader(HttpContextHeaders.HEADER_SERVER_NAME, serverName());

        /* Log starting request */
        REQUEST_LOGGER.info("Processing HTTP Event '{}' '{}'", req.getMethod(), getFullRequestURI(req));
        final String eventName = aliasRegistry.resolve(resourceName(req.getRequestURI()));

        final Class<? extends Event> eventClass = exposedEvents.get(eventName);
        if (null == eventClass) {
            resp.setStatus(Response.Status.NOT_FOUND.getStatusCode());
            return;
        }

        JsonParser parser = null;
        try {

            if ( (null == req.getContentType()) || ( ! req.getContentType().contains(MediaType.APPLICATION_JSON_VALUE))) {
                resp.setStatus(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode());
                return;
            }

            final ObjectReader reader = mapper.reader();

            /* parse the input stream to that event, no utility method for inputstream+type?? */
            parser = reader.getFactory().createParser(req.getInputStream());
            final Event event = reader.readValue(parser, eventClass);

            /* extract context from request */
            final Context context = contextDeserializer.deserialize(req, kasperCorrelationUUID);
            MDC.setContextMap(context.asMap());

            /* send now that event to the platform and wait for the result */
            final Timer.Context eventHandleTime = getMetricRegistry().timer(name(event.getClass(), "requests-handle-time")).time();
            final Timer.Context classHandleTime = getMetricRegistry().timer(GLOBAL_TIMER_REQUESTS_HANDLE_TIME_NAME).time();

            eventBus.publish(event);

            resp.setStatus(Response.Status.ACCEPTED.getStatusCode());

            eventHandleTime.stop();
            classHandleTime.stop();

        } catch (final IOException e) {

            LOGGER.error("Error in event [" + eventClass.getName() + "]", e);
            getMetricRegistry().meter(GLOBAL_METER_ERRORS_NAME).mark();
            resp.setStatus(Response.Status.BAD_REQUEST.getStatusCode());

        } catch (final Throwable th) {

            // we catch any other exception in order to still respond with json
            LOGGER.error("Error in event [" + eventClass.getName() + "]", th);
            getMetricRegistry().meter(GLOBAL_METER_ERRORS_NAME).mark();
            resp.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());

        } finally {

            if (null != parser) {
                parser.close();
            }

            /* Log & metrics */
            final long time = classTimer.stop();
            REQUEST_LOGGER.info("Execution Time '{}' ns", time);
            getMetricRegistry().meter(GLOBAL_METER_REQUESTS_NAME).mark();
        }
    }


    // ------------------------------------------------------------------------

    @SuppressWarnings({"rawtypes", "unchecked"})
    HttpExposer expose(final Class<? extends Event> eventClass, final List<String> aliases) {
        checkNotNull(eventClass);
        checkNotNull(aliases);

        final String eventPath = eventToPath(eventClass);
        final String eventName = eventClass.getSimpleName();

        LOGGER.info("-> Exposing event[{}] at path[/{}]",
                eventName,
                    getServletContext().getContextPath() + eventPath);

        for (final String alias : aliases) {
            LOGGER.info("-> Exposing event[{}] at path[/{}]",
                    eventName,
                    getServletContext().getContextPath() + alias);
        }

        putKey(eventPath, eventClass, exposedEvents);

        aliasRegistry.register(eventPath, aliases);

        return this;
    }

    // ------------------------------------------------------------------------

    private String eventToPath(final Class<? extends Event> exposedEvent) {
        return Introspector.decapitalize(exposedEvent.getSimpleName().replaceAll("Event", ""));
    }

}
