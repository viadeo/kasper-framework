// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain.sample;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResponse;
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
import com.viadeo.kasper.event.EventResponse;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.event.domain.DomainEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class MyCustomDomainBox {

    public static final String MY_CUSTOM_DOMAIN_LABEL = "MyCustomDomain";

    @XKasperDomain(label = MY_CUSTOM_DOMAIN_LABEL, prefix = "test", description = "A domain definition used only for the test")
    public static class MyCustomDomain implements Domain { }

    @XKasperCommandHandler(domain = MyCustomDomain.class)
    public static class MyCustomCommandHandler extends CommandHandler<MyCustomCommand> {
        @Override
        public CommandResponse handle(MyCustomCommand command) throws Exception {
            if ( ! command.isSuccessful()) {
                throw new RuntimeException("I must failed!");
            }
            return CommandResponse.ok();
        }
    }

    @XKasperQueryHandler(domain = MyCustomDomain.class)
    public static class MyCustomQueryHandler extends QueryHandler<MyCustomQuery, MyCustomQueryResult> {
        @Override
        public QueryResponse<MyCustomQueryResult> retrieve(MyCustomQuery query) throws Exception {
            if ( ! query.isSuccessful()) {
                throw new RuntimeException("I must failed!");
            }
            return QueryResponse.of(new MyCustomQueryResult());
        }
    }

    @XKasperEventListener(domain = MyCustomDomain.class)
    public static class MyCustomEventListener extends EventListener<MyCustomEvent> {
        @Override
        public EventResponse handle(Context context, MyCustomEvent event) {
            return EventResponse.success();
        }
    }

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

    public static class MyCustomCommand implements Command {
        private static final long serialVersionUID = -8986821788476319364L;

        private final boolean successful;

        public MyCustomCommand() {
            this(true);
        }

        public MyCustomCommand(boolean successful) {
            this.successful = successful;
        }

        public boolean isSuccessful() {
            return successful;
        }
    }

    public static class MyCustomQuery implements Query {
        private static final long serialVersionUID = -6151236712593564163L;

        private final boolean successful;

        public MyCustomQuery() {
            this(true);
        }

        public MyCustomQuery(boolean successful) {
            this.successful = successful;
        }

        public boolean isSuccessful() {
            return successful;
        }
    }

    public static class MyCustomQueryResult implements QueryResult { }

    public static class MyCustomEvent implements Event { }

    public static abstract class AbstractMyCustomEvent implements Event { }

    public static class MyCustomDomainEvent implements DomainEvent<MyCustomDomain> { }

    public static class MyCustomMalformedDomainEvent implements DomainEvent { }

    @Configuration
    public static class MyCustomDomainSpringConfiguration {

        @Bean
        public MyCustomCommandHandler myCustomCommandHandler() {
            return new MyCustomCommandHandler();
        }

        @Bean
        public MyCustomQueryHandler myCustomQueryHandler() {
            return new MyCustomQueryHandler();
        }

        @Bean
        public MyCustomEventListener myCustomEventListener() {
            return new MyCustomEventListener();
        }

        @Bean
        public MyCustomRepository myCustomRepository() {
            return new MyCustomRepository();
        }
    }

    public static DomainBundle getBundle() {
        return new DomainBundle.Builder(new MyCustomDomain())
                .with(new MyCustomCommandHandler())
                .with(new MyCustomQueryHandler())
                .with(new MyCustomEventListener())
                .with(new MyCustomRepository())
                .build();
    }

}
