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
import com.viadeo.kasper.query.exposition.TypeAdapter;
import com.viadeo.kasper.query.exposition.adapters.NullSafeTypeAdapter;
import com.viadeo.kasper.query.exposition.query.QueryBuilder;
import com.viadeo.kasper.query.exposition.query.QueryParser;
import org.junit.Assert;
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
        Assert.assertEquals("/kasper/query/test", resource.getURI().getPath());
    }

    @Test public void commandBaseLocation_withBaseUrlWithoutTrailingSlash_shouldAddTrailingSlash() {
        // given
        final String baseUrl = "http://localhost:8080/kasper/command";

        // when
        final KasperClient kasperClient = new KasperClientBuilder().commandBaseLocation(baseUrl).create();
        final WebResource resource = kasperClient.client.resource(kasperClient.resolveCommandPath(TestCommand.class));

        // then
        Assert.assertEquals("/kasper/command/test", resource.getURI().getPath());
    }

    @Test public void queryBaseLocation_withBaseUrlWithTrailingSlash_shouldNotAddTrailingSlash() {
        // given
        final String baseUrl = "http://localhost:8080/kasper/query/";

        // when
        final KasperClient kasperClient = new KasperClientBuilder().queryBaseLocation(baseUrl).create();
        final WebResource resource = kasperClient.client.resource(kasperClient.resolveQueryPath(TestQuery.class));

        // then
        Assert.assertEquals("/kasper/query/test", resource.getURI().getPath());
    }

    @Test public void commandBaseLocation_withBaseUrlWithTrailingSlash_shouldNotAddTrailingSlash() {
        // given
        final String baseUrl = "http://localhost:8080/kasper/command/";

        // when
        final KasperClient kasperClient = new KasperClientBuilder().commandBaseLocation(baseUrl).create();
        final WebResource resource = kasperClient.client.resource(kasperClient.resolveCommandPath(TestCommand.class));

        // then
        Assert.assertEquals("/kasper/command/test", resource.getURI().getPath());
    }

}
