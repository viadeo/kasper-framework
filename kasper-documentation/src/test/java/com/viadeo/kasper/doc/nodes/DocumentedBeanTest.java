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
package com.viadeo.kasper.doc.nodes;

import com.viadeo.kasper.api.annotation.XKasperField;
import com.viadeo.kasper.core.component.command.aggregate.LinkedConcept;
import com.viadeo.kasper.domain.sample.root.api.query.GetAllMembersQueryHandler;
import com.viadeo.kasper.domain.sample.root.command.model.entity.Member;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

public class DocumentedBeanTest {

    public static class ClassWithConstant {
        @SuppressWarnings("unused")
        private static final Logger LOGGER = LoggerFactory.getLogger(DocumentedBeanTest.ClassWithConstant.class);
    }

    @Test
    public void init_withConstant_shouldBeIgnored() {
        // When
        final DocumentedBean bean = new DocumentedBean(ClassWithConstant.class);

        // Then
        assertNotNull(bean);
        assertEquals(0, bean.size());
    }

    // ------------------------------------------------------------------------

    public static class ClassWithTransientFieldUsingAnnotation {
        @SuppressWarnings("unused")
        @Transient
        public String field;
    }

    @Test
    public void init_withTransientField_usingAnnotation_shouldBeIgnored() {
        // When
        final DocumentedBean bean = new DocumentedBean(ClassWithTransientFieldUsingAnnotation.class);

        // Then
        assertNotNull(bean);
        assertEquals(0, bean.size());
    }

    // ------------------------------------------------------------------------

    public static class ClassWithTransientFieldUsingModifier {
        @SuppressWarnings("unused")
        public transient String field;
    }

    @Test
    public void init_withTransientField_usingModifier_shouldBeIgnored() {
        // When
        final DocumentedBean bean = new DocumentedBean(ClassWithTransientFieldUsingModifier.class);

        // Then
        assertNotNull(bean);
        assertEquals(0, bean.size());
    }

    // ------------------------------------------------------------------------

    public static class ClassWithSimpleField {
        @SuppressWarnings("unused")
        public String field;
    }

    @Test
    public void init_withSimpleField_shouldBeDetailed() {
        // When
        final DocumentedBean bean = new DocumentedBean(ClassWithSimpleField.class);

        // Then
        assertNotNull(bean);
        assertEquals(1, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("field", prop.getName());
        assertEquals("String", prop.getType());
        assertFalse(prop.isList());
        assertFalse(prop.getLinkedConcept());
        assertFalse(prop.isQueryResult());
    }

    // ------------------------------------------------------------------------

    public class ClassWithFieldCollection {
        @SuppressWarnings("unused")
        public Collection<Integer> fieldCollection;
    }

    @Test
    public void init_withCollection_shouldBeDetailed() {
        // When
        final DocumentedBean bean = new DocumentedBean(ClassWithFieldCollection.class);

        // Then
        assertNotNull(bean);
        assertEquals(1, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("fieldCollection", prop.getName());
        assertEquals("Integer", prop.getType());
        assertTrue(prop.isList());
        assertFalse(prop.getLinkedConcept());
        assertFalse(prop.isQueryResult());
    }

    // ------------------------------------------------------------------------

    public class ClassWithFieldCollectionGeneric<E> {
        @SuppressWarnings("unused")
        public Collection<E> fieldCollection;
    }

    public class ClassExtendingCollectionGeneric extends ClassWithFieldCollectionGeneric<String> { }

    @Test
    public void testDetectCollectionGeneric() {
        // Given
        final DocumentedBean bean = new DocumentedBean(ClassExtendingCollectionGeneric.class);

        // Then
        assertNotNull(bean);
        assertEquals(1, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("fieldCollection", prop.getName());
        assertEquals("String", prop.getType());
        assertTrue(prop.isList());
        assertFalse(prop.getLinkedConcept());
        assertFalse(prop.isQueryResult());
    }

    // ------------------------------------------------------------------------

    public static class ClassWithLinkedConcept {
        @SuppressWarnings("unused")
        public LinkedConcept<Member> member;
    }

    @Test
    public void init_withLinkedConcept_shouldBeDetailed() {
        // When
        final DocumentedBean bean = new DocumentedBean(ClassWithLinkedConcept.class);

        // Then
        assertNotNull(bean);
        assertEquals(1, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("member", prop.getName());
        assertEquals("Member", prop.getType());
        assertFalse(prop.isList());
        assertTrue(prop.getLinkedConcept());
        assertFalse(prop.isQueryResult());
    }

    // ------------------------------------------------------------------------

    public class ClassWithValidationOnField {
        @SuppressWarnings("unused")
        @NotNull
        public String iCantBeNull;
    }

    @Test
    public void init_withField_withValidation_shouldBeDetailed() {
        // When
        final DocumentedBean bean = new DocumentedBean(ClassWithValidationOnField.class);

        // Then
        assertNotNull(bean);
        assertEquals(1, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("iCantBeNull", prop.getName());
        assertEquals("String", prop.getType());
        assertFalse(prop.isList());
        assertFalse(prop.getLinkedConcept());
        assertFalse(prop.isQueryResult());

        assertEquals(1, prop.getConstraints().size());

        final DocumentedConstraint constraint = prop.getConstraints().iterator().next();
        assertEquals("NotNull", constraint.getType());
    }

    @Test
    public void init_withCollectionQueryResult_shouldBeDetailed() {
        // When
        final DocumentedBean bean = new DocumentedBean(GetAllMembersQueryHandler.AllMembersResult.class);

        // Then
        assertNotNull(bean);
        assertEquals(1, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("list", prop.getName());
        assertEquals("MembersResult", prop.getType());
        assertTrue(prop.isList());
        assertFalse(prop.getLinkedConcept());
        assertTrue(prop.isQueryResult());
        assertEquals(0, prop.getConstraints().size());
    }

    // ------------------------------------------------------------------------

    public class ClassWithDescriptionOnField {
        @SuppressWarnings("unused")
        @XKasperField(description = "a simple field")
        public String field;
    }

    @Test
    public void init_withField_withXKasperField_shouldBeDetailed() {
        // When
        final DocumentedBean bean = new DocumentedBean(ClassWithDescriptionOnField.class);

        // Then
        assertNotNull(bean);
        assertEquals(1, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("field", prop.getName());
        assertEquals("String", prop.getType());
        assertEquals("a simple field", prop.getDescription());
        assertFalse(prop.isList());
        assertFalse(prop.getLinkedConcept());
        assertFalse(prop.isQueryResult());
    }

    public class ClassWithMap {
        @SuppressWarnings("unused")
        @XKasperField(description = "a map")
        public Map<String, Member> fieldMap;
    }

    @Test
    public void init_withMap_shouldBeDetailed() {
        // When
        final DocumentedBean bean = new DocumentedBean(ClassWithMap.class);

        // Then
        assertNotNull(bean);
        assertEquals(1, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("fieldMap", prop.getName());
        assertEquals("Member", prop.getType());
        assertEquals("String", prop.getKeyType());
        assertEquals("a map", prop.getDescription());
        assertTrue(prop.isMap());
        assertFalse(prop.isList());
        assertFalse(prop.getLinkedConcept());
        assertFalse(prop.isQueryResult());
    }

}
