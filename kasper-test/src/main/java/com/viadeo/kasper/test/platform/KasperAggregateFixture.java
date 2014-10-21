// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.RepositoryManager;
import com.viadeo.kasper.cqrs.command.impl.DefaultRepositoryManager;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.repository.EventSourcedRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.domain.DomainEvent;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.test.platform.fixture.KasperCommandFixture;
import org.axonframework.commandhandling.CommandBus;
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
            final Repository<AGR> repository,
            final Class<AGR> aggregateClass) {
        return new KasperAggregateFixture<>(repository, aggregateClass);
    }

    // ------------------------------------------------------------------------

    private final GivenWhenThenTestFixture<AGR> fixture;
    private final Repository<AGR> repository;
    private final RepositoryManager repositoryManager;

    private boolean checkIllegalState = true;

    private KasperAggregateFixture(final Repository<AGR> repository, final Class<AGR> aggregateClass) {
        Preconditions.checkNotNull(repository);
        Preconditions.checkNotNull(aggregateClass);

        this.fixture = new GivenWhenThenTestFixture<>(aggregateClass);
        this.fixture.setReportIllegalStateChange(checkIllegalState);

        this.repository = repository;
        this.repository.setEventStore(fixture.getEventStore());
        this.repository.setEventBus(fixture.getEventBus());
        this.repository.init();

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

    public KasperAggregateFixture<AGR> registerCommandHandler(final CommandHandler commandHandler) {
        commandHandler.setEventBus(fixture.getEventBus());
        commandHandler.setRepositoryManager(repositoryManager);

        fixture.registerCommandHandler(commandHandler.getCommandClass(), commandHandler);
        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperAggregateExecutor given() {
        return new KasperAggregateExecutor(fixture.given());
    }

    public KasperAggregateExecutor givenEvents(final DomainEvent... events) {
        if (events.length > 0) {
            if ( ! EventSourcedRepository.class.isAssignableFrom(this.repository.getClass())) {
                throw new KasperException(
                        "Your repository is not event-sourced, you cannot use given(events)"
                );
            }
        }
        return new KasperAggregateExecutor(fixture.given((Object[]) events));
    }

    public KasperAggregateExecutor givenEvents(final List<DomainEvent> events) {
        if ( ! EventSourcedRepository.class.isAssignableFrom(this.repository.getClass())) {
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

    public IRepository<AGR> repository() {
        return repository;
    }

}
