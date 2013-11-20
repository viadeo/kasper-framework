// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.configuration.DefaultPlatformConfiguration;
import com.viadeo.kasper.client.platform.configuration.PlatformConfiguration;
import com.viadeo.kasper.client.platform.configuration.PlatformFactory;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.EventBus;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperPlatformFixture
        implements KasperFixture<
            KasperPlatformExecutor,
            KasperPlatformResultValidator
        > {

    private String[] prefixes;

    /**
     * A recording platform for fixture
     */
    class RecordingPlatform {
        public final List<IEvent> recordedEvents = Lists.newLinkedList();
        private Platform platform;

        public Platform get() {
            return this.platform;
        }

        public void set(final Platform platform) {
            this.platform = platform;
        }
    }
    private final RecordingPlatform platform = new RecordingPlatform();

    /**
     * A test configuration to record events
     */
    private PlatformConfiguration testConfiguration = new DefaultPlatformConfiguration() {

        @Override
        public KasperEventBus eventBus() {
            if (containsInstance(KasperEventBus.class)) {
                return getInstance(KasperEventBus.class);
            } else {
                final KasperEventBus eventBus = new KasperEventBus() {
                    @Override
                    public void publish(final EventMessage... messages) {
                        super.publish(messages);

                        for (final EventMessage message : messages) {
                        if (IEvent.class.isAssignableFrom(message.getPayloadType())) {
                                platform.recordedEvents.add((IEvent) message.getPayload());
                            }
                        }
                    }
                };

                registerInstance(KasperEventBus.class, eventBus);
                return eventBus;
            }
        }

    };

    // ------------------------------------------------------------------------

    public static KasperPlatformFixture forPrefix(final String... prefix) {
        return new KasperPlatformFixture(prefix);
    }

    private KasperPlatformFixture(final String... prefixes) {
        this.prefixes = prefixes;
    }

    // ------------------------------------------------------------------------

    public PlatformConfiguration conf() {
        if (null == platform.get()) {
            platform();
            this.testConfiguration.annotationRootProcessor().setScanPrefixes(prefixes);
        }
        return this.testConfiguration;
    }

    public Platform platform() {
        if (null == platform.get()) {
            platform.set(new PlatformFactory(testConfiguration).getPlatform(true));
            conf();
        }
        return platform.get();
    }

    // ========================================================================

    @Override
    public KasperPlatformFixture registerCommandHandler(CommandHandler commandHandler) {
        checkNotNull(commandHandler).setEventBus(conf().eventBus());
        conf().getComponentsInstanceManager().recordInstance(
                (Class) commandHandler.getClass(),
                commandHandler
        );
        return this;
    }

    public KasperPlatformFixture registerEventListener(EventListener eventListener) {
        checkNotNull(eventListener).setCommandGateway(conf().commandGateway());
        conf().getComponentsInstanceManager().recordInstance(
                (Class) eventListener.getClass(),
                eventListener
        );
        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperPlatformExecutor given() {
        platform();
        platform.recordedEvents.clear();
        return new KasperPlatformExecutor(platform);
    }

    @Override
    public KasperPlatformExecutor givenCommands(final Command... commands) {
        return this.givenCommands(DefaultContextBuilder.get(), commands);
    }

    @Override
    public KasperPlatformExecutor givenCommands(final List<Command> commands) {
        return this.givenCommands(DefaultContextBuilder.get(), commands);
    }

    public KasperPlatformExecutor givenCommands(final Context context, final Command... commands) {
        return this.givenCommands(context, Arrays.asList(commands));
    }

    public KasperPlatformExecutor givenCommands(final Context context, final List<Command> commands) {
        for (final Command command : commands) {
            try {
                platform().getCommandGateway().sendCommandAndWaitForAResponse(
                        command, context
                );
            } catch (final Exception e) {
                throw new KasperException(e);
            }
        }

        return given();
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandBus commandBus() {
        return conf().commandBus();
    }

    @Override
    public EventBus eventBus() {
        return conf().eventBus();
    }

}
