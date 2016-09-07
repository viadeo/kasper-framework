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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.api.id.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KasperIdModuleUTest {

    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new KasperIdModule());
    }

    @Test
    public void serialize_fromDefaultKasperRelationId_isOk() throws IOException {
        // Given
        String sourceId = "83fa9d62-3f38-486c-84c2-4868a1bc8609";
        String targetId = "b54dc4b0-19a8-413b-847c-aab1b6d1c291";
        DefaultKasperRelationId kasperId = new DefaultKasperRelationId(
                new DefaultKasperId(sourceId),
                new DefaultKasperId(targetId)
        );

        // When
        String json = objectMapper.writeValueAsString(kasperId);

        // Given
        assertNotNull(json);
        assertEquals("\"" + sourceId + DefaultKasperRelationId.SEPARATOR + targetId + "\"", json);
    }

    @Test
    public void deserialize_toDefaultKasperRelationId_isOk() throws IOException {
        // Given
        String json = "\"83fa9d62-3f38-486c-84c2-4868a1bc8609--b54dc4b0-19a8-413b-847c-aab1b6d1c291\"";
        DefaultKasperRelationId expectedKasperId = new DefaultKasperRelationId(
                new DefaultKasperId("83fa9d62-3f38-486c-84c2-4868a1bc8609"),
                new DefaultKasperId("b54dc4b0-19a8-413b-847c-aab1b6d1c291")
        );

        // When
        DefaultKasperRelationId actualKasperId = objectMapper.readValue(json, DefaultKasperRelationId.class);

        // Given
        assertNotNull(actualKasperId);
        assertEquals(expectedKasperId, actualKasperId);
    }

    @Test
    public void serialize_fromDefaultKasperId_isOk() throws IOException {
        // Given
        String id = "83fa9d62-3f38-486c-84c2-4868a1bc8609";
        DefaultKasperId kasperId = new DefaultKasperId(id);

        // When
        String json = objectMapper.writeValueAsString(kasperId);

        // Given
        assertNotNull(json);
        assertEquals("\"" + id + "\"", json);
    }

    @Test
    public void deserialize_toDefaultKasperId_isOk() throws IOException {
        // Given
        String json = "\"83fa9d62-3f38-486c-84c2-4868a1bc8609\"";
        DefaultKasperId expectedKasperId = new DefaultKasperId("83fa9d62-3f38-486c-84c2-4868a1bc8609");

        // When
        DefaultKasperId actualKasperId = objectMapper.readValue(json, DefaultKasperId.class);

        // Given
        assertNotNull(actualKasperId);
        assertEquals(expectedKasperId, actualKasperId);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void serialize_fromIntegerKasperId_isOk() throws IOException {
        // Given
        Integer id = 42;
        IntegerKasperId kasperId = new IntegerKasperId(id);

        // When
        String json = objectMapper.writeValueAsString(kasperId);

        // Given
        assertNotNull(json);
        assertEquals("{\"id\":" + id + "}", json);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void deserialize_toIntegerKasperId_isOk() throws IOException {
        // Given
        String json = "{\"id\":42}";
        IntegerKasperId expectedKasperId = new IntegerKasperId(42);

        // When
        IntegerKasperId actualKasperId = objectMapper.readValue(json, IntegerKasperId.class);

        // Given
        assertNotNull(actualKasperId);
        assertEquals(expectedKasperId, actualKasperId);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void serialize_fromLongKasperId_isOk() throws IOException {
        // Given
        Long id = 42L;
        LongKasperId kasperId = new LongKasperId(id);

        // When
        String json = objectMapper.writeValueAsString(kasperId);

        // Given
        assertNotNull(json);
        assertEquals("{\"id\":" + id + "}", json);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void deserialize_toLongKasperId_isOk() throws IOException {
        // Given
        String json = "{\"id\":42}";
        LongKasperId expectedKasperId = new LongKasperId(42L);

        // When
        LongKasperId actualKasperId = objectMapper.readValue(json, LongKasperId.class);

        // Given
        assertNotNull(actualKasperId);
        assertEquals(expectedKasperId, actualKasperId);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void serialize_fromStringKasperId_isOk() throws IOException {
        // Given
        String id = "pioupiou";
        StringKasperId kasperId = new StringKasperId(id);

        // When
        String json = objectMapper.writeValueAsString(kasperId);

        // Given
        assertNotNull(json);
        assertEquals("{\"id\":\"" + id + "\"}", json);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void deserialize_toStringKasperId_isOk() throws IOException {
        // Given
        String json = "\"pioupiou\"";
        StringKasperId expectedKasperId = new StringKasperId("pioupiou");

        // When
        StringKasperId actualKasperId = objectMapper.readValue(json, StringKasperId.class);

        // Given
        assertNotNull(actualKasperId);
        assertEquals(expectedKasperId, actualKasperId);
    }
}
