// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommandHandler;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.repository.EventSourcedRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.event.domain.EntityUpdatedEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

import javax.validation.constraints.NotNull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class FixtureUseCase {

    public static class TestDomain implements Domain { }

    public static class TestCreateCommand extends CreateCommand {
        @NotNull
        private final String firstName;
        public TestCreateCommand(KasperID providedId, String firstName) {
            super(providedId);
            this.firstName = firstName;
        }
        public String getFirstName() {
            return this.firstName;
        }
    }

    public static class TestChangeLastNameCommand extends UpdateCommand {
        private final String lastName;
        public TestChangeLastNameCommand(KasperID providedId, String lastName) {
            super(providedId);
            this.lastName = lastName;
        }
        public String getLastName() {
            return this.lastName;
        }
    }

    public static class TestCreatedEvent extends EntityCreatedEvent<TestDomain> {
        protected TestCreatedEvent(KasperID id) {
            super(id);
        }
    }

    public static class TestFirstNameChangedEvent extends EntityUpdatedEvent<TestDomain> {
        private final String firstName;
        protected TestFirstNameChangedEvent(String firstName) {
            this.firstName = firstName;
        }
        public String getFirstName() {
            return this.firstName;
        }
    }

    public static class TestLastNameChangedEvent extends EntityUpdatedEvent<TestDomain> {
        private final String lastName;
        protected TestLastNameChangedEvent(String lastName) {
            this.lastName = lastName;
        }
        public String getLastName() {
            return this.lastName;
        }
    }

    public static class TestAggregate extends Concept {

        private String firstName = "unknown";
        private String lastName = "unknown";

        TestAggregate() { }

        TestAggregate(final KasperID id) {
            apply(new TestCreatedEvent(id));
        }

        @EventHandler
        protected void onCreated(final TestCreatedEvent event) {
            setId(event.getEntityId());
        }

        public void changeFirstName(final Context context, final String firstName) {
            apply(new TestFirstNameChangedEvent(firstName));
        }

        @EventHandler
        protected void onFirstNameChanged(final TestFirstNameChangedEvent event) {
            this.firstName = event.getFirstName();
        }

        public void changeLastName(final Context context, final String lastName) {
            apply(new TestLastNameChangedEvent(lastName));
        }

        @EventHandler
        protected void onLastNameChanged(final TestLastNameChangedEvent event) {
            this.lastName = event.getLastName();
        }

    }

    public static class TestRepository extends Repository<TestAggregate> {

        private final Map<String, TestAggregate> store = Maps.newHashMap();

        private String key(KasperID id, Long expectedVersion) {
            String key = id.toString();
            if (null != expectedVersion) {
                key += "_" + String.valueOf(expectedVersion);
            }
            return key;
        }

        @Override
        protected Optional<TestAggregate> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
            return Optional.fromNullable(store.get(key(aggregateIdentifier, expectedVersion)));
        }

        @Override
        protected void doSave(TestAggregate aggregate) {
            store.put(key(aggregate.getEntityId(), aggregate.getVersion()), aggregate);
        }

        @Override
        protected void doDelete(TestAggregate aggregate) {
            store.remove(key(aggregate.getEntityId(), aggregate.getVersion()));
        }

    }

    public static class TestEventRepository extends EventSourcedRepository<TestAggregate> {
        /* During tests, do not use constructor with event store */
        public TestEventRepository() {
            super();
        }
    }

    @XKasperCommandHandler( domain = TestDomain.class )
    public static class TestCreateCommandHandler
            extends EntityCommandHandler<TestCreateCommand, TestAggregate> {

        public CommandResponse handle(final KasperCommandMessage<TestCreateCommand> message) throws Exception {

            final TestAggregate agr = new TestAggregate(message.getCommand().getIdToUse());

            agr.changeFirstName(message.getContext(), message.getCommand().getFirstName());

            this.getRepository().add(agr);

            return CommandResponse.ok();
        }
    }

    @XKasperCommandHandler( domain = TestDomain.class )
    public static class TestChangeLastNameCommandHandler
            extends EntityCommandHandler<TestChangeLastNameCommand, TestAggregate> {

        public CommandResponse handle(final KasperCommandMessage<TestChangeLastNameCommand> message) throws Exception {

            final Optional<TestAggregate> agr =
                    this.getRepository().load(
                            message.getCommand().getId(),
                            message.getCommand().getVersion()
                    );

            if (agr.isPresent()) {
                agr.get().changeLastName(message.getContext(), message.getCommand().getLastName());
            } else {
                return CommandResponse.error(CoreReasonCode.NOT_FOUND, "not found");
            }

            return CommandResponse.ok();
        }

    }

    public static class TestQuery implements Query {
        @NotNull
        private final String type;
        TestQuery(final String type) {
            this.type = type;
        }
        String getType() {
            return this.type;
        }
    }

    public static class TestResult implements QueryResult {
        private final String answer;
        TestResult(final String answer) {
            this.answer = answer;
        }
        String getAnswer() {
            return this.answer;
        }
        public int hashCode() { return answer.hashCode(); }
        public boolean equals(final Object obj) {
            if (this == checkNotNull(obj)) { return true; }
            if (!getClass().equals(obj.getClass())) { return false; }
            return ((TestResult) obj).answer.equals(answer);
        }
        public String toString() { return this.answer; }
    }

    @XKasperQueryHandler( domain = TestDomain.class )
    public static class TestGetSomeData extends QueryHandler<TestQuery, TestResult> {
        public QueryResponse<TestResult> retrieve(final TestQuery query) throws Exception {
            if (query.getType().contentEquals("REFUSED")) {
                return QueryResponse.refused(new KasperReason("REFUSED", "Go To Hell"));
            } else if (query.getType().contentEquals("ERROR")) {
                return QueryResponse.error(new KasperReason("ERROR", "I'm bad"));
            }
            return QueryResponse.of(new TestResult("42"));
        }
    }

    public static DomainBundle getDomainBundle() {
        return new DefaultDomainBundle(
                  Lists.<CommandHandler>newArrayList( new TestCreateCommandHandler(), new TestChangeLastNameCommandHandler())
                , Lists.<QueryHandler>newArrayList( new TestGetSomeData())
                , Lists.<Repository>newArrayList(new TestRepository())
                , Lists.<EventListener>newArrayList()
                , new TestDomain()
                , "TestDomain"
        );
    }
}
