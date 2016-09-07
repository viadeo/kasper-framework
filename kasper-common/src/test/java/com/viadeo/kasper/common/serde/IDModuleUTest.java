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
package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.api.id.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class IDModuleUTest {
    
    private static final String VENDOR = "vendor";
    private static final Format DB_ID = new FormatAdapter("db-id", Integer.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(final String identifier) {
            return (E) new Integer(identifier);
        }
    };

    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        IDBuilder builder = new SimpleIDBuilder(DB_ID);
        IDModule module = new IDModule(builder);

        mapper = new ObjectMapper();
        mapper.registerModule(module);
    }

    @Test
    public void serialize_isOk() throws JsonProcessingException {
        // Given
        ID id = new ID(VENDOR, "member", DB_ID, 42);

        // When
        String serializedId = mapper.writeValueAsString(id);

        // Then
        assertNotNull(serializedId);
        assertEquals("\"" + id.toString() + "\"", serializedId);
    }

    @Test
    public void deserialize_isOk() throws IOException {
        // Given
        String serializedId = "\"urn:viadeo:member:db-id:42\"";

        // When
        ID deserializedId = mapper.readValue(serializedId, ID.class);

        // Then
        assertNotNull(deserializedId);
        assertEquals("member", deserializedId.getType());
        assertEquals(DB_ID, deserializedId.getFormat());
        assertEquals("42", deserializedId.getIdentifier());
    }

    @Test
    public void serialize_then_deserialize_isOk() throws IOException {
        // Given
        ID givenId = new ID(VENDOR, "member", DB_ID, 42);

        // When
        ID actualId = mapper.readValue(mapper.writeValueAsString(givenId), ID.class);

        // Then
        assertNotNull(actualId);
        assertEquals(givenId.getVendor(), actualId.getVendor());
        assertEquals(givenId.getType(), actualId.getType());
        assertEquals(givenId.getFormat(), actualId.getFormat());
        assertEquals(givenId.getIdentifier(), actualId.getIdentifier());
    }

    @Test
    public void serialize_then_deserialize_a_relation_ID_isOk() throws IOException {
        // Given
        RelationID relationId = new RelationID(
                new ID(VENDOR, "member", DB_ID, 42),
                new ID(VENDOR, "member", DB_ID, 24)
        );

        // When
        String content = mapper.writeValueAsString(relationId);
        RelationID actualRelationId = mapper.readValue(content, RelationID.class);

        // Then
        assertNotNull(actualRelationId);
        assertEquals(relationId.getSourceId(), actualRelationId.getSourceId());
        assertEquals(relationId.getTargetId(), actualRelationId.getTargetId());
        assertEquals(relationId.getId(), actualRelationId.getId());
    }
}
