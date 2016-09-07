// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.interceptor.authorization;

import com.google.common.collect.Sets;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.core.id.TestFormats;
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