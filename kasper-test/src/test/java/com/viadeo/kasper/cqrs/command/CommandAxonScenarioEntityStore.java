// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.command.impl.AbstractCreateCommand;
import com.viadeo.kasper.cqrs.command.impl.AbstractEntityCommandHandler;
import com.viadeo.kasper.cqrs.command.impl.AbstractUpdateCommand;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.impl.EventSourcedRepository;
import com.viadeo.kasper.ddd.impl.Repository;
import com.viadeo.kasper.er.impl.AbstractRootConcept;
import com.viadeo.kasper.event.domain.impl.AbstractEntityCreatedEvent;
import com.viadeo.kasper.event.domain.impl.AbstractEntityUpdatedEvent;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.test.ddd.KasperRepositoryTest;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.AggregateDeletedException;
import org.axonframework.eventstore.EventStore;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.viadeo.kasper.test.event.EventMatcher.anyDate;
import static com.viadeo.kasper.test.event.EventMatcher.equalToEvent;
import static org.axonframework.test.matchers.Matchers.*;

@RunWith(Parameterized.class)
public class CommandAxonScenarioEntityStore {

    private boolean repositoryIsMocked = false;
    private FixtureConfiguration<TestAggregate> fixture;
    private IRepository<TestAggregate> testRepository;

    // ------------------------------------------------------------------------

    @XKasperUnregistered
    public static class TestDomain implements Domain { }

    @XKasperUnregistered
    public static class TestCreateCommand extends AbstractCreateCommand {
        private final String firstName;
        public TestCreateCommand(KasperID providedId, String firstName) {
            super(providedId);
            this.firstName = firstName;
        }
        public String getFirstName() {
            return this.firstName;
        }
    }

    @XKasperUnregistered
    public static class TestChangeLastNameCommand extends AbstractUpdateCommand {
        private final String lastName;
        public TestChangeLastNameCommand(KasperID providedId, String lastName) {
            super(providedId);
            this.lastName = lastName;
        }
        public String getLastName() {
            return this.lastName;
        }
    }

    @XKasperUnregistered
    public static class TestCreatedEvent extends AbstractEntityCreatedEvent<TestDomain> {
        protected TestCreatedEvent(Context context, KasperID id, DateTime lastModificationDate) {
            super(context, id, lastModificationDate);
        }
    }

    @XKasperUnregistered
    public static class TestFirstNameChangedEvent extends AbstractEntityUpdatedEvent<TestDomain> {
        private final String firstName;
        protected TestFirstNameChangedEvent(
                Context context, KasperID id, Long version,
                DateTime lastModificationDate, String firstName) {
            super(context, id, version, lastModificationDate);
            this.firstName = firstName;
        }
        public String getFirstName() {
            return this.firstName;
        }
    }

    @XKasperUnregistered
    public static class TestLastNameChangedEvent extends AbstractEntityUpdatedEvent<TestDomain> {
        private final String lastName;
        protected TestLastNameChangedEvent(
                Context context, KasperID id, Long version,
                DateTime lastModificationDate, String lastName) {
            super(context, id, version, lastModificationDate);
            this.lastName = lastName;
        }
        public String getLastName() {
            return this.lastName;
        }
    }

    @XKasperUnregistered
    public static class TestAggregate extends AbstractRootConcept {

        private String firstName = "unknown";
        private String lastName = "unknown";

        TestAggregate() { }

        TestAggregate(final Context context, final KasperID id) {
            apply(new TestCreatedEvent(context, id, DateTime.now()));
        }

        @EventHandler
        protected void onCreated(final TestCreatedEvent event) {
            setId(event.getEntityId());
            setCreationDate(event.getEntityLastModificationDate());
        }

        public void changeFirstName(final Context context, final String firstName) {
            apply(new TestFirstNameChangedEvent(
                    context, this.getEntityId(), this.getVersion(), DateTime.now(), firstName
            ));
        }

        @EventHandler
        protected void onFirstNameChanged(final TestFirstNameChangedEvent event) {
            this.firstName = event.getFirstName();
            setModificationDate(event.getEntityLastModificationDate());
        }

        public void changeLastName(final Context context, final String lastName) {
            apply(new TestLastNameChangedEvent(
                    context, this.getEntityId(), this.getVersion(), DateTime.now(), lastName
            ));
        }

        @EventHandler
        protected void onLastNameChanged(final TestLastNameChangedEvent event) {
            this.lastName = event.getLastName();
            setModificationDate(event.getEntityLastModificationDate());
        }

    }

    @XKasperUnregistered
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

    @XKasperUnregistered
    public static class TestEventRepository extends EventSourcedRepository<TestAggregate> {
        /* During tests, do not use constructor with event store */
        protected TestEventRepository() {
            super();
        }
    }

    @XKasperUnregistered
    public static class TestCreateCommandHandler
            extends AbstractEntityCommandHandler<TestCreateCommand, TestAggregate> {

        public CommandResponse handle(final KasperCommandMessage<TestCreateCommand> message) throws Exception {

            final TestAggregate agr =
                    new TestAggregate(message.getContext(), message.getCommand().getIdToUse());

            agr.changeFirstName(message.getContext(), message.getCommand().getFirstName());

            this.getRepository().add(agr);

            return CommandResponse.ok();
        }
    }

