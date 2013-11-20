package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResponse;

import java.util.HashMap;
import java.util.Map;

public class KasperPlatformExecutor implements KasperFixtureCommandExecutor<KasperPlatformResultValidator> {

    private final KasperPlatformFixture.RecordingPlatform platform;

    // ------------------------------------------------------------------------

    KasperPlatformExecutor(final KasperPlatformFixture.RecordingPlatform platform) {
        this.platform = platform;
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperPlatformResultValidator when(final Command command) {
        return this.when(command, DefaultContextBuilder.get());
    }

    @Override
    public KasperPlatformResultValidator when(final Command command, final Context context) {
        final Map<String, Object> metaContext = new HashMap<String, Object>() {{
            this.put(Context.METANAME, context);
        }};

        CommandResponse response = null;
        Exception exception = null;
        try {
            response = platform.get().getCommandGateway().sendCommandAndWaitForAResponse(command, context);
        } catch (final Exception e) {
            exception = e;
        }

        return new KasperPlatformResultValidator(platform, response, exception);
    }

}
