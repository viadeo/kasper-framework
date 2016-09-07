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
import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

class QueryResponseFuture<P extends QueryResult> extends ResponseFuture<QueryResponse<P>> {
    private final TypeToken<P> mapTo;
    private KasperClient kasperClient;

    // ------------------------------------------------------------------------

    public QueryResponseFuture(final KasperClient kasperClient,
                               final Future<ClientResponse> futureResponse,
                               final TypeToken<P> mapTo) {
        super(futureResponse);

        this.kasperClient = checkNotNull(kasperClient);
        this.mapTo = checkNotNull(mapTo);
    }

    // ------------------------------------------------------------------------

    public QueryResponse<P> get() throws InterruptedException, ExecutionException {
        try {
            return get(KasperClient.DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            futureResponse().cancel(true);
            throw propagate(e);
        }
    }

    public QueryResponse<P> get(final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        ClientResponse clientResponse = futureResponse().get(timeout, unit);
        try {
            return kasperClient.handleQueryResponse(clientResponse, mapTo);
        } finally {
            kasperClient.closeClientResponse(clientResponse);
        }
    }

}
