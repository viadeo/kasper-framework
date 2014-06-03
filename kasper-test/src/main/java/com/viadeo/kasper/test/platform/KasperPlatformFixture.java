// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandBus;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.test.platform.fixture.KasperCommandFixture;
import com.viadeo.kasper.test.platform.fixture.KasperEventFixture;
import com.viadeo.kasper.test.platform.fixture.KasperQueryFixture;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.domain.EventMessage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.mockito.Mockito.spy;

public class KasperPlatformFixture implements
        KasperCommandFixture<KasperPlatformExecutor, KasperPlatformCommandResultValidator>,
        KasperQueryFixture<KasperPlatformExecutor, KasperPlatformQueryResultValidator>,
        KasperEventFixture<KasperPlatformExecutor, KasperPlatformListenedEventsValidator> {

    private final RecordingPlatform platform;
    private final SpyEventBus eventBus;
    private final SpyCommandBus commandBus;
    private final Config config;

    private boolean initialized;
    private DomainBundle domainBundle;

    // ------------------------------------------------------------------------

    public KasperPlatformFixture() {
        this(ConfigFactory.empty());
    }

    public KasperPlatformFixture(final Config config) {
        this.config = checkNotNull(config);
        this.platform = new RecordingPlatform();
        this.eventBus = new SpyEventBus(this.platform);
        this.commandBus = new SpyCommandBus(this.platform);
    }

    // ------------------------------------------------------------------------

    private KasperPlatformExecutor prepare() {
        this.platform.recordedEvents.clear();
        return new KasperPlatformExecutor(this.platform);
    }

    
    private void initialize() {
        if( ! this.initialized) {
            final KasperPlatformConfiguration platformConfiguration = new KasperPlatformConfiguration();

            this.platform.set(
                new Platform.Builder(platformConfiguration)
                        .withConfiguration(this.config)
                        .withEventBus(this.eventBus)
                        .withCommandGateway(
                            new KasperCommandGateway(this.commandBus)
                        )
                        .addDomainBundle(this.domainBundle)
                        .build()
            );

            this.initialized = true;
        }
    }

    private void reset() {
        this.initialized = false;
        this.platform.reset();
    }

    public KasperPlatformFixture register(final DomainBundle domainBundle){
        this.domainBundle = domainBundle;
        reset();
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
                this.platform.get().getEventBus().publishEvent(context, event);
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
                this.platform.get().getCommandGateway().sendCommandAndWaitForAResponse(
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
        public final List<Command> recordedCommands = Lists.newLinkedList();
        public final Map<Class<? extends EventListener>, EventListener> listeners = Maps.newHashMap();
        private Platform platform;

        // -----

        public Platform get() {
            return this.platform;
        }

        public void set(final Platform platform) {
            this.platform = checkNotNull(platform);
        }

        public List<IEvent> getRecordedEvents(final Class<IEvent> eventClass) {
            final List<IEvent> events = Lists.newArrayList();
            for (final IEvent event : this.recordedEvents) {
                if (eventClass.isAssignableFrom(event.getClass())) {
                    events.add(event);
                }
            }
            return events;
        }

        public void reset() {
            this.recordedCommands.clear();
            this.recordedEvents.clear();
            this.listeners.clear();
        }
    }

    /**
     * Spy event bus
     */
    public static class SpyEventBus extends KasperEventBus {

        private final RecordingPlatform recordingPlatform;

        // -----

        protected SpyEventBus(RecordingPlatform recordingPlatform){
            this.recordingPlatform = recordingPlatform;
        }

        // -----

        @Override
        public void publish(final EventMessage... messages) {
            super.publish(messages);
            for (final EventMessage message : messages) {
                if (IEvent.class.isAssignableFrom(message.getPayloadType())) {
                    this.recordingPlatform.recordedEvents.add((IEvent) message.getPayload());
                }
            }
        }

        @Override
        public void subscribe(final org.axonframework.eventhandling.EventListener eventListener) {
            if (eventListener instanceof EventListener) {
                final EventListener kasperEventListener = (EventListener) eventListener;
                final EventListener spiedKasperEventListener = spy(kasperEventListener);
                this.recordingPlatform.listeners.put(kasperEventListener.getClass(), spiedKasperEventListener);
                super.subscribe(spiedKasperEventListener);
            } else {
                super.subscribe(eventListener);
            }
        }
    }

    /**
     * Spy Command gateway
     */
    public static class SpyCommandBus extends KasperCommandBus {

        private final RecordingPlatform recordingPlatform;

        protected SpyCommandBus(final RecordingPlatform recordingPlatform){
            this.recordingPlatform = checkNotNull(recordingPlatform);
        }

        @Override
        protected <R> void doDispatch(final CommandMessage<?> command, final CommandCallback<R> callback) {
            this.recordingPlatform.recordedCommands.add((Command) command.getPayload());
            super.doDispatch(command, callback);
        }

    }

}
