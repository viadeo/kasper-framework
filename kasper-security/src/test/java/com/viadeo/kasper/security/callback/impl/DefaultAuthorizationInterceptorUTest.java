// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.callback.impl;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.annotation.XKasperCommand;
import com.viadeo.kasper.security.annotation.XKasperRequirePermissions;
import com.viadeo.kasper.security.annotation.XKasperRequireRoles;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.authz.mgt.AuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.mgt.impl.DefaultAuthorizationSecurityManager;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class DefaultAuthorizationInterceptorUTest {

    private DefaultAuthorizationInterceptor interceptor;

    @Mock
    private Object payload;

    @Mock
    private InterceptorChain chain;


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
        authorizationSecurityManager = new DefaultAuthorizationSecurityManager();
        interceptor = new DefaultAuthorizationInterceptor(new TypeToken<Collection<String>>() {}, authorizationSecurityManager, authorizationStorage);
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_extractRoles_withNull_shouldReturnEmpty() {
        // Given

        // When
        final List<String> result = interceptor.extractRoles(TestRoleCommand.class);

        // Then
        assertEquals(result.size(), 1);
        assertEquals("perm1,perm2,perm3,perm4", result.get(0));
    }

    @Test
    public void test_extractMultiplesRoles_withNull_shouldReturnEmpty() {
        // Given

        // When
        final List<String> result = interceptor.extractRoles(TestMultiplesRolesCommand.class);

        // Then
        assertEquals(result.size(), 2);
        assertEquals("perm1,perm2", result.get(0));
        assertEquals("perm3,perm4", result.get(1));
    }

    @Test
    public void test_extractPermissions_withNull_shouldReturnEmpty() {
        // Given

        // When
        final List<String> result = interceptor.extractPermissions(TestPermissionCommand.class);

        // Then
        assertEquals(result.size(), 1);
        assertEquals("perm1,perm2,perm3,perm4", result.get(0));
    }

    @Test
    public void test_extractComplexePermissions_withNull_shouldReturnEmpty() {
        // Given

        // When
        final List<String> result = interceptor.extractPermissions(TestComplexePermissionCommand.class);

        // Then
        assertEquals(result.size(), 1);
        assertEquals("groups:897452:post:newsletter", result.get(0));
    }

    @Test
    public void test_validate_withGoodSubjectAndGoodRole_shouldGoThrough() throws Exception {
        // Given
        final Optional<OpenUser> actor = initTestUser();
        final Context context = new DefaultContext();
        doReturn(actor).when(authorizationStorage).getActor(context);
        interceptor = new DefaultAuthorizationInterceptor(TypeToken.of(TestSimpleRoleCommand.class), authorizationSecurityManager, authorizationStorage);

        // When
        interceptor.process(payload, context, chain);
    }

    @Test
    public void test_validate_withGoodSubjectAndGoodPermissionFromRole_shouldGoThrough() throws Exception{
        // Given
        final Optional<OpenUser> actor = initTestUser();
        final Context context = new DefaultContext();
        doReturn(actor).when(authorizationStorage).getActor(context);
        interceptor = new DefaultAuthorizationInterceptor(TypeToken.of(TestSimplePermissionCommand.class), authorizationSecurityManager, authorizationStorage);

        // When
        interceptor.process(payload, context, chain);
    }

    @Test
    public void test_validate_withGoodSubjectAndGoodPermission_shouldGoThrough() throws Exception{
        // Given
        final Optional<OpenUser> actor = initTestUser();
        actor.get()._setPermissions(actor.get().getRoles().get(0).getPermissions());
        actor.get()._setRoles(new ArrayList<Role>());
        final Context context = new DefaultContext();
        doReturn(actor).when(authorizationStorage).getActor(context);
        interceptor = new DefaultAuthorizationInterceptor(TypeToken.of(TestSimplePermissionCommand.class), authorizationSecurityManager, authorizationStorage);

        // When
        interceptor.process(payload, context, chain);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_validate_withGoodSubjectAndWrongRole_shouldThrowException() throws Exception{
        // Given
        final Optional<OpenUser> actor = initTestUser();
        final Context context = new DefaultContext();
        doReturn(actor).when(authorizationStorage).getActor(context);
        interceptor = new DefaultAuthorizationInterceptor(TypeToken.of(TestRoleCommand.class), authorizationSecurityManager, authorizationStorage);

        // When
        interceptor.process(payload, context, chain);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_validate_withGoodSubjectAndWrongPermission_shouldThrowException() throws Exception{
        // Given
        final Optional<OpenUser> actor = initTestUser();
        final Context context = new DefaultContext();
        doReturn(actor).when(authorizationStorage).getActor(context);
        interceptor = new DefaultAuthorizationInterceptor(TypeToken.of(TestPermissionCommand.class), authorizationSecurityManager, authorizationStorage);

        // When
        interceptor.process(payload, context, chain);
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

}
