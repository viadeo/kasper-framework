// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context;

import com.google.common.base.Optional;
import com.viadeo.kasper.AbstractPlatformTests;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperTestId;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractEntityCommandHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.ddd.impl.ClientRepository;
import com.viadeo.kasper.ddd.impl.Repository;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.er.impl.AbstractRootConcept;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.impl.AbstractEntityEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.repository.AggregateNotFoundException;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@SuppressWarnings("serial")
public class ContextualizedUnitOfWorkITest extends AbstractPlatformTests {

    private static final Integer TOTAL_VERIFY_CALLS = 7;

    // -- Static verificator --------------------------------------------------

    private static class StaticChecker {
        private static Integer counter = 0;
        private static Context context;

        public static void context(final Context context) {
            StaticChecker.context = context;
        }

        public static void verify(final Context context) {
            counter++;
            final boolean equals = context == StaticChecker.context;
            if (!equals) {
                fail(context + " != " + StaticChecker.context);
            }
        }

        public static Integer getCounter() {
            return counter;
        }
    }

    // -- Test components -----------------------------------------------------

    @XKasperDomain(label = "test domain", prefix = "ctx")
    public static class ContextTestDomain implements Domain { }

    @XKasperCommand
    public static class ContextTestCommand implements Command { }

    @XKasperCommandHandler(domain = ContextTestDomain.class)
    public static class ContextTestHandler extends AbstractEntityCommandHandler<ContextTestCommand, ContextTestAGR> {
        public CommandResponse handle(final ContextTestCommand command) throws Exception {

            StaticChecker.verify(CurrentContext.value().get());

            final ClientRepository<ContextTestAGR> repo = this.getRepository();

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
    public static class ContextTestEvent extends AbstractEntityEvent<ContextTestDomain> {
        private static final long serialVersionUID = 7017358308867238442L;

        public ContextTestEvent(final KasperID id) {
            super(CurrentContext.value().get(), id, 1L, DateTime.now());
            StaticChecker.verify(CurrentContext.value().get());
        }
    }

    @XKasperConcept(domain = ContextTestDomain.class, label = "test agr")
    public static class ContextTestAGR extends AbstractRootConcept {
        public ContextTestAGR(final KasperID id) {
            StaticChecker.verify(CurrentContext.value().get());
            apply(new ContextTestEvent(id));
        }

        @EventHandler
        protected void handlerContextTestEvent(final ContextTestEvent event) {
            this.setId(event.getEntityId());
            StaticChecker.verify(CurrentContext.value().get());
            StaticChecker.verify(event.getContext().get());
        }
    }

    @XKasperRepository
    public static class ContextTestRepository extends Repository<ContextTestAGR> {
        @Override
        protected Optional<ContextTestAGR> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
            StaticChecker.verify(CurrentContext.value().get());
            return Optional.absent();
        }

        @Override
        protected void doSave(final ContextTestAGR aggregate) {
            StaticChecker.verify(CurrentContext.value().get());
        }

        @Override
        protected void doDelete(final ContextTestAGR aggregate) {
        }

    }

    // ------------------------------------------------------------------------

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

}
