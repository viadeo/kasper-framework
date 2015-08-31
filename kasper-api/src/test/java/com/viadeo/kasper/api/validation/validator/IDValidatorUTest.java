// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.validation.validator;

import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.api.id.SimpleIDBuilder;
import com.viadeo.kasper.api.validation.AssertID;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintValidatorContext;

import static com.viadeo.kasper.api.id.TestFormats.DB_ID;
import static com.viadeo.kasper.api.id.TestFormats.UUID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IDValidatorUTest {

    private IDValidator validator;
    private AssertID assertID;
    private IDBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new SimpleIDBuilder(DB_ID, UUID);
        
        validator = new IDValidator();
        
        assertID = mock(AssertID.class);
        when(assertID.vendor()).thenReturn("");
        when(assertID.type()).thenReturn(new String[]{});
        when(assertID.format()).thenReturn("");
    }

    @Test
    public void assertVendor_withExpectedVendor_returnTrue() {
        // Given
        when(assertID.vendor()).thenReturn("viadeo");

        ID id = builder.build("urn:viadeo:member:db-id:42");

        validator.initialize(assertID);

        // When
        boolean valid = validator.assertVendor(id);

        // Then
        assertTrue(valid);
    }

    @Test
    public void assertVendor_withEmptyVendor_returnTrue() {
        // Given
        when(assertID.vendor()).thenReturn("");

        ID id = builder.build("urn:viadeo:member:db-id:42");

        validator.initialize(assertID);

        // When
        boolean valid = validator.assertVendor(id);

        // Then
        assertTrue(valid);
    }

    @Test
    public void assertVendor_withUnexpectedVendor_returnFalse() {
        // Given
        when(assertID.vendor()).thenReturn("viadeo");

        ID id = builder.build("urn:miaou:member:db-id:42");

        validator.initialize(assertID);

        // When
        boolean valid = validator.assertVendor(id);

        // Then
        assertFalse(valid);
    }

    @Test
    public void assertFormat_withExpectedFormat_returnTrue() {
        // Given
        when(assertID.format()).thenReturn(DB_ID.name());
        ID id = builder.build("urn:viadeo:member:db-id:42");

        validator.initialize(assertID);

        // When
        boolean valid = validator.assertFormat(id);

        // Then
        assertTrue(valid);
    }

    @Test
    public void assertFormat_withEmptyFormat_returnTrue() {
        // Given
        when(assertID.format()).thenReturn("");

        ID id = builder.build("urn:viadeo:member:db-id:42");

        validator.initialize(assertID);

        // When
        boolean valid = validator.assertVendor(id);

        // Then
        assertTrue(valid);
    }

    @Test
    public void assertFormat_withUnexpectedFormat_returnFalse() {
        // Given
        when(assertID.format()).thenReturn(DB_ID.name());

        ID id = builder.build("urn:viadeo:member:uuid:594fb387-3c18-4b99-b1e2-dc5704b8cea7");

        validator.initialize(assertID);

        // When
        boolean valid = validator.assertFormat(id);

        // Then
        assertFalse(valid);
    }

    @Test
    public void assertObjectType_withExpectedType_returnTrue() {
        // Given
        when(assertID.type()).thenReturn(new String[]{"member"});

        ID id = builder.build("urn:viadeo:member:db-id:42");

        validator.initialize(assertID);

        // When
        boolean valid = validator.assertObjectType(id);

        // Then
        assertTrue(valid);
    }

    @Test
    public void assertObjectType_withEmptyType_returnTrue() {
        // Given
        when(assertID.type()).thenReturn(new String[]{});

        ID id = builder.build("urn:viadeo:member:db-id:42");

        validator.initialize(assertID);

        // When
        boolean valid = validator.assertObjectType(id);

        // Then
        assertTrue(valid);
    }

    @Test
    public void assertObjectType_withUnexpectedType_returnFalse() {
        // Given
        when(assertID.type()).thenReturn(new String[]{"company"});

        ID id = builder.build("urn:viadeo:member:uuid:594fb387-3c18-4b99-b1e2-dc5704b8cea7");

        validator.initialize(assertID);

        // When
        boolean valid = validator.assertObjectType(id);

        // Then
        assertFalse(valid);
    }

    @Test
    public void isValid_withExpectedID_returnTrue() {
        // Given
        when(assertID.type()).thenReturn(new String[]{"member"});

        ID id = builder.build("urn:viadeo:member:uuid:594fb387-3c18-4b99-b1e2-dc5704b8cea7");

        validator.initialize(assertID);

        // When
        boolean valid = validator.isValid(id, mock(ConstraintValidatorContext.class));

        // Then
        assertTrue(valid);
    }

    @Test
    public void isValid_withExpectedVendor_withExpectedType_withExpectedFormat_returnTrue() {
        // Given
        when(assertID.vendor()).thenReturn("viadeo");
        when(assertID.type()).thenReturn(new String[]{"member"});
        when(assertID.format()).thenReturn("uuid");

        ID id = builder.build("urn:viadeo:member:uuid:594fb387-3c18-4b99-b1e2-dc5704b8cea7");

        validator.initialize(assertID);

        // When
        boolean valid = validator.isValid(id, mock(ConstraintValidatorContext.class));

        // Then
        assertTrue(valid);
    }

    @Test
    public void isValid_withNullAsID_returnTrue() {
        // Given
        when(assertID.type()).thenReturn(new String[]{"member"});

        validator.initialize(assertID);

        // When
        boolean valid = validator.isValid(null, mock(ConstraintValidatorContext.class));

        // Then
        assertTrue(valid);
    }
}
