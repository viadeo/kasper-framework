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

import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.test.platform.executor.KasperFixtureCommandExecutor;
import com.viadeo.kasper.test.platform.executor.KasperFixtureEventExecutor;
import com.viadeo.kasper.test.platform.executor.KasperFixtureQueryExecutor;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.test.platform.KasperPlatformFixture.RecordingPlatform;

public class KasperPlatformExecutor implements
        KasperFixtureCommandExecutor<KasperPlatformCommandResultValidator>,
        KasperFixtureQueryExecutor<KasperPlatformQueryResultValidator>,
        KasperFixtureEventExecutor<KasperPlatformListenedEventsValidator> {

    private final RecordingPlatform platform;

    // ------------------------------------------------------------------------

    KasperPlatformExecutor(final RecordingPlatform platform) {
        this.platform = checkNotNull(platform);
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperPlatformCommandResultValidator when(final Command command) {
        return this.when(command, Contexts.empty());
    }

    @Override
    public KasperPlatformCommandResultValidator when(final Command command, final Context context) {
        CommandResponse response = null;
        Exception exception = null;
        try {
            response = platform.get().getCommandGateway().sendCommandAndWaitForAResponse(command, context);
        } catch (final Exception e) {
            exception = e;
        }

        return new KasperPlatformCommandResultValidator(platform, response, exception);
    }

    @Override
    public KasperPlatformQueryResultValidator when(final Query query) {
        return this.when(query, Contexts.empty());
    }

    @Override
    public KasperPlatformQueryResultValidator when(final Query query, Context context) {
        QueryResponse response = null;
        Exception exception = null;
        try {
            response = platform.get().getQueryGateway().retrieve(query, context);
        } catch (final Exception e) {
            exception = e;
        }

        return new KasperPlatformQueryResultValidator(platform, response, exception);
    }

    @Override
    public KasperPlatformListenedEventsValidator when(final Event event) {
        return this.when(event, Contexts.empty());
    }

    @Override
    public KasperPlatformListenedEventsValidator when(final Event event, final Context context) {
        Exception exception = null;
        try {
            platform.get().getEventBus().publish(context, event);
        } catch (final Exception e) {
            exception = e;
        }
        return new KasperPlatformListenedEventsValidator(platform, exception);
    }

}
