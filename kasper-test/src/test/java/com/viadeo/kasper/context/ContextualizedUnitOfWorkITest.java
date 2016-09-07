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
package com.viadeo.kasper.context;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.AbstractPlatformTests;
import com.viadeo.kasper.KasperTestId;
import com.viadeo.kasper.api.annotation.XKasperCommand;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.annotation.XKasperEvent;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.event.EntityCreatedEvent;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperRepository;
import com.viadeo.kasper.core.component.command.AutowiredEntityCommandHandler;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperConcept;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.command.gateway.ContextualizedUnitOfWork;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.repository.AutowiredRepository;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.platform.bundle.DefaultDomainBundle;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.repository.AggregateNotFoundException;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("serial")
public class ContextualizedUnitOfWorkITest extends AbstractPlatformTests {

    private static final Integer TOTAL_VERIFY_CALLS = 5;

    // -- Test components -----------------------------------------------------

    @XKasperDomain(label = "test domain", prefix = "ctx")
    public static class ContextTestDomain implements Domain { }

    @XKasperCommand
    public static class ContextTestCommand implements Command { }

    @XKasperCommandHandler(domain = ContextTestDomain.class)
    public static class ContextTestHandler extends AutowiredEntityCommandHandler<ContextTestCommand, ContextTestAGR> {
        public CommandResponse handle(final ContextTestCommand command) {

            StaticChecker.verify(ContextualizedUnitOfWork.getCurrentUnitOfWork().getContext().get());

            final Repository<KasperID,ContextTestAGR> repo = this.getRepository();

            try {
                repo.load(new KasperTestId("42"), 0L);
            } catch (final AggregateNotFoundException e) {
                // Ignore
            }

            final ContextTestAGR agr = new ContextTestAGR(new KasperTestId("42"));
            repo.add(agr);

            return CommandResponse.ok();
        }
    }

    @XKasperEvent(action = "test")
    public static class ContextTestEvent extends EntityCreatedEvent<ContextTestDomain> {
        private static final long serialVersionUID = 7017358308867238442L;

        public ContextTestEvent(final KasperID id) {
            super(id);
        }
    }

    @XKasperConcept(domain = ContextTestDomain.class, label = "test agr")
    public static class ContextTestAGR extends Concept {
        public ContextTestAGR(final KasperID id) {
            StaticChecker.verify(ContextualizedUnitOfWork.getCurrentUnitOfWork().getContext().get());
            apply(new ContextTestEvent(id));
        }

        @EventHandler
        protected void handlerContextTestEvent(final ContextTestEvent event) {
            this.setId(event.getEntityId());
            StaticChecker.verify(ContextualizedUnitOfWork.getCurrentUnitOfWork().getContext().get());
        }
    }

    @XKasperRepository
    public static class ContextTestRepository extends AutowiredRepository<KasperID,ContextTestAGR> {
        @Override
        protected Optional<ContextTestAGR> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
            StaticChecker.verify(ContextualizedUnitOfWork.getCurrentUnitOfWork().getContext().get());
            return Optional.absent();
        }

        @Override
        protected void doSave(final ContextTestAGR aggregate) {
            StaticChecker.verify(ContextualizedUnitOfWork.getCurrentUnitOfWork().getContext().get());
        }

        @Override
        protected void doDelete(final ContextTestAGR aggregate) {
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public List<DomainBundle> getBundles() {
        return Lists.<DomainBundle>newArrayList(
                new DefaultDomainBundle(
                        Lists.<CommandHandler>newArrayList(new ContextTestHandler()),
                        Lists.<QueryHandler>newArrayList(),
                        Lists.<Repository>newArrayList(new ContextTestRepository()),
                        Lists.<EventListener>newArrayList(),
                        Lists.<Saga>newArrayList(),
                        Lists.<QueryInterceptorFactory>newArrayList(),
                        Lists.<CommandInterceptorFactory>newArrayList(),
                        Lists.<EventInterceptorFactory>newArrayList(),
                        new ContextTestDomain(),
                        "ContextTestDomain"
                )
        );
    }

    @Test
    public void test() throws Exception {

        // Given
        final Context context = this.newContext();
        final CommandGateway gw = this.getPlatform().getCommandGateway();
        final ContextTestCommand command = new ContextTestCommand();
        StaticChecker.context(context);

        // When
        final Future<CommandResponse> future = gw.sendCommandForFuture(command, context);
        future.get();

        // Then
        assertEquals(TOTAL_VERIFY_CALLS, StaticChecker.getCounter());
    }

    // -- Static verificator --------------------------------------------------

    private static class StaticChecker {
        private static Integer counter = 0;
        private static Context context;

        public static void context(final Context context) {
            StaticChecker.context = context;
        }

        public static void verify(final Context context) {
            counter++;
            final boolean equals = context.equals(StaticChecker.context);
            if ( ! equals) {
                fail(context + " != " + StaticChecker.context);
            }
        }

        public static Integer getCounter() {
            return counter;
        }
    }

}
