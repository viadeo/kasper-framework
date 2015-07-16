// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.event.Event;
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
            platform.get().getEventBus().publishEvent(context, event);
        } catch (final Exception e) {
            exception = e;
        }
        return new KasperPlatformListenedEventsValidator(platform, exception);
    }

}
