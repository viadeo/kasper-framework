// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.impl.DefaultDomainLocator;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.repository.EventSourcedRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.domain.DomainEvent;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventstore.EventStore;
import org.axonframework.test.GivenWhenThenTestFixture;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A mocked platform integration test fixture based on Axon GivenWhenThenFixture
 * oriented on testing a specific aggregate (and associated repository) testing
 *
 * @param <AGR> the aggregate type
 */
public class KasperAggregateFixture<AGR extends AggregateRoot>
        implements KasperFixture<
             KasperAggregateExecutor,
             KasperAggregateResultValidator
        > {

    public static final int AGGREGATE_PARAMETER_POSITION = 0;

    private GivenWhenThenTestFixture<AGR> fixture;
    private final IRepository<AGR> repository;
    private boolean checkIllegalState = true;

    private final ConceptResolver conceptResolver = new ConceptResolver();
    private final RelationResolver relationResolver = new RelationResolver(conceptResolver);
    private final EntityResolver entityResolver = new EntityResolver(conceptResolver, relationResolver);
    private final RepositoryResolver repositoryResolver = new RepositoryResolver(entityResolver);
    private final CommandHandlerResolver commandHandlerResolver = new CommandHandlerResolver();

    private final DomainLocator domainLocator = new DefaultDomainLocator(repositoryResolver, commandHandlerResolver);

    // ------------------------------------------------------------------------

    private KasperAggregateFixture(final IRepository<AGR> repository, final Class<AGR> aggregateClass) {
        this.repository = repository;
        domainLocator.registerRepository(repository);

        fixture = new GivenWhenThenTestFixture<>(aggregateClass);
        fixture.setReportIllegalStateChange(checkIllegalState);

        if (Repository.class.isAssignableFrom(this.repository.getClass())) {
            ((Repository) repository).setEventStore(fixture.getEventStore());
            ((Repository) repository).setEventBus(fixture.getEventBus());
        }

        /* WARNING: fixture.registerRepository(repository); */
    }

    public static final <AGR extends AggregateRoot> KasperAggregateFixture<AGR> forRepository(
            final IRepository<AGR> repository, final Class<AGR> aggregateClass) {
        return new KasperAggregateFixture<>(checkNotNull(repository), checkNotNull(aggregateClass));
    }

    // ------------------------------------------------------------------------

    public KasperAggregateFixture<AGR> withoutIllegalStateCheck() {
        this.checkIllegalState = false;
        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperAggregateFixture<AGR> registerCommandHandler(final CommandHandler commandHandler) {
        domainLocator.registerHandler(commandHandler);

        final Class<? extends Command> commandClass = commandHandlerResolver.getCommandClass(commandHandler.getClass());

        commandHandler.setEventBus(fixture.getEventBus());
        commandHandler.setDomainLocator(domainLocator);

        fixture.registerCommandHandler(commandClass, commandHandler);

        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperAggregateExecutor given() {
        return new KasperAggregateExecutor(fixture.given());
    }

    public KasperAggregateExecutor given(final DomainEvent... events) {
        if (events.length > 0) {
            if ( ! EventSourcedRepository.class.isAssignableFrom(this.repository.getClass())) {
                throw new KasperException(
                        "Your repository is not event-sourced, you cannot use given(events)"
                );
            }
        }
        return new KasperAggregateExecutor(fixture.given(events));
    }

    public KasperAggregateExecutor given(final List<DomainEvent> events) {
        if ( ! EventSourcedRepository.class.isAssignableFrom(this.repository.getClass())) {
            throw new KasperException(
                    "Your repository is not event-sourced, you cannot use given(events)"
            );
        }
        return new KasperAggregateExecutor(fixture.given(events));
    }

    @Override
    public KasperAggregateExecutor givenCommands(final Command... commands) {
        return new KasperAggregateExecutor(fixture.givenCommands(commands));
    }

    @Override
    public KasperAggregateExecutor givenCommands(final List<Command> commands) {
        return new KasperAggregateExecutor(fixture.givenCommands(commands));
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandBus commandBus() {
        return fixture.getCommandBus();
    }

    @Override
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
