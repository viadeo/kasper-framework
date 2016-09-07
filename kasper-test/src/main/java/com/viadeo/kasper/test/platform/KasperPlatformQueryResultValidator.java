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
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
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
