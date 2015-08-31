package com.viadeo.kasper.api.id;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class TransformableIDBuilderUTest {

    private IDTransformer transformer;
    private TransformableIDBuilder builder;

    @Before
    public void setUp() throws Exception {
        transformer = mock(IDTransformer.class);
        builder = new TransformableIDBuilder(
                transformer,
                TestFormats.DB_ID,
                TestFormats.UUID
        );
    }

    @Test
    public void build_withValidURN_withKnownFormat_isOk() {
        // Given
        String urn = "urn:viadeo:member:db-id:42";

        // When
        ID id = builder.build(urn);

        // Then
        assertNotNull(id);
        assertTrue(id.getTransformer().isPresent());
        assertEquals(transformer, id.getTransformer().get());
    }
}
