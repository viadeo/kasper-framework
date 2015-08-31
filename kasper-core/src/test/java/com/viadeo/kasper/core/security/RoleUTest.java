// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RoleUTest {

    // ------------------------------------------------------------------------
    @Test
    public void test_isPermitted_withPermissions_shoudlAssert() {
        // Given
        final Role role = new Role("role");
        final Permission perm = new Permission("permission");
        role.add(perm);

        // When
        final boolean resultForGoodPerm = role.isPermitted(perm);
        final boolean resultForWrongPerm = role.isPermitted(new Permission("wrongPermission"));

        // Then
        assertTrue(resultForGoodPerm);
        assertFalse(resultForWrongPerm);
    }

    @Test
    public void test_removePermission_shouldRemove() {
        // Given
        final Role role = new Role("role");
        final Permission perm = new Permission("permission");
        role.add(perm);

        // When
        final Permission perm2 = new Permission("permission");
        role.remove(perm2);

        // Then
        assertEquals(role.getPermissions().size(), 0);
    }

    @Test
    public void test_EqualsRole_withSameRolesByName_shouldReturnTrue(){
        // Given
        final Role role1 = new Role("role");
        final Role role2 = new Role("role");

        // When
        boolean result = role1.equals(role2);

        // Then
        assertTrue(result);
    }

    @Test
    public void test_EqualsRole_withSameRolesByNameAndTargetIds_shouldReturnTrue(){
        // Given
        final Role role1 = new Role("role", Optional.of(3));
        final Role role2 = new Role("role", Optional.of(3));

        // When
        boolean result = role1.equals(role2);

        // Then
        assertTrue(result);
    }

    @Test
    public void test_EqualsRole_withNotSameRolesByNames_shouldReturnFalse(){
        // Given
        final Role role1 = new Role("role1");
        final Role role2 = new Role("role2");

        // When
        boolean result = role1.equals(role2);

        // Then
        assertFalse(result);
    }

    @Test
    public void test_EqualsRole_withNotSameRolesByTargetIds_shouldReturnFalse(){
        // Given
        final Role role1 = new Role("role", Optional.of(1));
        final Role role2 = new Role("role", Optional.of(3));

        // When
        boolean result = role1.equals(role2);

        // Then
        assertFalse(result);
    }
}
