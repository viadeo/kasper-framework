// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.callback.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.security.annotation.XKasperRequirePermissions;
import com.viadeo.kasper.security.annotation.XKasperRequireRoles;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.mgt.impl.DefaultAuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;
import com.viadeo.kasper.security.callback.*;
import com.viadeo.kasper.security.configuration.SecurityConfiguration;
import com.viadeo.kasper.security.exception.KasperSecurityException;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;
import com.viadeo.kasper.security.strategy.SecurityStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class DefaultAuthorizationValidatorUTest {

    @MockitoAnnotations.Mock
    AuthorizationSecurityManager authorizationSecurityManager;

    @MockitoAnnotations.Mock
    AuthorizationStorage authorizationStorage;

    @XKasperRequireRoles("perm1,perm2,perm3,perm4")
    @XKasperCommand
    private static class TestRoleCommand implements Command {
    }

    @XKasperRequireRoles({"perm1,perm2", "perm3,perm4"})
    @XKasperCommand
    private static class TestMultiplesRolesCommand implements Command {
    }

    @XKasperRequirePermissions("perm1,perm2,perm3,perm4")
    @XKasperCommand
    private static class TestPermissionCommand implements Command {
    }

    @XKasperRequirePermissions("groups:897452:post:newsletter")
    @XKasperCommand
    private static class TestComplexePermissionCommand implements Command {
    }

    @XKasperRequireRoles("Robert")
    @XKasperCommand
    private static class TestSimpleRoleCommand implements Command {
    }

    @XKasperRequirePermissions("perm5")
    @XKasperCommand
    private static class TestSimplePermissionCommand implements Command {
    }

    DefaultAuthorizationValidator defaultAuthorizationValidator;

    private class OpenUser extends User {
        public OpenUser(String firstName, String lastName) {
            super(firstName, lastName);
        }
        protected void _addRoles(final Collection<Role> roles) {
            super.addRoles(roles);
        }
        protected void _setRoles(final List<Role> roles) {
            super.setRoles(roles);
        }
        protected void _setPermissions(final List<WildcardPermission> permissions) {
            super.setPermissions(permissions);
        }
    }

    private class OpenRole extends Role {
        public OpenRole(String name) {
            super(name);
        }
        public void _add(WildcardPermission permission) {
            super.add(permission);
        }
    }

    // ------------------------------------------------------------------------

    @Before
    public void setUp() {
        authorizationSecurityManager = spy(new DefaultAuthorizationSecurityManager());
        defaultAuthorizationValidator = new DefaultAuthorizationValidator(authorizationSecurityManager,authorizationStorage);
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_extractRoles_withNull_shouldReturnEmpty() {
        // Given

        // When
        final List<String> result = defaultAuthorizationValidator.extractRoles(TestRoleCommand.class);

        // Then
        assertEquals(result.size(), 1);
        assertEquals("perm1,perm2,perm3,perm4", result.get(0));
    }

    @Test
    public void test_extractMultiplesRoles_withNull_shouldReturnEmpty() {
        // Given

        // When
        final List<String> result = defaultAuthorizationValidator.extractRoles(TestMultiplesRolesCommand.class);

        // Then
        assertEquals(result.size(), 2);
        assertEquals("perm1,perm2", result.get(0));
        assertEquals("perm3,perm4", result.get(1));
    }

    @Test
    public void test_extractPermissions_withNull_shouldReturnEmpty() {
        // Given

        // When
        final List<String> result = defaultAuthorizationValidator.extractPermissions(TestPermissionCommand.class);

        // Then
        assertEquals(result.size(), 1);
        assertEquals("perm1,perm2,perm3,perm4", result.get(0));
    }

    @Test
    public void test_extractComplexePermissions_withNull_shouldReturnEmpty() {
        // Given

        // When
        final List<String> result = defaultAuthorizationValidator.extractPermissions(TestComplexePermissionCommand.class);

        // Then
        assertEquals(result.size(), 1);
        assertEquals("groups:897452:post:newsletter", result.get(0));
    }

    @Test
    public void test_validate_withGoodSubjectAndGoodRole_shouldGoThrough()  throws KasperSecurityException {
        // Given
        final Optional<OpenUser> actor = initTestUser();
        final Context context = new DefaultContext();
        doReturn(actor).when(authorizationStorage).getActor(context);

        // When
        defaultAuthorizationValidator.validate(context, TestSimpleRoleCommand.class);
    }

    @Test
    public void test_validate_withGoodSubjectAndGoodPermissionFromRole_shouldGoThrough() throws KasperSecurityException{
        // Given
        final Optional<OpenUser> actor = initTestUser();
        final Context context = new DefaultContext();
        doReturn(actor).when(authorizationStorage).getActor(context);

        // When
        defaultAuthorizationValidator.validate(context, TestSimplePermissionCommand.class);
    }

    @Test
    public void test_validate_withGoodSubjectAndGoodPermission_shouldGoThrough() throws KasperSecurityException{
        // Given
        final Optional<OpenUser> actor = initTestUser();
        actor.get()._setPermissions(actor.get().getRoles().get(0).getPermissions());
        actor.get()._setRoles(new ArrayList<Role>());
        final Context context = new DefaultContext();
        doReturn(actor).when(authorizationStorage).getActor(context);

        // When
        defaultAuthorizationValidator.validate(context, TestSimplePermissionCommand.class);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_validate_withGoodSubjectAndWrongRole_shouldThrowException() throws KasperSecurityException{
        // Given
        final Optional<OpenUser> actor = initTestUser();
        final Context context = new DefaultContext();
        doReturn(actor).when(authorizationStorage).getActor(context);

        // When
        defaultAuthorizationValidator.validate(context, TestRoleCommand.class);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_validate_withGoodSubjectAndWrongPermission_shouldThrowException() throws KasperSecurityException{
        // Given
        final Optional<OpenUser> actor = initTestUser();
        final Context context = new DefaultContext();
        doReturn(actor).when(authorizationStorage).getActor(context);

        // When
        defaultAuthorizationValidator.validate(context, TestPermissionCommand.class);
    }

    // ------------------------------------------------------------------------

    private Optional<OpenUser> initTestUser(){
        final OpenUser actor = new OpenUser("Kasper", "Robert");
        final String roleStr = "Robert";
        final String perm = "perm5";

        final OpenRole role = new OpenRole(roleStr);

        final WildcardPermission permission = new WildcardPermission(perm);
        role._add(permission);

        final List<Role> roles = new ArrayList<>();
        roles.add(role);

        actor._addRoles(roles);
        return Optional.of(actor);
    }

    public static class SecurityStrategyUTest {

        @Mock
        SecurityTokenValidator tokenValidator;

        @Mock
        IdentityContextProvider identityProvider;

        @Mock
        ApplicationIdValidator applicationIdValidator;

        @Mock
        IpAddressValidator ipAddressValidator;

        @Mock
        AuthorizationValidator authorizationValidator;

        @Mock
        AuthorizationSecurityManager authorizationSecurityManager;

        SecurityConfiguration securityConfiguration;


        // ------------------------------------------------------------------------

        @Before
        public void setup() {
            initMocks(this);
            securityConfiguration = new SecurityConfiguration.Builder()
                    .withSecurityTokenValidator(tokenValidator)
                    .withIdentityProvider(identityProvider)
                    .withApplicationIdValidator(applicationIdValidator)
                    .withIpAddressValidator(ipAddressValidator)
                    .withAuthorizationValidator(authorizationValidator)
                    .build();
        }

        // ------------------------------------------------------------------------

        @Test
        public void applySecurityBeforeRequest_onNonPublicRequest_shouldInvokeAuthenticationCallbacks()
                throws Exception {

            // Given
            final com.viadeo.kasper.security.strategy.SecurityStrategy securityStrategy = new SecurityStrategy(securityConfiguration, QueryHandler.class);
            final Context context = mock(Context.class);

            // When
            securityStrategy.beforeRequest(context);

            // Then
            verify(tokenValidator).validate(refEq(context.getSecurityToken()));
            verify(identityProvider).provideIdentity(refEq(context));
            verify(applicationIdValidator).validate(context.getApplicationId());
            verify(ipAddressValidator).validate(context.getIpAddress());
            verify(authorizationValidator).validate(context, QueryHandler.class);
        }

        @Test
        public void applySecurityBeforeRequest_onPublicRequest_shouldNotInvokeAuthenticationCallbacks()
                throws Exception {

            // Given
            final com.viadeo.kasper.security.strategy.SecurityStrategy securityStrategy = new SecurityStrategy(securityConfiguration, QueryHandler.class);
            final Context context = mock(Context.class);

            // When
            securityStrategy.beforeRequest(context);

            // Then
            verifyZeroInteractions(tokenValidator);
            verify(identityProvider).provideIdentity(refEq(context));
            verify(applicationIdValidator).validate(context.getApplicationId());
            verify(ipAddressValidator).validate(context.getIpAddress());
            verify(authorizationValidator).validate(context, QueryHandler.class);
        }

    }
}
