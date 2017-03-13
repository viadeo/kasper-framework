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
package com.viadeo.kasper.client;

import com.google.common.reflect.TypeToken;
import com.sun.jersey.api.client.WebResource;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.common.exposition.TypeAdapter;
import com.viadeo.kasper.common.exposition.adapters.NullSafeTypeAdapter;
import com.viadeo.kasper.common.exposition.query.QueryBuilder;
import com.viadeo.kasper.common.exposition.query.QueryParser;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class KasperClientBuilderTest {

    class TestQuery implements Query {}
    class TestCommand implements Command {}

    // ------------------------------------------------------------------------

    @Test public void testCustomTypeAdapterOverrideDefault() {
        // Given
        final TypeAdapter<Date> expected = new TypeAdapter<Date>() {
            public void adapt(final Date value, final QueryBuilder builder) {
                // Empty
            }
            
            @Override
            public Date adapt(final QueryParser parser) {
            	return null;
            }
        };
        
        // When
        final TypeAdapter<Date> actual = new KasperClientBuilder().use(expected).create()
                                                .queryFactory.create(TypeToken.of(Date.class));
        
        // Then
        assertEquals(expected, ((NullSafeTypeAdapter<Date>) actual).unwrap());
    }

    @Test public void queryBaseLocation_withBaseUrlWithoutTrailingSlash_shouldAddTrailingSlash() {
        // given
        final String baseUrl = "http://localhost:8080/kasper/query";

        // when
        final KasperClient kasperClient = new KasperClientBuilder().queryBaseLocation(baseUrl).create();
        final WebResource resource = kasperClient.client.resource(kasperClient.resolveQueryPath(TestQuery.class));

        // then
        assertEquals("/kasper/query/test", resource.getURI().getPath());
    }

    @Test public void commandBaseLocation_withBaseUrlWithoutTrailingSlash_shouldAddTrailingSlash() {
        // given
        final String baseUrl = "http://localhost:8080/kasper/command";

        // when
        final KasperClient kasperClient = new KasperClientBuilder().commandBaseLocation(baseUrl).create();
        final WebResource resource = kasperClient.client.resource(kasperClient.resolveCommandPath(TestCommand.class));

        // then
        assertEquals("/kasper/command/test", resource.getURI().getPath());
    }

    @Test public void queryBaseLocation_withBaseUrlWithTrailingSlash_shouldNotAddTrailingSlash() {
        // given
        final String baseUrl = "http://localhost:8080/kasper/query/";

        // when
        final KasperClient kasperClient = new KasperClientBuilder().queryBaseLocation(baseUrl).create();
        final WebResource resource = kasperClient.client.resource(kasperClient.resolveQueryPath(TestQuery.class));

        // then
        assertEquals("/kasper/query/test", resource.getURI().getPath());
    }

    @Test public void commandBaseLocation_withBaseUrlWithTrailingSlash_shouldNotAddTrailingSlash() {
        // given
        final String baseUrl = "http://localhost:8080/kasper/command/";

        // when
        final KasperClient kasperClient = new KasperClientBuilder().commandBaseLocation(baseUrl).create();
        final WebResource resource = kasperClient.client.resource(kasperClient.resolveCommandPath(TestCommand.class));

        // then
        assertEquals("/kasper/command/test", resource.getURI().getPath());
    }

}
