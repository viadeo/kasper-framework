// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.QueryResult;
import org.axonframework.test.AxonAssertionError;

import static com.viadeo.kasper.KasperResponse.Status.*;
import static com.viadeo.kasper.tools.KasperMatcher.equalTo;

/**
 * FIXME: add better debugging information
 */
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
    public KasperFixtureQueryResultValidator expectReturnOK() {
        System.out.print("#######################");
        System.out.print("KasperFixtureQueryResultValidator expectReturnOK");
        System.out.print("response() : "+response());
        System.out.print("QueryResponse) response()).getStatus() : "+((QueryResponse) response()).getStatus());
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
