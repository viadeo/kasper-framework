// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
import com.viadeo.kasper.core.component.command.repository.BaseEventSourcedRepository;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.command.repository.WirableRepository;
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
            ((WirableRepository) this.repository).setEventStore(fixture.getEventStore());
            ((WirableRepository) this.repository).setEventBus(fixture.getEventBus());
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
