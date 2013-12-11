// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.annotation.XKasperEventListener;

public class MyCustomDomainBox {

    public static final String MY_CUSTOM_DOMAIN_LABEL = "MyCustomDomain";

    @XKasperDomain(label = MY_CUSTOM_DOMAIN_LABEL, prefix = "test", description = "A domain definition used only for the test")
    public static class MyCustomDomain implements Domain { }

    @XKasperCommandHandler(domain = MyCustomDomain.class)
    public static class MyCustomCommandHandler extends CommandHandler<Command> { }

    @XKasperQueryHandler(domain = MyCustomDomain.class)
    public static class MyCustomQueryHandler extends QueryHandler<Query, QueryResult> { }

    @XKasperEventListener(domain = MyCustomDomain.class)
    public static class MyCustomEventListener extends EventListener<Event> { }

    @XKasperRepository()
    public static class MyCustomRepository extends Repository<MyCustomEntity> {

        @Override
        protected Optional<MyCustomEntity> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
            return null;
        }

        @Override
        protected void doSave(MyCustomEntity aggregate) {
            // nothing
        }

        @Override
        protected void doDelete(MyCustomEntity aggregate) {
            // nothing
        }
    }

    @XKasperConcept(domain = MyCustomDomain.class, label = "MyCustomEntity")
    public static class MyCustomEntity extends Concept { }

    public static class MyCustomCommand implements Command { }

    public static class MyCustomQuery implements Query { }

    public static class MyCustomQueryResult implements QueryResult { }

}
