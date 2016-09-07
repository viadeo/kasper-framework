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
package com.viadeo.kasper.api.id;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class IDUTest {

    private ID givenId;

    @Before
    public void setUp() throws Exception {
        givenId = new ID("viadeo", "member", TestFormats.DB_ID, 42);
    }

    @Test
    public void test_id_integrity() {
        // When
        ID id = new ID("viadeo", "member", TestFormats.DB_ID, 42);

        // Then
        assertEquals("viadeo", id.getVendor());
        assertEquals("member", id.getType());
        assertEquals(TestFormats.DB_ID, id.getFormat());
        assertEquals("42", id.getIdentifier());
        assertEquals(42, (int) id.<Integer>parseIdentifier());
    }

    @Test
    public void test_toString_method() {
        // When
        ID id = new ID("viadeo", "member", TestFormats.DB_ID, 42);

        // Then
        assertEquals("urn:viadeo:member:db-id:42", id.toString());
    }

    @Test
    public void checkVendor_withVendor_isOk() {
        ID id = givenId.checkVendor("viadeo");

        assertNotNull(id);
        assertTrue(id == givenId);
    }

    @Test(expected = IllegalStateException.class)
    public void checkVendor_withDifferentVendor_isKo() {
        givenId.checkVendor("orange");
    }

    @Test
    public void checkFormat_withFormat_isOk() {
        ID id = givenId.checkFormat(TestFormats.DB_ID);

        assertNotNull(id);
        assertTrue(id == givenId);
    }

    @Test(expected = IllegalStateException.class)
    public void checkFormat_withDifferentFormat_isKo() {
        givenId.checkFormat(TestFormats.UUID);
    }

    @Test
    public void checkType_withType_isOk() {
        ID id = givenId.checkType("member");

        assertNotNull(id);
        assertTrue(id == givenId);
    }

    @Test(expected = IllegalStateException.class)
    public void checkType_withDifferentType_isKo() {
        givenId.checkType("company");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void to_withoutIDTransformer_isKo() {
        givenId.to(TestFormats.UUID);
    }

    @Test
    public void to_withIDTransformer_isKo() {
        // Given
        final ID expectedID = new ID("viadeo", "member", TestFormats.UUID, java.util.UUID.randomUUID());
        final IDTransformer transformer = new IDTransformer() {
            @Override
            public Map<ID, ID> to(Format format, Collection<ID> ids) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Map<ID, ID> to(Format format, ID firstId, ID... restIds) {
                throw new UnsupportedOperationException();
            }

            @Override
            @Deprecated
            public List<ID> toList(Format format, Collection<ID> ids) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ID to(Format format, ID id) {
                return expectedID;
            }
        };

        givenId.setIDTransformer(transformer);

        // When
        ID transformedID = givenId.to(TestFormats.UUID);

        // Then
        assertEquals(expectedID, transformedID);
        assertTrue(transformedID.getTransformer().isPresent());
        assertEquals(transformer, transformedID.getTransformer().get());
    }

}
