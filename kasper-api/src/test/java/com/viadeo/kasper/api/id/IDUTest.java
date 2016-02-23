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
