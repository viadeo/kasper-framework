// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.actor;

import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;
import com.viadeo.kasper.security.authz.permission.impl.WildcardPermission;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.*;

public class SubjectTest {

    private Subject subject;

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

    private List<Permission> permissions1;
    private List<Permission> permissions2;

    private List<Role> roles1;
    private List<Role> roles2;

    // ------------------------------------------------------------------------

    @Before
    public void setUp() {
        subject = new Subject();
        role1 = new Role("role1");
        role2 = new Role("role2");
        role3 = new Role("role3");
        role4 = new Role("role4");
        role5 = new Role("role5");
        perm1 = new WildcardPermission("perm1");
        perm2 = new WildcardPermission("perm2");
        perm3 = new WildcardPermission("perm3");
        perm4 = new WildcardPermission("perm4");
        perm5 = new WildcardPermission("perm5");

        permissions1 = new ArrayList<Permission>();
        permissions1.add(perm1);
        permissions1.add(perm2);
        permissions2 = new ArrayList<Permission>();
        permissions2.add(perm3);
        permissions2.add(perm4);
        permissions2.add(perm5);

        role1.addAll(permissions1);
        role2.add(perm2);
        role3.add(perm3);
        role4.add(perm4);
        role5.addAll(permissions2);

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
        subject.addPermissions(permissions1);

        // Then
        assertEquals(subject.getPermissions().size(), 2);
    }

    @Test
    public void test_removesPermissions_withPermission_shouldHaveGoodSize(){
        // Given
        subject.addPermissions(permissions1);

        // When
        subject.removePermissions(permissions1);

        // Then
        assertEquals(subject.getPermissions().size(), 0);
    }

    @Test
    public void test_isPermitted_withPermissions_shouldBePermitted(){
        // Given
        subject.addPermissions(permissions1);

        // When
        final boolean isPermitted1 = subject.isPermitted(perm1);
        final boolean isPermitted2 = subject.isPermitted(perm3);

        // Then
        assertTrue(isPermitted1);
        assertFalse(isPermitted2);
    }

    @Test
    public void test_isPermitted_withRoles_shouldBePermitted(){
        // Given
        subject.addRoles(roles1);

        // When
        final boolean isPermitted1 = subject.isPermitted(perm1);
        final boolean isPermitted2 = subject.isPermitted(perm3);

        // Then
        assertTrue(isPermitted1);
        assertFalse(isPermitted2);
    }

    @Test
    public void test_hasRole_withRoles_shouldBeHasRole(){
        // Given
        subject.addRoles(roles1);

        // When
        final boolean hasRole1 = subject.hasRole(role1);
        final boolean hasRole2 = subject.hasRole(role3);

        // Then
        assertTrue(hasRole1);
        assertFalse(hasRole2);
    }

}
