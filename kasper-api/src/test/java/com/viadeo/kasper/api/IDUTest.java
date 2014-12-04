package com.viadeo.kasper.api;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Map;

import static com.viadeo.kasper.api.TestFormats.DB_ID;
import static com.viadeo.kasper.api.TestFormats.UUID;
import static org.junit.Assert.*;

public class IDUTest {

    private ID givenId;

    @Before
    public void setUp() throws Exception {
        givenId = new ID("viadeo", "member", DB_ID, 42);
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
        ID id = givenId.checkFormat(DB_ID);

        assertNotNull(id);
        assertTrue(id == givenId);
    }

    @Test(expected = IllegalStateException.class)
    public void checkFormat_withDifferentFormat_isKo() {
        givenId.checkFormat(UUID);
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
        givenId.to(UUID);
    }

    @Test
    public void to_withIDTransformer_isKo() {
        // Given
        final ID expectedID = new ID("viadeo", "member", UUID, java.util.UUID.randomUUID());

        givenId.setIDTransformer(new IDTransformer() {
            @Override
            public Map<ID,ID> to(Format format, Collection<ID> ids) {
               throw new UnsupportedOperationException();
            }

            @Override
            public Map<ID,ID> to(Format format, ID firstId, ID... restIds) {
                throw new UnsupportedOperationException();
            }

            @Override
            public ID to(Format format, ID id) {

                return expectedID;
            }
        });

        // When
        ID transformedID = givenId.to(UUID);

        // Then
        assertEquals(expectedID, transformedID);
    }

}
