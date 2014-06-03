// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.mgt.impl;

import com.viadeo.kasper.security.authz.actor.Actor;
import com.viadeo.kasper.security.authz.actor.User;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;
import com.viadeo.kasper.security.exception.KasperSecurityException;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class DefaultAuthorizationSecurityManagerTest {

    private DefaultAuthorizationSecurityManager defaultAuthorizationSecurityManager;

    private Actor actor = new User();

    // ------------------------------------------------------------------------

    @Before
    public void setUp() {
        this.defaultAuthorizationSecurityManager = new DefaultAuthorizationSecurityManager();
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_resolvePermission_shouldReturnPermission() throws KasperSecurityException {
        // Given
        final String perm = "perm1,perm2,perm3";

        // When
        final Permission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);

        // Then
        assertNotNull(permission);
    }

    @Test
    public void test_chekPermission_withGoodPermission_shouldNotThrowException() throws KasperSecurityException {
        // Given
        final String perm = "perm1";
        final Permission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        final List<Permission> permissions = new ArrayList<>();
        permissions.add(permission);
        actor.setPermissions(permissions);

        // When
        defaultAuthorizationSecurityManager.checkPermission(perm, actor);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_chekPermission_withWrongPermission_shouldThrowException() throws KasperSecurityException {
        // Given
        final String perm = "perm1";
        final String wrongPerm = "wrongperm1";
        final Permission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        final List<Permission> permissions = new ArrayList<>();
        permissions.add(permission);
        actor.setPermissions(permissions);

        // When
        defaultAuthorizationSecurityManager.checkPermission(wrongPerm, actor);
    }

    @Test
    public void test_chekRole_withGoodRole_shouldNotThrowException() throws KasperSecurityException {
        // Given
        final String roleStr = "Robert";
        final Role role = new Role(roleStr);
        final List<Role> roles = new ArrayList<>();
        roles.add(role);
        actor.setRoles(roles);

        // When
        defaultAuthorizationSecurityManager.checkRole(roleStr, actor);
    }

    @Test
    public void test_chekPermission_withGoodPermissionInRole_shouldNotThrowException() throws KasperSecurityException {
        // Given
        final String roleStr = "Robert";
        final String perm = "perm1";
        final Role role = new Role(roleStr);
        final Permission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        role.add(permission);
        final List<Role> roles = new ArrayList<>();
        roles.add(role);
        actor.addRoles(roles);

        // When
        defaultAuthorizationSecurityManager.checkPermission(perm, actor);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_chekRole_withWrongRole_shouldThrowException() throws KasperSecurityException {
        // Given
        final String roleStr = "Robert";
        final String wrongRoleStr = "PasRobert";
        final Role role = new Role(roleStr);
        final List<Role> roles = new ArrayList<>();
        roles.add(role);
        actor.setRoles(roles);

        // When
        defaultAuthorizationSecurityManager.checkRole(wrongRoleStr, actor);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_chekPermission_withWrongPermissionInRole_shouldThrowException() throws KasperSecurityException {
        // Given
        final String roleStr = "Robert";
        final String perm = "perm1";
        final String wrongPerm = "wrongperm1";
        final Role role = new Role(roleStr);
        final Permission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        role.add(permission);
        final List<Role> roles = new ArrayList<>();
        roles.add(role);
        actor.addRoles(roles);

        // When
        defaultAuthorizationSecurityManager.checkPermission(wrongPerm, actor);
    }

}
