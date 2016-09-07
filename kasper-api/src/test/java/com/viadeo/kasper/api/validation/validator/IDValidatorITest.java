// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
