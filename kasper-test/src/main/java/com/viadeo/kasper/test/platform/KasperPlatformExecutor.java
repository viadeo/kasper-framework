package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.test.platform.executor.KasperFixtureCommandExecutor;
import com.viadeo.kasper.test.platform.executor.KasperFixtureEventExecutor;
import com.viadeo.kasper.test.platform.executor.KasperFixtureQueryExecutor;

public class KasperPlatformExecutor implements
        KasperFixtureCommandExecutor<KasperPlatformCommandResultValidator>,
        KasperFixtureQueryExecutor<KasperPlatformQueryResultValidator>,
        KasperFixtureEventExecutor<KasperPlatformEventResultValidator>
{

    private final KasperPlatformFixture.RecordingPlatform platform;

    // ------------------------------------------------------------------------

    KasperPlatformExecutor(final KasperPlatformFixture.RecordingPlatform platform) {
        this.platform = platform;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperPlatformCommandResultValidator when(final Command command) {
        return this.when(command, DefaultContextBuilder.get());
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
        return this.when(query, DefaultContextBuilder.get());
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
    public KasperPlatformEventResultValidator when(Event event) {
        return this.when(event, DefaultContextBuilder.get());
    }

    @Override
    public KasperPlatformEventResultValidator when(Event event, Context context) {
        Exception exception = null;
        try {
            platform.get().getEventBus().publishEvent(context, event);
        } catch (final Exception e) {
            exception = e;
        }
        return new KasperPlatformEventResultValidator(platform, exception);
    }
}
