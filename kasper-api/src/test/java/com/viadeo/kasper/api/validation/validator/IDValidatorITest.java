package com.viadeo.kasper.api.validation.validator;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.TestFormats;
import com.viadeo.kasper.api.validation.AssertID;
import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class IDValidatorITest {

    private Validator validator;

    @Before
    public void setUp() throws Exception {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void validate_witID_isOk() throws Exception {
        // Given
        BeanWithID bean = new BeanWithID();
        bean.id = new ID("vendor", "member", TestFormats.DB_ID, 42);

        // When
        Set<ConstraintViolation<BeanWithID>> violations = validator.validate(bean);

        // Then
        assertNotNull(violations);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validate_withUnexpectedID_isKo() throws Exception {
        // Given
        BeanWithID bean = new BeanWithID();
        bean.id = new ID("vendor", "company", TestFormats.DB_ID, 42);

        // When
        Set<ConstraintViolation<BeanWithID>> violations = validator.validate(bean);

        // Then
        assertNotNull(violations);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void validate_withListOfID_isOk() throws Exception {
        // Given
        BeanWithListOfID bean = new BeanWithListOfID();
        bean.ids = Lists.newArrayList(new ID("vendor", "member", TestFormats.DB_ID, 42));

        // When
        Set<ConstraintViolation<BeanWithListOfID>> violations = validator.validate(bean);

        // Then
        assertNotNull(violations);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validate_withListOfUnexpectedID_isKo() throws Exception {
        // Given
        BeanWithListOfID bean = new BeanWithListOfID();
        bean.ids = Lists.newArrayList(new ID("vendor", "company", TestFormats.DB_ID, 42));

        // When
        Set<ConstraintViolation<BeanWithListOfID>> violations = validator.validate(bean);

        // Then
        assertNotNull(violations);
        assertFalse(violations.isEmpty());
    }

    @Test
    public void validate_withSetOfID_isOk() throws Exception {
        // Given
        BeanWithSetOfID bean = new BeanWithSetOfID();
        bean.ids = Sets.newHashSet(new ID("vendor", "member", TestFormats.DB_ID, 42));

        // When
        Set<ConstraintViolation<BeanWithSetOfID>> violations = validator.validate(bean);

        // Then
        assertNotNull(violations);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void validate_withSetOfUnexpectedID_isKo() throws Exception {
        // Given
        BeanWithSetOfID bean = new BeanWithSetOfID();
        bean.ids = Sets.newHashSet(new ID("vendor", "company", TestFormats.DB_ID, 42));

        // When
        Set<ConstraintViolation<BeanWithSetOfID>> violations = validator.validate(bean);

        // Then
        assertNotNull(violations);
        assertFalse(violations.isEmpty());
    }

    static class BeanWithID {
        @AssertID(vendor = "vendor", type = "member", format = "db-id")
        ID id;
    }

    static class BeanWithListOfID {
        @AssertID(vendor = "vendor", type = "member", format = "db-id")
        List<ID> ids;
    }

    static class BeanWithSetOfID {
        @AssertID(vendor = "vendor", type = "member", format = "db-id")
        Set<ID> ids;
    }
}