    @XKasperUnregistered
    public static class TestChangeLastNameCommandHandler
            extends AbstractEntityCommandHandler<TestChangeLastNameCommand, TestAggregate> {

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

    // ========================================================================

    @Parameterized.Parameters
    public static Collection repositories() {
        return Arrays.asList(new Object[][] {
                { new TestRepository() },
                { new TestEventRepository() },
                { null /* will use an axon-mocked repository*/ }
        });
    }

    public CommandAxonScenarioEntityStore(final IRepository testRepository) {
        if (null != testRepository) {
            this.testRepository = testRepository;
        }
    }

    @Before
    public void resetFixture() {
        this.fixture = Fixtures.newGivenWhenThenFixture(TestAggregate.class);
        fixture.setReportIllegalStateChange(true);

        if ((null == this.testRepository) || (repositoryIsMocked)) {
            /* use the Axon-mocked repository */
            this.testRepository = KasperRepositoryTest.mockAs(fixture, new TestRepository());
            repositoryIsMocked = true;
        }

        if (Repository.class.isAssignableFrom(this.testRepository.getClass())) {
            ((Repository) this.testRepository).setEventStore(fixture.getEventStore());
            ((Repository) this.testRepository).setEventBus(fixture.getEventBus());
        }
    }

    // ========================================================================

    @Test
    public void testSimpleCreation() {

        // Given a creation command handler instance
        final TestCreateCommandHandler createHandler = new TestCreateCommandHandler();
        createHandler.setRepository(this.testRepository);
        fixture.registerCommandHandler(TestCreateCommand.class, createHandler);

        // Given a command context
        final Context context = DefaultContextBuilder.get();
        final Map<String, Object> metaContext = new HashMap<String, Object>() {{
            this.put(Context.METANAME, context);
        }};

        // Given a creation command
        final KasperID createId = DefaultKasperId.random();
        final String firstName = "Richard";
        final TestCreateCommand createCommand = new TestCreateCommand(
                createId,
                firstName
        );

        // When command is made, Then we expect creation and first name changing events
        fixture
                .given()
                .when(createCommand, metaContext)
                .expectReturnValue(CommandResponse.ok())
                .expectEventsMatching(payloadsMatching(exactSequenceOf(
                        equalToEvent(new TestCreatedEvent(
                                context, createId, anyDate()
                        )),
                        equalToEvent(new TestFirstNameChangedEvent(
                                context, createId, 0L, anyDate(), firstName
                        )),
                        andNoMore()
                )));

    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleUpdateAfterCreateCommand() {

        // Given an update command handler instance
        final TestChangeLastNameCommandHandler updateHandler = new TestChangeLastNameCommandHandler();
        updateHandler.setRepository(this.testRepository);
        fixture.registerCommandHandler(TestChangeLastNameCommand.class, updateHandler);

        // Given a command context
        final Context context = DefaultContextBuilder.get();
        final Map<String, Object> metaContext = new HashMap<String, Object>() {{
            this.put(Context.METANAME, context);
        }};

        // Given an update command
        final KasperID aggregateId = DefaultKasperId.random();
        final String lastName = "Richard";
        final TestChangeLastNameCommand updateCommand = new TestChangeLastNameCommand(
                aggregateId,
                lastName
        );

        // Given a creation command handler instance - for non event-sourcing repositories
        final TestCreateCommandHandler createHandler = new TestCreateCommandHandler();
        createHandler.setRepository(this.testRepository);
        fixture.registerCommandHandler(TestCreateCommand.class, createHandler);

        // Given an initial firstName
        final String firstName = "Richard";

        // When command is made, Then we expect creation and first name changing events
        fixture
                .givenCommands(
                    new TestCreateCommand(
                            aggregateId,
                            firstName
                    )
                )
                .when(updateCommand, metaContext)
                .expectReturnValue(CommandResponse.ok())
                .expectEventsMatching(payloadsMatching(exactSequenceOf(
                        equalToEvent(new TestLastNameChangedEvent(
                                context, aggregateId, 0L, anyDate(), lastName
                        )),
                        andNoMore()
                )));

    }

    // ------------------------------------------------------------------------

    @Test
    public void testSimpleUpdateAfterCreateEvents() {

        /**
         * Non-event sourced repositories cannot handle "given" events
         */
        if ( ! EventSourcedRepository.class.isAssignableFrom(this.testRepository.getClass())) {
            return;
        }

        // Given an update command handler instance
        final TestChangeLastNameCommandHandler updateHandler = new TestChangeLastNameCommandHandler();
        updateHandler.setRepository(this.testRepository);
        fixture.registerCommandHandler(TestChangeLastNameCommand.class, updateHandler);

        // Given a command context
        final Context context = DefaultContextBuilder.get();
        final Map<String, Object> metaContext = new HashMap<String, Object>() {{
            this.put(Context.METANAME, context);
        }};

        // Given an update command
        final KasperID aggregateId = DefaultKasperId.random();
        final String lastName = "Richard";
        final TestChangeLastNameCommand updateCommand = new TestChangeLastNameCommand(
                aggregateId,
                lastName
        );

        // Given an initial firstName
        final String firstName = "Richard";

        // When command is made, Then we expect creation and first name changing events
        fixture
                .given(
                        new TestCreatedEvent(
                                context, aggregateId, anyDate()
                        ),
                        new TestFirstNameChangedEvent(
                            context, aggregateId, 0L, anyDate(), firstName
                        )
                )
                .when(updateCommand, metaContext)
                .expectReturnValue(CommandResponse.ok())
                .expectEventsMatching(payloadsMatching(exactSequenceOf(
                        equalToEvent(new TestLastNameChangedEvent(
                                context, aggregateId, 0L, anyDate(), lastName
                        )),
                        andNoMore()
                )));

    }

}
