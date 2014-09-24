// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.mgt.impl;

import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import com.viadeo.kasper.security.exception.KasperSecurityException;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class DefaultAuthorizationSecurityManagerUTest {

    private DefaultAuthorizationSecurityManager defaultAuthorizationSecurityManager;

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

    private OpenUser actor = new OpenUser("Kasper", "User");

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
        final WildcardPermission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        final List<WildcardPermission> permissions = new ArrayList<WildcardPermission>();
        permissions.add(permission);
        actor._setPermissions(permissions);

        // When
        defaultAuthorizationSecurityManager.checkPermission(perm, actor);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_chekPermission_withWrongPermission_shouldThrowException() throws KasperSecurityException {
        // Given
        final String perm = "perm1";
        final String wrongPerm = "wrongperm1";
        final WildcardPermission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        final List<WildcardPermission> permissions = new ArrayList<WildcardPermission>();
        permissions.add(permission);
        actor._setPermissions(permissions);

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
        actor._setRoles(roles);

        // When
        defaultAuthorizationSecurityManager.checkRole(roleStr, actor);
    }

    @Test
    public void test_chekPermission_withGoodPermissionInRole_shouldNotThrowException() throws KasperSecurityException {
        // Given
        final String roleStr = "Robert";
        final String perm = "perm1";
        final OpenRole role = new OpenRole(roleStr);
        final WildcardPermission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        role._add(permission);
        final List<Role> roles = new ArrayList<>();
        roles.add(role);
        actor._addRoles(roles);

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
        actor._setRoles(roles);

        // When
        defaultAuthorizationSecurityManager.checkRole(wrongRoleStr, actor);
    }

    @Test(expected = KasperUnauthorizedException.class)
    public void test_chekPermission_withWrongPermissionInRole_shouldThrowException() throws KasperSecurityException {
        // Given
        final String roleStr = "Robert";
        final String perm = "perm1";
        final String wrongPerm = "wrongperm1";
        final OpenRole role = new OpenRole(roleStr);
        final WildcardPermission permission = defaultAuthorizationSecurityManager.resolvePermission(perm);
        role._add(permission);
        final List<Role> roles = new ArrayList<>();
        roles.add(role);
        actor._addRoles(roles);

        // When
        defaultAuthorizationSecurityManager.checkPermission(wrongPerm, actor);
    }

}
