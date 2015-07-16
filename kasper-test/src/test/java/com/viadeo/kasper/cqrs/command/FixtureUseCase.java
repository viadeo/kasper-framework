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
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.command.CreateCommand;
import com.viadeo.kasper.api.component.command.UpdateCommand;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.EntityCommandHandler;
import com.viadeo.kasper.core.component.command.KasperCommandMessage;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.repository.EventSourcedRepository;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.event.CommandEventListener;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.EventListener;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.component.event.EntityCreatedEvent;
import com.viadeo.kasper.api.component.event.EntityUpdatedEvent;
import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.api.id.DefaultKasperId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventstore.EventStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class FixtureUseCase {

    public static class TestDomain implements Domain { }

    public static class TestCreateCommand extends CreateCommand {

        private static final long serialVersionUID = 4487842725516232104L;

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

        private static final long serialVersionUID = -4871267299717519334L;

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
        private static final long serialVersionUID = -5387545841270513825L;

        protected TestCreatedEvent(KasperID id) {
            super(id);
        }
    }

    public static class TestFirstNameChangedEvent extends EntityUpdatedEvent<TestDomain> {
        private static final long serialVersionUID = -3951590352755201574L;

        private final String firstName;
        protected TestFirstNameChangedEvent(String firstName) {
            this.firstName = firstName;
        }
        public String getFirstName() {
            return this.firstName;
        }
    }

    public static class TestLastNameChangedEvent extends EntityUpdatedEvent<TestDomain> {
        private static final long serialVersionUID = -3765684302496734855L;

        private final String lastName;
        protected TestLastNameChangedEvent(String lastName) {
            this.lastName = lastName;
        }
        public String getLastName() {
            return this.lastName;
        }
    }

    public static class TestCreatedEventListener extends EventListener<TestCreatedEvent> {
        @Override
        public EventResponse handle(Context context, TestCreatedEvent event) {
            return EventResponse.success();
        }
    }

    public static class TestFirstNameChangedEventListener extends EventListener<TestFirstNameChangedEvent> {
        @Override
        public EventResponse handle(Context context, TestFirstNameChangedEvent event) {
            return EventResponse.success();
        }
    }

    public static class TestLastNameChangedEventListener extends EventListener<TestLastNameChangedEvent> {
        @Override
        public EventResponse handle(Context context, TestLastNameChangedEvent event) {
            return EventResponse.success();
        }
    }

    public static class DoSyncUserEvent implements Event {
        private static final long serialVersionUID = -1686085684381486691L;
        public final String firstName;
        public final String lastName;

        public DoSyncUserEvent(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    public static class DoSyncUserEventListener extends CommandEventListener<DoSyncUserEvent> {
        private static final Logger LOGGER = LoggerFactory.getLogger(DoSyncUserEventListener.class);

        @Override
        public EventResponse handle(Context context, DoSyncUserEvent event) {
            final DefaultKasperId kasperId = DefaultKasperId.random();

            try {
                final CommandResponse response1 = getCommandGateway().get().sendCommandAndWaitForAResponse(
                        new TestCreateCommand(kasperId, event.firstName),
                        getContext()
                );

                if (response1.isOK()) {
                    getCommandGateway().get().sendCommandAndWaitForAResponse(
                            new TestChangeLastNameCommand(kasperId, event.lastName),
                            getContext()
                    );
                }

            } catch (Exception e) {
                LOGGER.error("Unexpected error", e);
            }
            return EventResponse.success();
        }
    }

    public static class TestAggregate extends Concept {
        private static final long serialVersionUID = -6659974540945049543L;

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
        public TestEventRepository() { }
        public TestEventRepository(EventStore eventStore) {
            super(eventStore);
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
        private static final long serialVersionUID = 2544486429078930789L;

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
        private static final long serialVersionUID = 4986629752879834584L;

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
    public static class TestGetSomeDataQueryHandler extends QueryHandler<TestQuery, TestResult> {
        public QueryResponse<TestResult> retrieve(final TestQuery query) throws Exception {
            if (query.getType().contentEquals("REFUSED")) {
                return QueryResponse.refused(new KasperReason("REFUSED", "Go To Hell"));
            } else if (query.getType().contentEquals("ERROR")) {
                return QueryResponse.error(new KasperReason("ERROR", "I'm bad"));
            }
            return QueryResponse.of(new TestResult("42"));
        }
    }

    public static class TestCoreReasonCodeQuery implements Query {

        private static final long serialVersionUID = 1881078049619434902L;

        @NotNull
        private final CoreReasonCode coreReasonCode;

        public TestCoreReasonCodeQuery(CoreReasonCode coreReasonCode) {
            this.coreReasonCode = coreReasonCode;
        }

        public CoreReasonCode getCoreReasonCode() {
            return coreReasonCode;
        }
    }

    @XKasperQueryHandler( domain = TestDomain.class )
    public static class TestCoreReasonCodeQueryHandler extends QueryHandler<TestCoreReasonCodeQuery, TestResult> {
        public QueryResponse<TestResult> retrieve(final TestCoreReasonCodeQuery query) throws Exception {
            return QueryResponse.error(query.getCoreReasonCode());
        }
    }

    public static class TestCoreReasonCodeCommand implements Command {
        private static final long serialVersionUID = 2036940809846760347L;

        @NotNull
        private final CoreReasonCode coreReasonCode;

        public TestCoreReasonCodeCommand(CoreReasonCode coreReasonCode) {
            this.coreReasonCode = coreReasonCode;
        }

        public CoreReasonCode getCoreReasonCode() {
            return coreReasonCode;
        }
    }

    @XKasperCommandHandler( domain = TestDomain.class )
    public static class TestCoreReasonCodeCommandHandler extends CommandHandler<TestCoreReasonCodeCommand> {
        @Override
        public CommandResponse handle(TestCoreReasonCodeCommand command) throws Exception {
            return CommandResponse.error(command.getCoreReasonCode());
        }
    }

    public static class TestCommand implements Command {
        private static final long serialVersionUID = -5960872900042393220L;

        @NotNull
        private final String type;
        TestCommand(final String type) {
            this.type = type;
        }
        String getType() {
            return this.type;
        }
    }

    @XKasperCommandHandler( domain = TestDomain.class )
    public static class TestCommandHandler extends CommandHandler<TestCommand> {
        @Override
        public CommandResponse handle(TestCommand command) throws Exception {
            if (command.getType().contentEquals("REFUSED")) {
                return CommandResponse.refused(new KasperReason("REFUSED", "Go To Hell"));
            } else if (command.getType().contentEquals("ERROR")) {
                return CommandResponse.error(new KasperReason("ERROR", "I'm bad"));
            }
            return CommandResponse.ok();
        }
    }

    public static class TestCreateUserCommand extends CreateCommand {

        private static final long serialVersionUID = -304218800458456173L;

        @NotNull
        public final String firstName;

        @NotNull
        public final String lastName;

        public TestCreateUserCommand(KasperID kasperID, String firstName, String lastName) {
            super(kasperID);
            this.firstName = firstName;
            this.lastName = lastName;
        }

    }

    @XKasperCommandHandler( domain = TestDomain.class )
    public static class TestCreateUserCommandHandler extends CommandHandler<TestCreateUserCommand> {

        @Override
        public CommandResponse handle(TestCreateUserCommand command) throws Exception {
            final CommandResponse response1 = getCommandGateway().sendCommandAndWaitForAResponse(
                    new TestCreateCommand(command.getIdToUse(), command.firstName),
                    getContext()
            );

            if (response1.isOK()) {
                return getCommandGateway().sendCommandAndWaitForAResponse(
                        new TestChangeLastNameCommand(command.getIdToUse(), command.lastName),
                        getContext()
                );
            } else {
                return response1;
            }
        }

    }

    public static DomainBundle getDomainBundle() {
        return new DefaultDomainBundle(
                  Lists.<CommandHandler>newArrayList(
                          new TestCreateCommandHandler(),
                          new TestChangeLastNameCommandHandler(),
                          new TestCoreReasonCodeCommandHandler(),
                          new TestCommandHandler(),
                          new TestCreateUserCommandHandler()
                  )
                , Lists.<QueryHandler>newArrayList(
                          new TestGetSomeDataQueryHandler(),
                          new TestCoreReasonCodeQueryHandler()
                )
                , Lists.<Repository>newArrayList(new TestRepository())
                , Lists.<EventListener>newArrayList(
                        new TestCreatedEventListener(),
                        new TestFirstNameChangedEventListener(),
                        new TestLastNameChangedEventListener(),
                        new DoSyncUserEventListener()
                )
                , Lists.<Saga>newArrayList()
                , Lists.<QueryInterceptorFactory>newArrayList()
                , Lists.<CommandInterceptorFactory>newArrayList()
                , Lists.<EventInterceptorFactory>newArrayList()
                , new TestDomain()
                , "TestDomain"
        );
    }

}
