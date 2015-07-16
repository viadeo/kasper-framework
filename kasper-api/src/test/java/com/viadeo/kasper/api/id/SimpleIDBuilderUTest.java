package com.viadeo.kasper.api.id;

import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.SimpleIDBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class SimpleIDBuilderUTest {

    private SimpleIDBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new SimpleIDBuilder(
                TestFormats.DB_ID,
                TestFormats.UUID
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_withInvalidURN_isKo() {
        // Given
        String urn = "urn:miaou:42";

        // When
        builder.build(urn);
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_withValidURN_withUnknownFormat_isKo() {
        // Given
        String urn = "urn:viadeo:member:web-id:42";

        // When
        builder.build(urn);
    }

    @Test
    public void build_withValidURN_withKnownFormat_isOk() {
        // Given
        String urn = "urn:viadeo:member:db-id:42";

        // When
        ID id = builder.build(urn);

        // Then
        assertNotNull(id);
        assertFalse(id.getTransformer().isPresent());
    }
}
