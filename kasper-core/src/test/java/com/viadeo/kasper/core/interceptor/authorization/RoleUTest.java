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

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.*;

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
