// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.event.DomainEvent;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.DefaultRepositoryManager;
import com.viadeo.kasper.core.component.command.RepositoryManager;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.gateway.AxonCommandHandler;
import com.viadeo.kasper.core.component.command.gateway.ContextualizedUnitOfWork;
import com.viadeo.kasper.core.component.command.repository.*;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.test.platform.fixture.KasperCommandFixture;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventstore.EventStore;
import org.axonframework.test.GivenWhenThenTestFixture;

import java.util.List;

/**
 * A mocked platform integration test fixture based on Axon GivenWhenThenFixture
 * oriented on testing a specific aggregate (and associated repository) testing
 *
 * @param <AGR> the aggregate type
 */
public final class KasperAggregateFixture<AGR extends AggregateRoot>
        implements KasperCommandFixture<KasperAggregateExecutor, KasperAggregateResultValidator>
{

    public static <AGR extends AggregateRoot> KasperAggregateFixture<AGR> forRepository(
            final Repository<KasperID,AGR> repository,
            final Class<AGR> aggregateClass) {
        return new KasperAggregateFixture<>(repository, aggregateClass);
    }

    // ------------------------------------------------------------------------

    private final GivenWhenThenTestFixture<AGR> fixture;
    private final Repository<KasperID,AGR> repository;
    private final RepositoryManager repositoryManager;

    private boolean checkIllegalState = true;

    private KasperAggregateFixture(final Repository<KasperID,AGR> repository, final Class<AGR> aggregateClass) {
        Preconditions.checkNotNull(repository);
        Preconditions.checkNotNull(aggregateClass);

        this.fixture = new GivenWhenThenTestFixture<>(aggregateClass);
        this.fixture.setReportIllegalStateChange(checkIllegalState);
        this.fixture.registerIgnoredField(AggregateRoot.class, "version");
        ((SimpleCommandBus)this.fixture.getCommandBus()).setUnitOfWorkFactory(new ContextualizedUnitOfWork.Factory());

        this.repository = repository;

        if (WirableRepository.class.isAssignableFrom(this.repository.getClass())) {
            ((WirableRepository) this.repository).setEventBus(fixture.getEventBus());
        }

        if (WirableEventSourcedRepository.class.isAssignableFrom(this.repository.getClass())) {
            ((WirableEventSourcedRepository) this.repository).setEventStore(fixture.getEventStore());
        }

        // special case for which we attach an event store in order to apply our assertions
        if (AutowiredRepository.class.isAssignableFrom(this.repository.getClass())) {
            ((AutowiredRepository)this.repository).setEventStore(fixture.getEventStore());
        }

        this.repositoryManager = new DefaultRepositoryManager();
        this.repositoryManager.register(repository);

        Preconditions.checkState(
                aggregateClass == repository.getAggregateClass(),
                String.format(
                        "The specified repository don't support the specified aggregate, <expected=%s> <actual=%s> ",
                        repository.getAggregateClass(),
                        aggregateClass
                )
        );

        KasperMetrics.setMetricRegistry(new MetricRegistry());
    }

    public KasperAggregateFixture<AGR> withoutIllegalStateCheck() {
        this.checkIllegalState = false;
        return this;
    }

    public <COMMAND extends Command> KasperAggregateFixture<AGR> registerCommandHandler(final CommandHandler<COMMAND> commandHandler) {
        if (commandHandler instanceof AutowiredCommandHandler) {
            AutowiredCommandHandler autoWiringCommandHandler = (AutowiredCommandHandler) commandHandler;
            autoWiringCommandHandler.setEventBus(fixture.getEventBus());
            autoWiringCommandHandler.setRepositoryManager(repositoryManager);
        }

        fixture.registerCommandHandler(
                commandHandler.getInputClass(),
                new AxonCommandHandler<>(commandHandler)
        );
        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperAggregateExecutor given() {
        return new KasperAggregateExecutor(fixture.given());
    }

    public KasperAggregateExecutor givenEvents(final DomainEvent... events) {
        if (events.length > 0) {
            if ( ! BaseEventSourcedRepository.class.isAssignableFrom(this.repository.getClass())) {
                throw new KasperException(
                        "Your repository is not event-sourced, you cannot use given(events)"
                );
            }
        }
        return new KasperAggregateExecutor(fixture.given((Object[]) events));
    }

    public KasperAggregateExecutor givenEvents(final List<DomainEvent> events) {
        if ( ! BaseEventSourcedRepository.class.isAssignableFrom(this.repository.getClass())) {
            throw new KasperException(
                    "Your repository is not event-sourced, you cannot use given(events)"
            );
        }
        return new KasperAggregateExecutor(fixture.given(events));
    }

    @Override
    public KasperAggregateExecutor givenCommands(final Command... commands) {
        return new KasperAggregateExecutor(fixture.givenCommands((Object[]) commands));
    }

    @Override
    public KasperAggregateExecutor givenCommands(final List<Command> commands) {
        return new KasperAggregateExecutor(fixture.givenCommands(commands));
    }

    @Override
    public KasperAggregateExecutor givenCommands(Context context, Command... commands) {
        throw new UnsupportedOperationException(
                "Context in given commands not supported in aggregate test fixture"
        );
    }

    @Override
    public KasperAggregateExecutor givenCommands(Context context, List<Command> commands) {
        throw new UnsupportedOperationException(
                "Context in given commands not supported in aggregate test fixture"
        );
    }

    // ------------------------------------------------------------------------

    public CommandBus commandBus() {
        return fixture.getCommandBus();
    }

    public EventBus eventBus() {
        return fixture.getEventBus();
    }

    public EventStore eventStore() {
        return fixture.getEventStore();
    }

    public Repository<KasperID,AGR> repository() {
        return repository;
    }

}
