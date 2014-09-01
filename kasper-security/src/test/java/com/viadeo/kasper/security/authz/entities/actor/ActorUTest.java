// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.authz.entities.actor;

import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static junit.framework.Assert.*;

public class ActorUTest {

    private class OpenUser extends User {
        public OpenUser(String firstName, String lastName) {
            super(firstName, lastName);
        }
        protected void _addRoles(final Collection<Role> roles) {
            super.addRoles(roles);
        }
        protected void _addPermissions(final List<WildcardPermission> permissions) {
            super.addPermissions(permissions);
        }
        protected void _removePermissions(final List<WildcardPermission> permissions) {
            super.removePermissions(permissions);
        }
    }

    private class OpenRole extends Role {
        public OpenRole(String name) {
            super(name);
        }
        public void _addAll(Collection<WildcardPermission> perms) {
            super.addAll(perms);
        }
        public void _add(WildcardPermission permission) {
            super.add(permission);
        }
    }

    private OpenUser actor;

    private OpenRole role1;
    private OpenRole role2;
    private OpenRole role3;
    private OpenRole role4;
    private OpenRole role5;

    private WildcardPermission perm1;
    private WildcardPermission perm2;
    private WildcardPermission perm3;
    private WildcardPermission perm4;
    private WildcardPermission perm5;

    private List<WildcardPermission> permissions1;
    private List<WildcardPermission> permissions2;

    private List<Role> roles1;
    private List<Role> roles2;

    // ------------------------------------------------------------------------

    @Before
    public void setUp() {
        actor = new OpenUser("Kasper", "User");
        role1 = new OpenRole("role1");
        role2 = new OpenRole("role2");
        role3 = new OpenRole("role3");
        role4 = new OpenRole("role4");
        role5 = new OpenRole("role5");
        perm1 = new WildcardPermission("perm1");
        perm2 = new WildcardPermission("perm2");
        perm3 = new WildcardPermission("perm3");
        perm4 = new WildcardPermission("perm4");
        perm5 = new WildcardPermission("perm5");

        permissions1 = new ArrayList<WildcardPermission>();
        permissions1.add(perm1);
        permissions1.add(perm2);
        permissions2 = new ArrayList<WildcardPermission>();
        permissions2.add(perm3);
        permissions2.add(perm4);
        permissions2.add(perm5);

        role1._addAll(permissions1);
        role2._add(perm2);
        role3._add(perm3);
        role4._add(perm4);
        role5._addAll(permissions2);

        roles1 = new ArrayList<Role>();
        roles2 = new ArrayList<Role>();

        roles1.add(role1);
        roles1.add(role2);
        roles2.add(role3);
        roles2.add(role4);
        roles2.add(role5);
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_addPermissions_withPermission_shouldHaveGoodSize(){
        // Given

        // When
        actor._addPermissions(permissions1);

        // Then
        assertEquals(actor.getPermissions().size(), 2);
    }

    @Test
    public void test_removesPermissions_withPermission_shouldHaveGoodSize(){
        // Given
        actor._addPermissions(permissions1);

        // When
        actor._removePermissions(permissions1);

        // Then
        assertEquals(actor.getPermissions().size(), 0);
    }

    @Test
    public void test_isPermitted_withPermissions_shouldBePermitted(){
        // Given
        actor._addPermissions(permissions1);

        // When
        final boolean isPermitted1 = actor.isPermitted(perm1);
        final boolean isPermitted2 = actor.isPermitted(perm3);

        // Then
        assertTrue(isPermitted1);
        assertFalse(isPermitted2);
    }

    @Test
    public void test_isPermitted_withRoles_shouldBePermitted(){
        // Given
        actor._addRoles(roles1);

        // When
        final boolean isPermitted1 = actor.isPermitted(perm1);
        final boolean isPermitted2 = actor.isPermitted(perm3);

        // Then
        assertTrue(isPermitted1);
        assertFalse(isPermitted2);
    }

    @Test
    public void test_hasRole_withRoles_shouldBeHasRole(){
        // Given
        actor._addRoles(roles1);

        // When
        final boolean hasRole1 = actor.hasRole(role1);
        final boolean hasRole2 = actor.hasRole(role3);

        // Then
        assertTrue(hasRole1);
        assertFalse(hasRole2);
    }

    public static class RoleTest {

        private class OpenRole extends Role {
            public OpenRole(String name) {
                super(name);
            }
            protected void _remove(Permission permission) {
                super.remove(permission);
            }
            public void _add(WildcardPermission permission) {
                super.add(permission);
            }
        }

        // ------------------------------------------------------------------------

        @Test
        public void test_isPermitted_withPermissions_shoudlAssert() {
            // Given
            final OpenRole role = new OpenRole("role");
            final WildcardPermission perm = new WildcardPermission("permission");
            role._add(perm);

            // When
            final boolean resultForGoodPerm = role.isPermitted(perm);
            final boolean resultForWrongPerm = role.isPermitted(new WildcardPermission("wrongPermission"));

            // Then
            assertTrue(resultForGoodPerm);
            assertFalse(resultForWrongPerm);
        }

        @Test
        public void test_removePermission_shouldRemove() {
            // Given
            final OpenRole role = new OpenRole("role");
            final WildcardPermission perm = new WildcardPermission("permission");
            role._add(perm);

            // When
            final WildcardPermission perm2 = new WildcardPermission("permission");
            role._remove(perm2);

            // Then
            assertEquals(role.getPermissions().size(), 0);
        }

    }
}
