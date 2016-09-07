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
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.command.CreateCommand;
import com.viadeo.kasper.api.component.command.UpdateCommand;
import com.viadeo.kasper.api.component.event.EntityCreatedEvent;
import com.viadeo.kasper.api.component.event.EntityUpdatedEvent;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.id.DefaultKasperId;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.component.command.AutowiredEntityCommandHandler;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.CommandMessage;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.repository.AutowiredEventSourcedRepository;
import com.viadeo.kasper.core.component.command.repository.AutowiredRepository;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.core.component.event.listener.CommandEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.query.AutowiredQueryHandler;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.platform.bundle.DefaultDomainBundle;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import org.axonframework.eventhandling.annotation.EventHandler;
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

    public static class TestCreatedEventListener extends AutowiredEventListener<TestCreatedEvent> {
        @Override
        public EventResponse handle(Context context, TestCreatedEvent event) {
            return EventResponse.success();
        }
    }

    public static class TestFirstNameChangedEventListener extends AutowiredEventListener<TestFirstNameChangedEvent> {
        @Override
        public EventResponse handle(Context context, TestFirstNameChangedEvent event) {
            return EventResponse.success();
        }
    }

    public static class TestLastNameChangedEventListener extends AutowiredEventListener<TestLastNameChangedEvent> {
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
                        context
                );

                if (response1.isOK()) {
                    getCommandGateway().get().sendCommandAndWaitForAResponse(
                            new TestChangeLastNameCommand(kasperId, event.lastName),
                            context
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


        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("firstName", firstName)
                    .add("lastName", lastName)
                    .toString();
        }
    }

    public static class TestRepository extends AutowiredRepository<KasperID,TestAggregate> {

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
            store.put(key(aggregate.getEntityId(), null), aggregate);
        }

        @Override
        protected void doDelete(TestAggregate aggregate) {
            store.remove(key(aggregate.getEntityId(), aggregate.getVersion()));
        }

    }

    public static class TestEventRepository extends AutowiredEventSourcedRepository<KasperID,TestAggregate> { }

    @XKasperCommandHandler( domain = TestDomain.class )
    public static class TestCreateCommandHandler
            extends AutowiredEntityCommandHandler<TestCreateCommand, TestAggregate> {

        @SuppressWarnings("unchecked")
        public CommandResponse handle(final CommandMessage<TestCreateCommand> message) {

            final TestAggregate agr = new TestAggregate(message.getCommand().getIdToUse());

            agr.changeFirstName(message.getContext(), message.getCommand().getFirstName());

            this.getRepository().add(agr);

            return CommandResponse.ok();
        }
    }

    @XKasperCommandHandler( domain = TestDomain.class )
    public static class TestChangeLastNameCommandHandler
            extends AutowiredEntityCommandHandler<TestChangeLastNameCommand, TestAggregate> {

        @SuppressWarnings("unchecked")
        public CommandResponse handle(final CommandMessage<TestChangeLastNameCommand> message) {

            final Optional<TestAggregate> agr =
                    this.getRepository().load(
                            message.getCommand().getId(),
                            message.getCommand().getVersion().orNull()
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
    public static class TestGetSomeDataQueryHandler extends AutowiredQueryHandler<TestQuery, TestResult> {
        public QueryResponse<TestResult> handle(final TestQuery query) {
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
    public static class TestCoreReasonCodeQueryHandler extends AutowiredQueryHandler<TestCoreReasonCodeQuery, TestResult> {
        public QueryResponse<TestResult> handle(final TestCoreReasonCodeQuery query) {
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
    public static class TestCoreReasonCodeCommandHandler extends AutowiredCommandHandler<TestCoreReasonCodeCommand> {
        @Override
        public CommandResponse handle(TestCoreReasonCodeCommand command) {
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
    public static class TestCommandHandler extends AutowiredCommandHandler<TestCommand> {
        @Override
        public CommandResponse handle(TestCommand command) {
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
    public static class TestCreateUserCommandHandler extends AutowiredCommandHandler<TestCreateUserCommand> {

        @Override
        public CommandResponse handle(Context context, TestCreateUserCommand command) {
            try {
                final CommandResponse response1 = getCommandGateway().sendCommandAndWaitForAResponse(
                        new TestCreateCommand(command.getIdToUse(), command.firstName),
                        context
                );

                if (response1.isOK()) {
                    return getCommandGateway().sendCommandAndWaitForAResponse(
                            new TestChangeLastNameCommand(command.getIdToUse(), command.lastName),
                            context
                    );
                } else {
                    return response1;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
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
                , Lists.<Repository>newArrayList(
                        new TestRepository(),
                        new TestEventRepository()
                )
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
