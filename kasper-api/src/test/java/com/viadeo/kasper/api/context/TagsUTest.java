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
package com.viadeo.kasper.api.context;

import org.junit.Test;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TagsUTest {

    @Test
    @SuppressWarnings("all")
    public void valueOf_withEmptyString_shouldEmptyTags() {
        // Given
        final String string = "";

        // When
        final Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet(), tags);
    }

    @Test
    public void valueOf_withATag_shouldReturnTheTag() {
        // Given
        final String string = "a-tag";

        // When
        final Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet("a-tag"), tags);
    }

    @Test
    public void valueOf_withTags_shouldSplitTheTags() {
        // Given
        final String string = "a-tag,another-tag";

        // When
        final Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet("a-tag", "another-tag"), tags);
    }

    @Test
    public void valueOf_withTags_shouldOmitEmptyTags() {
        // Given
        final String string = ",,a-tag,,,,,,,,,another-tag,";

        // When
        final Set<String> tags = Tags.valueOf(string);

        // Then
        assertEquals(newHashSet("a-tag", "another-tag"), tags);
    }

    // ------------------------------------------------------------------------

    @Test
    public void toString_withNoTags_shouldReturnTheEmptyString() {
        // Given
        final Set<String> tags = newHashSet();

        // When
        final String theString = Tags.toString(tags);

        // Then
        assertEquals("", theString);
    }

    @Test
    public void toString_withTags_shouldReturnTheTag() {
        // Given
        final Set<String> tags = newHashSet("a-tag");

        // When
        final String theString = Tags.toString(tags);

        // Then
        assertEquals("a-tag", theString);
    }

    @Test
    public void toString_withTags_shouldJoinThem() {
        // Given
        final Set<String> tags = newHashSet("a-tag", "another-tag");

        // When
        final String theString = Tags.toString(tags);

        // Then
        assertTrue(newHashSet("a-tag,another-tag", "another-tag,a-tag").contains(theString)); // order is not important
    }

    @Test
    public void toString_withNullTag_shouldOmitIt() {
        // Given
        final Set<String> tags = newHashSet("a-tag", null, "another-tag");

        // When
        final String theString = Tags.toString(tags);

        // Then
        assertTrue(newHashSet("a-tag,another-tag", "another-tag,a-tag").contains(theString)); // order is not important
    }

}
