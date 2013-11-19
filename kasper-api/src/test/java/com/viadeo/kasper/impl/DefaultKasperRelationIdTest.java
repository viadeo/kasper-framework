// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.impl;

import com.viadeo.kasper.KasperID;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultKasperRelationIdTest {

    @Test
    public void testRandomId_shouldDeserializeItsOwnSerializedId() {
        // Given
        final DefaultKasperRelationId id = DefaultKasperRelationId.random();
        final KasperID id1 = id.getSourceId();
        final KasperID id2 = id.getTargetId();

        // When
        id.setId(id.toString());

        // Then
        assertEquals(id1, id.getSourceId());
        assertEquals(id2, id.getTargetId());
    }

    @Test
    public void testFixedId_shouldDeserializeItsOwnSerializedId() {
        // Given
        final DefaultKasperRelationId id = new DefaultKasperRelationId();
        final KasperID id1 = DefaultKasperId.random();
        final KasperID id2 = DefaultKasperId.random();

        // When
        id.setId(id1, id2);

        // Then
        assertEquals(id1, id.getSourceId());
        assertEquals(id2, id.getTargetId());

        // When
        id.setId(id.toString());

        // Then
        assertEquals(id1, id.getSourceId());
        assertEquals(id2, id.getTargetId());
    }

    @Test
    public void testFixedStringId_shouldDeserializeItsOwnSerializedId() {
        // Given
        final DefaultKasperRelationId id = new DefaultKasperRelationId();
        final KasperID id1 = new StringKasperId("foo");
        final KasperID id2 = new StringKasperId("bar");

        // When
        id.setId(id1, id2);

        // Then
        assertEquals(id1, id.getSourceId());
        assertEquals(id2, id.getTargetId());

        // When
        id.setId(id.toString());

        // Then
        assertEquals(id1, id.getSourceId());
        assertEquals(id2, id.getTargetId());

        assertEquals(id1.getClass(), id.getSourceId().getClass());
        assertEquals(id2.getClass(), id.getTargetId().getClass());
    }

    @Test
    public void testFixedIntegerId_shouldDeserializeItsOwnSerializedId() {
        // Given
        final DefaultKasperRelationId id = new DefaultKasperRelationId();
        final KasperID id1 = new IntegerKasperId(42);
        final KasperID id2 = new IntegerKasperId(12);

        // When
        id.setId(id1, id2);

        // Then
        assertEquals(id1, id.getSourceId());
        assertEquals(id2, id.getTargetId());

        // When
        id.setId(id.toString());

        // Then
        assertEquals(id1, id.getSourceId());
        assertEquals(id2, id.getTargetId());

        assertEquals(id1.getClass(), id.getSourceId().getClass());
        assertEquals(id2.getClass(), id.getTargetId().getClass());
    }

    @Test
    public void testFixedMixedId_shouldDeserializeItsOwnSerializedId() {
        // Given
        final DefaultKasperRelationId id = new DefaultKasperRelationId();
        final KasperID id1 = new StringKasperId("foo");
        final KasperID id2 = new IntegerKasperId(12);

        // When
        id.setId(id1, id2);

        // Then
        assertEquals(id1, id.getSourceId());
        assertEquals(id2, id.getTargetId());

        // When
        id.setId(id.toString());

        // Then
        assertEquals(id1, id.getSourceId());
        assertEquals(id2, id.getTargetId());

        assertEquals(id1.getClass(), id.getSourceId().getClass());
        assertEquals(id2.getClass(), id.getTargetId().getClass());
    }

}
