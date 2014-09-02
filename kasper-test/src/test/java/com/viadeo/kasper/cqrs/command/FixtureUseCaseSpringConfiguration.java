// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import org.axonframework.eventstore.EventStore;
import org.springframework.context.annotation.Bean;

import static com.viadeo.kasper.cqrs.command.FixtureUseCase.*;

public class FixtureUseCaseSpringConfiguration {

    @Bean
    public TestDomain domain() {
        return new TestDomain();
    }

    @Bean
    public TestRepository repository() {
        return new TestRepository();
    }

    @Bean
    public TestEventRepository eventRepository(EventStore eventStore) {
        return new TestEventRepository(eventStore);
    }

    @Bean
    public TestCreateCommandHandler createCommandHandler() {
        return new TestCreateCommandHandler();
    }

    @Bean
    public TestCoreReasonCodeCommandHandler coreReasonCodeCommandHandler() {
        return new TestCoreReasonCodeCommandHandler();
    }

    @Bean
    public TestCommandHandler commandHandler() {
        return new TestCommandHandler();
    }


    @Bean
    public TestChangeLastNameCommandHandler changeLastNameCommandHandler() {
        return new TestChangeLastNameCommandHandler();
    }

    @Bean
    public TestGetSomeDataQueryHandler queryHandler() {
        return new TestGetSomeDataQueryHandler();
    }

    @Bean
    public TestCoreReasonCodeQueryHandler coreReasonCodeQueryHandler() {
        return new TestCoreReasonCodeQueryHandler();
    }

    @Bean
    public TestCreatedEventListener createdEventListener() {
        return new TestCreatedEventListener();
    }

    @Bean
    public TestFirstNameChangedEventListener firstNameChangedEventListener() {
        return new TestFirstNameChangedEventListener();
    }

    @Bean
    public TestLastNameChangedEventListener lastNameChangedEventListener() {
        return new TestLastNameChangedEventListener();
    }

    @Bean
    public DoSyncUserEventListener doSyncUserEventListener() {
        return new DoSyncUserEventListener();
    }

    @Bean
    public TestCreateUserCommandHandler createUserCommandHandler() {
        return new TestCreateUserCommandHandler();
    }

}
