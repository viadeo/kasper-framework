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
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.repository.EventSourcedRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.event.domain.EntityCreatedEvent;
import com.viadeo.kasper.event.domain.EntityUpdatedEvent;
import com.viadeo.kasper.impl.DefaultKasperId;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.test.FixtureConfiguration;
import org.axonframework.test.Fixtures;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
    public static class TestCreateCommand extends CreateCommand {
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

    @XKasperUnregistered
    public static class TestCreatedEvent extends EntityCreatedEvent<TestDomain> {
        protected TestCreatedEvent(KasperID id) {
            super(id);
        }
    }

    @XKasperUnregistered
    public static class TestFirstNameChangedEvent extends EntityUpdatedEvent<TestDomain> {
        private final String firstName;
        protected TestFirstNameChangedEvent(String firstName) {
            this.firstName = firstName;
        }
        public String getFirstName() {
            return this.firstName;
        }
    }

    @XKasperUnregistered
    public static class TestLastNameChangedEvent extends EntityUpdatedEvent<TestDomain> {
        private final String lastName;
        protected TestLastNameChangedEvent(String lastName) {
            this.lastName = lastName;
        }
        public String getLastName() {
            return this.lastName;
        }
    }

    @XKasperUnregistered
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
            extends EntityCommandHandler<TestCreateCommand, TestAggregate> {

        public CommandResponse handle(final KasperCommandMessage<TestCreateCommand> message) throws Exception {

            final TestAggregate agr = new TestAggregate(message.getCommand().getIdToUse());

            agr.changeFirstName(message.getContext(), message.getCommand().getFirstName());

            this.getRepository().add(agr);

            return CommandResponse.ok();
        }
    }

    @XKasperUnregistered
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

    // ========================================================================

    @Parameterized.Parameters
    public static Collection repositories() {
        return Arrays.asList(new Object[][] {
                { new TestRepository() },
                { new TestEventRepository() }
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
                        equalToEvent(new TestCreatedEvent(createId)),
                        equalToEvent(new TestFirstNameChangedEvent(firstName)),
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
                        equalToEvent(new TestLastNameChangedEvent(lastName)),
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
                        new TestCreatedEvent(aggregateId),
                        new TestFirstNameChangedEvent(firstName)
                )
                .when(updateCommand, metaContext)
                .expectReturnValue(CommandResponse.ok())
                .expectEventsMatching(payloadsMatching(exactSequenceOf(
                        equalToEvent(new TestLastNameChangedEvent(lastName)),
                        andNoMore()
                )));

    }

}
