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

public class CollectionOfIDValidatorUTest {

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
    public void isValid_withValidID_withInvalidID_returnTrue() {
        // Given
        when(assertID.vendor()).thenReturn("viadeo");
        when(assertID.type()).thenReturn(new String[]{"member"});
        when(assertID.format()).thenReturn("uuid");

        ID idA = builder.build("urn:viadeo:member:uuid:594fb387-3c18-4b99-b1e2-dc5704b8cea7");
        ID idB = builder.build("urn:viadeo:member:db-id:42");

        validator.initialize(assertID);

        // When
        boolean valid = validator.isValid(Lists.newArrayList(idA, idB), mock(ConstraintValidatorContext.class));

        // Then
        assertFalse(valid);
    }

    @Test
    public void isValid_withEmptyListOfID_returnTrue() {
        // Given
        when(assertID.type()).thenReturn(new String[]{"member"});

        validator.initialize(assertID);

        // When
        boolean valid = validator.isValid(Lists.<ID>newArrayList(), mock(ConstraintValidatorContext.class));

        // Then
        assertTrue(valid);
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
