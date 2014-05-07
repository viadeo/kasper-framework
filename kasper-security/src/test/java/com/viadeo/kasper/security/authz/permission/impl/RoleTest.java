// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.permission.impl;

import com.viadeo.kasper.security.authz.permission.Permission;
import org.junit.Test;

import static junit.framework.Assert.*;

public class RoleTest {

    @Test
    public void test_isPermitted_withPermissions_shoudlAssert() {
        // Given
        final Role role = new Role("role");
        final Permission perm = new WildcardPermission("permission");
        role.add(perm);

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
        final Role role = new Role("role");
        final Permission perm = new WildcardPermission("permission");
        role.add(perm);

        // When
        final Permission perm2 = new WildcardPermission("permission");
        role.remove(perm2);

        // Then
        assertEquals(role.getPermissions().size(), 0);
    }

}
