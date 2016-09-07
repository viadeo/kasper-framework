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
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes.validation;

import com.google.common.collect.Sets;
import com.viadeo.kasper.doc.nodes.DocumentedConstraint;
import com.viadeo.kasper.doc.nodes.DocumentedProperty;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class PropertyValidationProcessorUTest {

    private PropertyValidationProcessor processor;

    // ------------------------------------------------------------------------

    @Before
    public void setUp(){
        processor = new PropertyValidationProcessor(new DefaultPropertyValidator());
    }

    // ------------------------------------------------------------------------

    @Test
    public void process_onField_annotatedByAnyJSR303Annotations_indicateProposal() throws NoSuchFieldException {
        // Given nothing
        final Field field = ObjectWithConstraints.class.getField("fieldAnnotatedByNotNull");
        final DocumentedProperty documentedProperty = new DocumentedProperty(
                field.getName(), null, field.getType().getSimpleName(), null, false, false, false, false, Sets.<DocumentedConstraint>newHashSet(), field.getType()
        );

        // When
        processor.process(field, documentedProperty);

        // Then
        assertEquals(1, documentedProperty.getConstraints().size());
        assertEquals("must not be null", documentedProperty.getConstraints().iterator().next().getMessage());
    }

    @Test
    public void process_onField_annotatedByTransient_indicateNoProposal() throws NoSuchFieldException {
        // Given nothing
        final Field field = ObjectWithConstraints.class.getField("fieldAnnotatedByTransient");
        final DocumentedProperty documentedProperty = new DocumentedProperty(
                field.getName(), null, field.getType().getSimpleName(), null, false, false, false, false, Sets.<DocumentedConstraint>newHashSet(), field.getType()
        );

        // When
        processor.process(field, documentedProperty);

        // Then
        assertEquals(0, documentedProperty.getConstraints().size());
    }

    @Test
    public void process_onField_unannotated_indicateNoProposal() throws NoSuchFieldException {
        // Given nothing
        final Field field = ObjectWithConstraints.class.getField("unannotatedField");
        final DocumentedProperty documentedProperty = new DocumentedProperty(
                field.getName(), null, field.getType().getSimpleName(), null, false, false, false, false, Sets.<DocumentedConstraint>newHashSet(), field.getType()
        );

        // When
        processor.process(field, documentedProperty);

        // Then
        assertEquals(0, documentedProperty.getConstraints().size());
    }

    // ------------------------------------------------------------------------

    public static class ObjectWithConstraints {
        @NotNull
        public String fieldAnnotatedByNotNull;

        public String unannotatedField;

        @Transient
        public String fieldAnnotatedByTransient;
    }

    public static class ObjectWithoutConstraints {
        public String field;
    }

}
