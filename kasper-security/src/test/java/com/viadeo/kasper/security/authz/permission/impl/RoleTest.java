// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.permission.impl;

import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.permission.impl.WildcardPermission;
import org.junit.Test;

import static junit.framework.Assert.*;

public class RoleTest {

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
