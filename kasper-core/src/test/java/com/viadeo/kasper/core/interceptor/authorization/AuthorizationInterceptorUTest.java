// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.authorization;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.api.annotation.XKasperAuthz;
import com.viadeo.kasper.api.annotation.XKasperCommand;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.command.AutowiredCommandHandler;
import com.viadeo.kasper.core.id.TestFormats;
import com.viadeo.kasper.core.interceptor.CompositeInterceptorFactory;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.core.interceptor.InterceptorFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresPermissions;
import static com.viadeo.kasper.core.component.annotation.XKasperAuthz.RequiresRoles;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationInterceptorUTest {

    static final AuthorizationManager NO_AUTHORIZATION = new AuthorizationManager() {
        @Override
        public boolean isPermitted(String[] permissions, CombinesWith combinesWith, ID actorID, Optional targetId) {
            return Boolean.FALSE;
        }

        @Override
        public boolean hasRole(String[] roles, CombinesWith combinesWith, ID actorID, Optional targetId) {
            return Boolean.TRUE;
        }
    };


    private Context context;
    private CompositeInterceptorFactory<Command, CommandResponse> factory;

    @Before
    public void setUp() {
        context = Contexts.builder()
                .withUserID(new ID("viadeo", "member", TestFormats.ID, 42))
                .build();

        final List<AuthorizationManager> authorizationManagers = Lists.newArrayList(
                NO_AUTHORIZATION,
                new OnlyForTestsAuthorizationManagerReturningTrue(),
                new OnlyForTestsAuthorizationManagerReturningFalse()
        );

        @SuppressWarnings("unchecked")
        List<InterceptorFactory<Command, CommandResponse>> interceptorFactories = Lists.newArrayList(
                new AuthorizationInterceptor.Factory<Command,CommandResponse>(
                        NO_AUTHORIZATION.getClass(),
                        authorizationManagers
                )
                ,
                new InterceptorFactory<Command, CommandResponse>() {
                    @Override
                    public Optional<InterceptorChain<Command, CommandResponse>> create(TypeToken<?> type) {
                        return Optional.of(
                                InterceptorChain.makeChain(new Interceptor<Command, CommandResponse>() {
                                    @Override
                                    public CommandResponse process(Command o, Context context, InterceptorChain chain) {
                                        return CommandResponse.accepted();
                                    }
                                }
                        ));
                    }
                }
        );

        factory = new CompositeInterceptorFactory<>(interceptorFactories);
    }

    @Test(expected = KasperInvalidAuthorizationException.class)
    public void process_for_command_withDefaultManager_shouldInstantiateAndReturnException() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = factory.create(TypeToken.of(TestCommandHandlerWithDefaultManager.class)).get();

        // When
        chain.next(new TestRoleCommand(), context);
    }

    @Test
    public void process_for_command_withGoodSubjectAndGoodRoles_shouldGoThrough() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = factory.create(TypeToken.of(TestRolesCommandHandlerWithGoodValues.class)).get();

        // When
        chain.next(new TestRoleCommand(), context);
    }

    @Test
    public void process_for_command_withGoodSubjectAndGoodPermissions_shouldGoThrough() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = factory.create(TypeToken.of(TestPermissionsCommandHandlerWithGoodValues.class)).get();

        // When
        chain.next(new TestRoleCommand(), context);
    }

    @Test(expected = KasperInvalidAuthorizationException.class)
    public void process_for_command_withGoodSubjectAndWrongRoles_shouldThrowAuthorizationException() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = factory.create(TypeToken.of(TestRolesCommandHandlerWithWrongValues.class)).get();

        // When
        chain.next(new TestRoleCommand(), context);
    }

    @Test(expected = KasperInvalidAuthorizationException.class)
    public void process_for_command_withGoodSubjectAndWrongPermissions_shouldThrowAuthorizationException() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = factory.create(TypeToken.of(TestPermissionsCommandHandlerWithWrongValues.class)).get();

        // When
        chain.next(new TestRoleCommand(), context);
    }

    @Test
    public void testGetTargetedId_withGoodCommand_shouldProcess() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = factory.create(TypeToken.of(TestRolesCommandHandlerWithGoodValues.class)).get();
        AuthorizationInterceptor<Command, CommandResponse> interceptor = (AuthorizationInterceptor<Command, CommandResponse>) chain.actor.get();
        TestRoleCommand command = new TestRoleCommand();

        // When
        Optional<Object> targetId = interceptor.getTargetedId(command);

        // Then
        assertTrue(targetId.isPresent());
        assertEquals(command.biduleId, targetId.get());
    }

    @Test
    public void testGetTargetedId_withGoodCommand_withNoTargetId_shouldProcess() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = factory.create(TypeToken.of(TestRolesCommandHandlerWithGoodValues.class)).get();
        AuthorizationInterceptor<Command, CommandResponse> interceptor = (AuthorizationInterceptor<Command, CommandResponse>) chain.actor.get();

        // When
        Optional<Object> targetId = interceptor.getTargetedId(new TestRoleCommandWithNoTargetId());

        // Then
        assertFalse(targetId.isPresent());
    }

    @Test(expected = KasperInvalidAuthorizationException.class)
    public void testGetTargetedId_withWrongCommand_shouldProcess() throws Exception {
        // Given
        InterceptorChain<Command, CommandResponse> chain = factory.create(TypeToken.of(TestRolesCommandHandlerWithGoodValues.class)).get();
        AuthorizationInterceptor<Command, CommandResponse> interceptor = (AuthorizationInterceptor<Command, CommandResponse>) chain.actor.get();

        // When
        interceptor.getTargetedId(new TestRoleCommandWithMultipleTargetId());
    }

    // ========================================================================

    @XKasperDomain(
            label = "Authorization",
            prefix = "sec",
            description = "The Authorization domain",
            owner = "Emmanuel Camper <ecamper@viadeoteam.com>"
    )
    public static class Authorization implements Domain {
    }

    @XKasperCommand
    private static class TestRoleCommand implements Command {
        @XKasperAuthz.TargetId
        public String biduleId = "54";
    }

    @XKasperCommand
    private static class TestRoleCommandWithNoTargetId implements Command {
        public String biduleId = "54";
    }

    @XKasperCommand
    private static class TestRoleCommandWithMultipleTargetId implements Command {
        @XKasperAuthz.TargetId
        public String biduleId = "54";
        @XKasperAuthz.TargetId
        public String machinId = "23";
    }

    @RequiresRoles(value = "role1,role2,role3,role4", manager = OnlyForTestsAuthorizationManagerReturningTrue.class)
    @XKasperCommandHandler(description = "", domain = Authorization.class)
    private static class TestRolesCommandHandlerWithGoodValues extends AutowiredCommandHandler<TestRoleCommand> {

    }

    @RequiresPermissions(value = "permission1,permission2", manager = OnlyForTestsAuthorizationManagerReturningTrue.class)
    @XKasperCommandHandler(description = "", domain = Authorization.class)
    private static class TestPermissionsCommandHandlerWithGoodValues extends AutowiredCommandHandler<TestRoleCommand> {

    }

    @RequiresRoles(value = "role1,role2,role3,role4", manager = OnlyForTestsAuthorizationManagerReturningFalse.class)
    @XKasperCommandHandler(description = "", domain = Authorization.class)
    private static class TestRolesCommandHandlerWithWrongValues extends AutowiredCommandHandler<TestRoleCommand> {

    }

    @RequiresPermissions(value = "permission1,permission2", manager = OnlyForTestsAuthorizationManagerReturningFalse.class)
    @XKasperCommandHandler(description = "", domain = Authorization.class)
    private static class TestPermissionsCommandHandlerWithWrongValues extends AutowiredCommandHandler<TestRoleCommand> {

    }

    @RequiresPermissions(value = "permission1,permission2")
    @XKasperCommandHandler(description = "", domain = Authorization.class)
    private static class TestCommandHandlerWithDefaultManager extends AutowiredCommandHandler<TestRoleCommand> {

    }

}
