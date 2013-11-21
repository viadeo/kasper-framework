// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;

import static org.axonframework.test.matchers.Matchers.equalTo;

public class KasperPlatformQueryResultValidator
        extends KasperPlatformResultValidator
        implements KasperFixtureQueryResultValidator {

    // ------------------------------------------------------------------------

    KasperPlatformQueryResultValidator(
            final KasperPlatformFixture.RecordingPlatform platform,
            final QueryResponse response,
            final Exception exception) {
        super(platform, response, exception);
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperPlatformQueryResultValidator expectReturnResponse(final QueryResponse queryResponse) {
        expectReturnValue(equalTo(queryResponse));
        return this;
    }

    @Override
    public KasperPlatformQueryResultValidator expectReturnError(final KasperReason reason) {
        expectReturnValue(equalTo(QueryResponse.error(reason)));
        return this;
    }

    @Override
    public KasperPlatformQueryResultValidator expectReturnRefused(final KasperReason reason) {
        expectReturnValue(equalTo(QueryResponse.refused(reason)));
        return this;
    }

    @Override
    public KasperPlatformQueryResultValidator expectReturnResponse(final QueryResult result) {
        expectReturnValue(equalTo(QueryResponse.of(result)));
        return this;
    }

}
