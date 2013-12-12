// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Lists;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.commandbus.KasperCommandBus;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.domain.EventMessage;

import java.util.Arrays;
import java.util.List;

public class KasperPlatformFixture
        implements KasperCommandFixture<KasperPlatformExecutor, KasperPlatformCommandResultValidator>,
                   KasperQueryFixture<KasperPlatformExecutor, KasperPlatformQueryResultValidator>
{

    private final RecordingPlatform platform;
    private final SpyEventBus eventBus;

    private DomainBundle domainBundle;

    public KasperPlatformFixture() {
        this.platform = new RecordingPlatform();
        this.eventBus = new SpyEventBus(platform);
    }

    // ------------------------------------------------------------------------

    private KasperPlatformExecutor prepare() {
        platform.recordedEvents.clear();
        return new KasperPlatformExecutor(platform);
    }

    
    private void initialize() {
        platform.set(
                new Platform.Builder()
                        .withConfiguration(ConfigFactory.empty())
                        .withEventBus(eventBus)
                        .withQueryGateway(new KasperQueryGateway())
                        .withCommandGateway(new KasperCommandGateway(new KasperCommandBus()))
                        .withMetricRegistry(new MetricRegistry())
                        .addDomainBundle(domainBundle)
                        .build()
        );
    }

    public KasperPlatformFixture register(final DomainBundle domainBundle){
        this.domainBundle = domainBundle;
        return this;
    }

    @Override
    public KasperPlatformExecutor given() {
        initialize();
        return prepare();
    }

    @Override
    public KasperPlatformExecutor givenEvents(final IEvent... events) {
        return this.givenEvents(DefaultContextBuilder.get(), events);
    }

    @Override
    public KasperPlatformExecutor givenEvents(final List<IEvent> events) {
        return this.givenEvents(DefaultContextBuilder.get(), events);
    }

    @Override
    public KasperPlatformExecutor givenEvents(final Context context, IEvent... events) {
        return this.givenEvents(context, Arrays.asList(events));
    }

    @Override
    public KasperPlatformExecutor givenEvents(final Context context, List<IEvent> events) {
        initialize();

        for (final IEvent event : events) {
            try {
                platform.get().getEventBus().publishEvent(context, event);
            } catch (final Exception e) {
                throw new KasperException(e);
            }
        }

        return prepare();
    }

    @Override
    public KasperPlatformExecutor givenCommands(final Command... commands) {
        return this.givenCommands(DefaultContextBuilder.get(), commands);
    }

    @Override
    public KasperPlatformExecutor givenCommands(final List<Command> commands) {
        return this.givenCommands(DefaultContextBuilder.get(), commands);
    }

    @Override
    public KasperPlatformExecutor givenCommands(final Context context, final Command... commands) {
        return this.givenCommands(context, Arrays.asList(commands));
    }

    @Override
    public KasperPlatformExecutor givenCommands(final Context context, final List<Command> commands) {
        initialize();

        for (final Command command : commands) {
            try {
                platform.get().getCommandGateway().sendCommandAndWaitForAResponse(
                        command, context
                );
            } catch (final Exception e) {
                throw new KasperException(e);
            }
        }

        return prepare();
    }

    // ------------------------------------------------------------------------

    /**
     * A recording platform for fixture
     */
    public static class RecordingPlatform {
        public final List<IEvent> recordedEvents = Lists.newLinkedList();
        private Platform platform;

        public Platform get() {
            return this.platform;
        }

        public void set(final Platform platform) {
            this.platform = platform;
        }
    }

    /**
     * Spy event bus
     */
    public static class SpyEventBus extends KasperEventBus {

        private final RecordingPlatform recordingPlatform;

        protected SpyEventBus(RecordingPlatform recordingPlatform){
            this.recordingPlatform = recordingPlatform;
        }

        @Override
        public void publish(final EventMessage... messages) {
            super.publish(messages);
            for (final EventMessage message : messages) {
                if (IEvent.class.isAssignableFrom(message.getPayloadType())) {
                    recordingPlatform.recordedEvents.add((IEvent) message.getPayload());
                }
            }
        }
    }
}
