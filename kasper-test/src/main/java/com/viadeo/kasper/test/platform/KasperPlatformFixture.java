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
import com.viadeo.kasper.client.platform.configuration.DefaultPlatformSpringConfiguration;
import com.viadeo.kasper.client.platform.configuration.PlatformConfiguration;
import com.viadeo.kasper.client.platform.configuration.PlatformFactory;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.boot.ComponentsInstanceManager;
import com.viadeo.kasper.core.boot.SimpleComponentsInstanceManager;
import com.viadeo.kasper.core.boot.SpringComponentsInstanceManager;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.EventBus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperPlatformFixture
        implements KasperCommandFixture<
            KasperPlatformExecutor,
            KasperPlatformCommandResultValidator
        >,
        KasperQueryFixture<
            KasperPlatformExecutor,
            KasperPlatformQueryResultValidator
        >
{

    /**
     * The optional Spring settings
     */
    private final AnnotationConfigApplicationContext springContext;
    private final String[] prefixes;
    private boolean strictSpringInstances = false;

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
    @Configuration
    public static class FixturePlatformConfiguration extends DefaultPlatformSpringConfiguration {

        private RecordingPlatform platform;
        private ApplicationContext springContext;
        private boolean strictSpringInstances = false;

        public FixturePlatformConfiguration() {
            super();
        }

        public void setDependencies(
                final RecordingPlatform platform,
                final ApplicationContext context,
                final Boolean strictSpringInstances) {
            this.platform = platform;
            this.springContext = context;
            this.strictSpringInstances = strictSpringInstances;
        }

        @Bean
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

        @Bean
        @Override
        public ComponentsInstanceManager getComponentsInstanceManager() {
                if (containsInstance(ComponentsInstanceManager.class)) {
                    return getInstance(ComponentsInstanceManager.class);
                } else {

                    final ComponentsInstanceManager sman;
                    if (null != springContext) {
                        sman = new SpringComponentsInstanceManager();
                        ((SpringComponentsInstanceManager) sman).setBeansMustExists(strictSpringInstances);
                    } else {
                        sman = new SimpleComponentsInstanceManager();
                    }

                    registerInstance(ComponentsInstanceManager.class, sman);

                    return sman;
                }
        }

    };

    private FixturePlatformConfiguration testConfiguration;

    // -- Static builders -----------------------------------------------------

    public static KasperPlatformFixture scanPrefix(final String... prefix) {
        return new KasperPlatformFixture(prefix);
    }

    public static KasperPlatformFixture fromSpring(final Class... configurationClasses) {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        for (final Class configurationClass : configurationClasses) {
            context.register(configurationClass);
        }

        return new KasperPlatformFixture(context);
    }

    public static KasperPlatformFixture fromSpring(final Class configurationClass, final String... prefix) {
        final AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

        context.register(configurationClass);

        return new KasperPlatformFixture(context, prefix);
    }

    public static KasperPlatformFixture fromSpring(final AnnotationConfigApplicationContext context, final String... prefix) {
        return new KasperPlatformFixture(context, prefix);
    }

    public static KasperPlatformFixture fromSpring(final AnnotationConfigApplicationContext context) {
        return new KasperPlatformFixture(context);
    }

    // ------------------------------------------------------------------------

    private KasperPlatformFixture(final String... prefixes) {
        this.prefixes = checkNotNull(prefixes);
        this.springContext = null;
    }

    private KasperPlatformFixture(final AnnotationConfigApplicationContext context) {
        this.prefixes = null;
        this.springContext = checkNotNull(context);
    }

    private KasperPlatformFixture(final AnnotationConfigApplicationContext context, final String... prefixes) {
        this.prefixes = checkNotNull(prefixes);
        this.springContext = checkNotNull(context);
    }

    public KasperPlatformFixture withStrictSpringInstanceStrategy() {
        this.strictSpringInstances = true;
        return this;
    }

    // ------------------------------------------------------------------------

    public PlatformConfiguration conf() {
        if (null == platform.get()) {
            platform();
        }
        return this.testConfiguration;
    }

    public Platform platform() {
        if (null == platform.get()) {

            if (null == springContext) {

                testConfiguration = new FixturePlatformConfiguration();
                testConfiguration.setDependencies(platform, springContext, strictSpringInstances);
                platform.set(new PlatformFactory(testConfiguration).getPlatform());

            } else {

                springContext.register(FixturePlatformConfiguration.class);

                testConfiguration = springContext.getBean(FixturePlatformConfiguration.class);
                testConfiguration.setDependencies(platform, springContext, strictSpringInstances);

                springContext.refresh();

                platform.set(springContext.getBean(Platform.class));
            }

            if (null != prefixes) {
                this.testConfiguration.annotationRootProcessor().setScanPrefixes(prefixes);
            }

        }

        return platform.get();
    }

    private void boot() {
        platform().boot();
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

    @Override
    public KasperQueryFixture<KasperPlatformExecutor, KasperPlatformQueryResultValidator> registerQueryHandler(QueryHandler queryHandler) {
        checkNotNull(queryHandler);
        conf().getComponentsInstanceManager().recordInstance(
                (Class) queryHandler.getClass(),
                queryHandler
        );
        return this;
    }

    // ------------------------------------------------------------------------

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
        boot();
        for (final IEvent event : events) {
            try {
                platform().publishEvent(context, event);
            } catch (final Exception e) {
                throw new KasperException(e);
            }
        }

        return given();
    }


    // ------------------------------------------------------------------------

    @Override
    public KasperPlatformExecutor given() {
        boot();
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

    @Override
    public KasperPlatformExecutor givenCommands(final Context context, final Command... commands) {
        return this.givenCommands(context, Arrays.asList(commands));
    }

    @Override
    public KasperPlatformExecutor givenCommands(final Context context, final List<Command> commands) {
        boot();
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
