// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.test.platform.validator.KasperFixtureQueryResultValidator;
import com.viadeo.kasper.test.platform.validator.base.DefaultBaseValidator;
import org.axonframework.test.AxonAssertionError;

import static com.viadeo.kasper.api.response.KasperResponse.Status.*;
import static com.viadeo.kasper.test.platform.KasperMatcher.equalTo;

/**
 * FIXME: add better debugging information
 */
public class KasperPlatformQueryResultValidator
        extends DefaultBaseValidator
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
    public KasperFixtureQueryResultValidator expectReturnOK() {
        if ((null == response()) ||  ! OK.equals(((QueryResponse) response()).getStatus())) {
            throw new AxonAssertionError("Query did not answered OK");
        }
        return this;
    }

    public KasperFixtureQueryResultValidator expectReturnError() {
        if ((null == response()) ||  ! ERROR.equals(((QueryResponse) response()).getStatus())) {
            throw new AxonAssertionError("Query did not answered an ERROR");
        }
        return this;
    }

    public KasperFixtureQueryResultValidator expectReturnRefused() {
        if ((null == response()) ||  ! REFUSED.equals(((QueryResponse) response()).getStatus())) {
            throw new AxonAssertionError("Query did not answered an ERROR");
        }
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

    public KasperFixtureQueryResultValidator expectReturnError(final String code) {
        if ((null == response())
                || (null == ((QueryResponse) response()).getReason())
                || ! ERROR.equals(((QueryResponse) response()).getStatus())
                || ! ((QueryResponse) response()).getReason().getCode().contentEquals(code)) {
            throw new AxonAssertionError(
                    "Query did not answered the expected error code"
            );
        }
        return this;
    }

    public KasperFixtureQueryResultValidator expectReturnRefused(final String code) {
        if ((null == response())
                || (null == ((QueryResponse) response()).getReason())
                || ! REFUSED.equals(((QueryResponse) response()).getStatus())
                || ! ((QueryResponse) response()).getReason().getCode().contentEquals(code)) {
            throw new AxonAssertionError(
                    "Query did not answered the expected error code"
            );
        }
        return this;
    }

    public KasperFixtureQueryResultValidator expectReturnError(final CoreReasonCode code) {
         if ((null == response())
                || (null == ((QueryResponse) response()).getReason())
                || ! ERROR.equals(((QueryResponse) response()).getStatus())
                || ! ((QueryResponse) response()).getReason().getCode().contentEquals(code.name())) {
            throw new AxonAssertionError(
                    "Query did not answered the expected error code"
            );
        }
        return this;
    }

    public KasperFixtureQueryResultValidator expectReturnRefused(final CoreReasonCode code) {
        if ((null == response())
                || (null == ((QueryResponse) response()).getReason())
                || ! REFUSED.equals(((QueryResponse) response()).getStatus())
                || ! ((QueryResponse) response()).getReason().getCode().contentEquals(code.name())) {
            throw new AxonAssertionError(
                    "Query did not answered the expected error code"
            );
        }
        return this;
    }

}
