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
package com.viadeo.kasper.api.component.query;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Data Transfer Object
 *
 * Represents an anemic transfer entity, in the Query semantics
 *
 * Can be used to store some properties of a root entity which can be later the
 * base entity of a Kasper CQRS domain entity command.
 */
public class QueryResponse<RESULT extends QueryResult> extends KasperResponse {
    private static final long serialVersionUID = -6543664128786160837L;

    private final RESULT result;

    // ------------------------------------------------------------------------

    /**
     * Get a failure response which is an unexpected and can require intervention before the system can resume at the
     * same level of operation. This does not mean that failures are always fatal, rather that some capacity of the
     * system will be reduced following a failure.
     *
     * @param reason a reason
     * @param <R> the type of result
     * @return a failure response
     */
    public static <R extends QueryResult> QueryResponse<R> failure(final KasperReason reason) {
        return new QueryResponse<R>(Status.FAILURE, reason);
    }

    // ------------------------------------------------------------------------

    /**
     * Get an error response which is an expected part of normal operations.
     *
     * @param reason  the reason
     * @param <R> the type of result
     * @return an error response
     */
    public static <R extends QueryResult> QueryResponse<R> error(final KasperReason reason) {
        return new QueryResponse<R>(checkNotNull(reason));
    }

    /**
     * Get an error response which is an expected part of normal operations.
     *
     * @param code  the core reason code
     * @param <R> the type of result
     * @return an error response
     */
    public static <R extends QueryResult> QueryResponse<R> error(final CoreReasonCode code) {
        return new QueryResponse<R>(new KasperReason(checkNotNull(code)));
    }

    // ------------------------------------------------------------------------

    /**
     * Get a refused response.
     *
     * @param reason the reason
     * @param <R> the type of result
     * @return a refused response
     */
    public static <R extends QueryResult> QueryResponse<R> refused(final KasperReason reason) {
        return new QueryResponse<R>(Status.REFUSED, checkNotNull(reason));
    }

    /**
     * Get a refused response.
     *
     * @param code the core reason code
     * @param <R> the type of result
     * @return a refused response
     */
    public static <R extends QueryResult> QueryResponse<R> refused(final CoreReasonCode code) {
        return new QueryResponse<R>(Status.REFUSED, new KasperReason(checkNotNull(code)));
    }

    // ------------------------------------------------------------------------

    /**
     * Get a result in the success response
     *
     * @param result the result to returned
     * @param <R> the type of result
     * @return a success response
     */
    public static <R extends QueryResult> QueryResponse<R> of(final R result) {
        return new QueryResponse<R>(checkNotNull(result));
    }

    // ------------------------------------------------------------------------

    public QueryResponse(final KasperResponse response) {
        super(response);
        this.result = null;
    }

    public QueryResponse(final KasperResponse response, final RESULT result) {
        super(response);
        this.result = checkNotNull(result);
    }

    public QueryResponse(final QueryResponse<RESULT> response) {
        super(response);
        this.result = response.result;
    }

    public QueryResponse(final RESULT result) {
        super();
        this.result = checkNotNull(result);
    }
    
    public QueryResponse(final KasperReason reason) {
        this(Status.ERROR, checkNotNull(reason));
    }

    public QueryResponse(final Status status, final KasperReason reason) {
        super(status, reason);
        this.result = null;
    }

    // ------------------------------------------------------------------------

    public RESULT getResult() {
        return result;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final QueryResponse other = (QueryResponse) obj;

        if ( ! super.equals(obj)) {
            return false;
        }

        return Objects.equal(this.result, other.result);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + com.google.common.base.Objects.hashCode(this.result);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .addValue(super.toString())
                .addValue(this.result)
                .toString();
    }

}
