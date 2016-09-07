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
package com.viadeo.kasper.api.id;

import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class SimpleIDBuilderUTest {

    private SimpleIDBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new SimpleIDBuilder(
                TestFormats.DB_ID,
                TestFormats.UUID
        );
    }

    @Test
    public void get_supported_formats_is_Ok() {
        // When
        Collection<Format> supportedFormats = builder.getSupportedFormats();

        // Then
        assertTrue(supportedFormats.contains(TestFormats.DB_ID));
        assertTrue(supportedFormats.contains(TestFormats.UUID));
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_withInvalidURN_isKo() {
        // Given
        String urn = "urn:miaou:42";

        // When
        builder.build(urn);
    }

    @Test(expected = IllegalArgumentException.class)
    public void build_withValidURN_withUnknownFormat_isKo() {
        // Given
        String urn = "urn:viadeo:member:web-id:42";

        // When
        builder.build(urn);
    }

    @Test
    public void build_withValidURN_withKnownFormat_isOk() {
        // Given
        String urn = "urn:viadeo:member:db-id:42";

        // When
        ID id = builder.build(urn);

        // Then
        assertNotNull(id);
        assertFalse(id.getTransformer().isPresent());
    }
}
