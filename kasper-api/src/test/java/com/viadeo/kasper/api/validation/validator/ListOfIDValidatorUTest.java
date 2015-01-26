package com.viadeo.kasper.api.validation.validator;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.ID;
import com.viadeo.kasper.api.IDBuilder;
import com.viadeo.kasper.api.SimpleIDBuilder;
import com.viadeo.kasper.api.validation.AssertID;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintValidatorContext;

import static com.viadeo.kasper.api.TestFormats.DB_ID;
import static com.viadeo.kasper.api.TestFormats.UUID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ListOfIDValidatorUTest {

    private CollectionOfIDValidator validator;
    private IDBuilder builder;
    private AssertID assertID;

    @Before
    public void setUp() throws Exception {
        builder = new SimpleIDBuilder(DB_ID, UUID);

        validator = new CollectionOfIDValidator();

        assertID = mock(AssertID.class);
        when(assertID.vendor()).thenReturn("");
        when(assertID.type()).thenReturn(new String[]{});
        when(assertID.format()).thenReturn("");
    }

    @Test
    public void isValid_withUnexpectedID_returnFalse() {
        // Given
        when(assertID.type()).thenReturn(new String[]{"member"});

        ID id = builder.build("urn:viadeo:company:uuid:594fb387-3c18-4b99-b1e2-dc5704b8cea7");

        validator.initialize(assertID);

        // When
        boolean valid = validator.isValid(Lists.newArrayList(id), mock(ConstraintValidatorContext.class));

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
        boolean valid = validator.isValid(Lists.newArrayList(id), mock(ConstraintValidatorContext.class));

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
        boolean valid = validator.isValid(Lists.newArrayList(id), mock(ConstraintValidatorContext.class));

        // Then
        assertTrue(valid);
    }

    @Test
    public void isValid_withEmptyListOfID_returnFalse() {
        // Given
        when(assertID.type()).thenReturn(new String[]{"member"});

        validator.initialize(assertID);

        // When
        boolean valid = validator.isValid(Lists.<ID>newArrayList(), mock(ConstraintValidatorContext.class));

        // Then
        assertFalse(valid);
    }

    @Test
    public void isValid_withNullAsListOfID_returnTrue() {
        // Given
        when(assertID.type()).thenReturn(new String[]{"member"});

        validator.initialize(assertID);

        // When
        boolean valid = validator.isValid(null, mock(ConstraintValidatorContext.class));

        // Then
        assertTrue(valid);
    }
}
