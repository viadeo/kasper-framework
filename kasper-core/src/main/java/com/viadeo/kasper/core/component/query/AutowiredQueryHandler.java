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
package com.viadeo.kasper.core.component.query;

import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 * This is a convenient class
 *
 * Extend it instead of implementing IQueryHandler will allow you to
 * override at your convenience handle(query)
 *
 * The query gateway is aware of this internal convenience and will deal with it
 *
 * @param <Q> the query
 * @param <RESULT> the Response
 */
public abstract class AutowiredQueryHandler<Q extends Query, RESULT extends QueryResult>
    extends BaseQueryHandler<Q,RESULT>
    implements WirableQueryHandler<Q,RESULT>
{

    private transient KasperEventBus eventBus;
    private transient QueryGateway queryGateway;

    // ------------------------------------------------------------------------

    protected AutowiredQueryHandler() { }

    // ------------------------------------------------------------------------

    /**
     * @param context The context
     * @param query The command to handle
     * @return the command response
     */
    public QueryResponse<RESULT> handle(Context context, Q query) {
        try {
            return handle(query);
        } catch (final UnsupportedOperationException e) {
            throw new UnsupportedOperationException();
        }
    }


    public QueryResponse<RESULT> handle(final Q query) {
        throw new UnsupportedOperationException();
    }

    // ------------------------------------------------------------------------

    /**
     * Publish an event on the event bus
     *
     * @param event The event
     */
    public void publish(final Context context, final Event event) {
        checkNotNull(event, "The specified event must be non null");
        checkNotNull(context, "The specified context must be non null");
        checkState(eventBus != null, "Unable to publish the specified event : the event bus is null");

        this.eventBus.publish(context, event);
    }

    // ------------------------------------------------------------------------

    public QueryGateway getQueryGateway() {
        return queryGateway;
    }

    // ------------------------------------------------------------------------

    @Override
    public void setEventBus(final KasperEventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }

    @Override
    public void setQueryGateway(final QueryGateway queryGateway) {
        this.queryGateway = checkNotNull(queryGateway);
    }

}

