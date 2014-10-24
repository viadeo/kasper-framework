// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.validation.validator;

import com.viadeo.kasper.api.*;
import com.viadeo.kasper.api.validation.AssertID;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintValidatorContext;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AssertIDValidatorUTest {

    private static final Format DB_ID = new FormatAdapter("db-id", Integer.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(String identifier) {
            return (E) new Integer(identifier);
        }
    };

    private static final Format UUID = new FormatAdapter("uuid", java.util.UUID.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(String identifier) {
            return (E) java.util.UUID.fromString(identifier);
        }
    };
    
    private AssertIDValidator validator;
    private AssertID assertID;
    private IDBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new SimpleIDBuilder(DB_ID, UUID);
        
        validator = new AssertIDValidator();
        
        assertID = mock(AssertID.class);
        when(assertID.vendor()).thenReturn("");
        when(assertID.type()).thenReturn(new String[]{});
        when(assertID.format()).thenReturn("");
    }

    @Test
    public void assertNoNull_withNullAsId_returnFalse() {
        // When
        boolean valid = validator.assertNotNull(null);
        
        // Then
        assertFalse(valid);
    }

    @Test
    public void assertNoNull_withId_returnTrue() {
        // Given
        ID id = builder.build("urn:viadeo:member:db-id:42");

        // When
        boolean valid = validator.assertNotNull(id);

        // Then
        assertTrue(valid);
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
    public void isValid_withNullAsID_returnFalse() {
        // Given
        when(assertID.type()).thenReturn(new String[]{"member"});

        validator.initialize(assertID);

        // When
        boolean valid = validator.isValid(null, mock(ConstraintValidatorContext.class));

        // Then
        assertFalse(valid);
    }
}
