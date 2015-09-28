// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security.authz;

import com.google.common.collect.Sets;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.core.id.TestFormats;
import com.viadeo.kasper.core.security.authz.Actor;
import com.viadeo.kasper.core.security.authz.Permission;
import com.viadeo.kasper.core.security.authz.Role;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class ActorUTest {

    private Actor actor;
    private Role role1;
    private Role role2;
    private Role role3;
    private Role role4;
    private Role role5;
    private Permission perm1;
    private Permission perm2;
    private Permission perm3;
    private Permission perm4;
    private Permission perm5;
    private Set<Permission> permissions1;
    private Set<Permission> permissions2;
    private Set<Role> roles1;
    private Set<Role> roles2;

    // ------------------------------------------------------------------------
    @Before
    public void setUp() {
        actor = new Actor(new ID("viadeo", "member", TestFormats.ID, 1));
        role1 = new Role("role1");
        role2 = new Role("role2");
        role3 = new Role("role3");
        role4 = new Role("role4");
        role5 = new Role("role5");
        perm1 = new Permission("perm1");
        perm2 = new Permission("perm2");
        perm3 = new Permission("perm3");
        perm4 = new Permission("perm4");
        perm5 = new Permission("perm5");
        permissions1 = Sets.newHashSet();
        permissions1.add(perm1);
        permissions1.add(perm2);
        permissions2 = Sets.newHashSet();
        permissions2.add(perm3);
        permissions2.add(perm4);
        permissions2.add(perm5);
        role1.addAll(permissions1);
        role2.add(perm2);
        role3.add(perm3);
        role4.add(perm4);
        role5.addAll(permissions2);
        roles1 = Sets.newHashSet();
        roles2 = Sets.newHashSet();
        roles1.add(role1);
        roles1.add(role2);
        roles2.add(role3);
        roles2.add(role4);
        roles2.add(role5);
    }

    // ------------------------------------------------------------------------
    @Test
    public void test_addPermissions_withPermission_shouldHaveGoodSize() {
        // Given

        // When
        actor.addPermissions(permissions1);

        // Then
        assertEquals(actor.getPermissions().size(), 2);
    }

    @Test
    public void test_removesPermissions_withPermission_shouldHaveGoodSize() {
        // Given
        actor.addPermissions(permissions1);

        // When
        actor.removePermissions(permissions1);

        // Then
        assertEquals(actor.getPermissions().size(), 0);
    }

    @Test
    public void test_isPermitted_withPermissions_shouldBePermitted() {
        // Given
        actor.addPermissions(permissions1);

        // When
        final boolean isPermitted1 = actor.isPermitted(perm1);
        final boolean isPermitted2 = actor.isPermitted(perm3);

        // Then
        assertTrue(isPermitted1);
        assertFalse(isPermitted2);
    }

    @Test
    public void test_isPermitted_withRoles_shouldBePermitted() {
        // Given
        actor.addRoles(roles1);

        // When
        final boolean isPermitted1 = actor.isPermitted(perm1);
        final boolean isPermitted2 = actor.isPermitted(perm3);

        // Then
        assertTrue(isPermitted1);
        assertFalse(isPermitted2);
    }

    @Test
    public void test_hasRole_withRoles_shouldBeHasRole() {
        // Given
        actor.addRoles(roles1);

        // When
        final boolean hasRole1 = actor.hasRole(role1);
        final boolean hasRole2 = actor.hasRole(role3);

        // Then
        assertTrue(hasRole1);
        assertFalse(hasRole2);
    }

}