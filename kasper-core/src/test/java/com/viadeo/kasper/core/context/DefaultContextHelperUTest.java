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
package com.viadeo.kasper.core.context;

import com.google.common.collect.ImmutableMap;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.FormatAdapter;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.SimpleIDBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DefaultContextHelperUTest {

    private static final Format DB_ID = new FormatAdapter("db-id", Integer.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(String identifier) {
            return (E) new Integer(identifier);
        }
    };

    private DefaultContextHelper contextHelper;

    @Before
    public void setUp() throws Exception {
        contextHelper = new DefaultContextHelper(new SimpleIDBuilder(DB_ID));
    }

    @Test
    public void toContext_withBasicProperty_isOk() {
        // Given
        Context givenContext = Contexts.builder().withFunnelVersion("funnelVr").build();

        // When
        Context actualContext = contextHelper.createFrom(givenContext.asMap());

        // Then
        assertNotNull(actualContext);
        assertEquals("funnelVr", actualContext.getFunnelVersion().orNull());
    }

    @Test
    public void toContext_withCustomProperty_isOk() {
        // Given
        Context givenContext = Contexts.builder().with("miaou", "hello kitty!").build();

        // When
        Context actualContext = contextHelper.createFrom(givenContext.asMap());

        // Then
        assertNotNull(actualContext);
        assertTrue(actualContext.getProperties().containsKey("miaou"));
        assertEquals("hello kitty!", actualContext.getProperty("miaou").get());
    }

    @Test
    public void toContext_withEmptyUserID_isOk() {
        // When
        Context actualContext = contextHelper.createFrom(ImmutableMap.<String, String>builder().put(Context.USER_ID_SHORTNAME, "").build());

        // Then
        assertNotNull(actualContext);
        assertFalse(actualContext.getUserID().isPresent());
    }

    @Test
    public void toContext_withUserID_isOk() {
        // Given
        Context givenContext = Contexts.builder()
                .withUserID(new ID("viadeo", "member", DB_ID, 4))
                .withSecurityToken("003chh8bxkrn338")
                .build();

        // When
        Context actualContext = contextHelper.createFrom(givenContext.asMap());

        // Then
        assertNotNull(actualContext);
        assertEquals(givenContext.getSecurityToken(), actualContext.getSecurityToken());
        assertEquals(givenContext.getUserID(), actualContext.getUserID());
    }
}
