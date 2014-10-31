package com.viadeo.kasper.api;

import org.junit.Before;
import org.junit.Test;

import static com.viadeo.kasper.api.TestFormats.DB_ID;
import static com.viadeo.kasper.api.TestFormats.UUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

}
