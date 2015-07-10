package com.viadeo.kasper.core.context;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.Contexts;
import com.viadeo.kasper.context.Version;
import static com.google.common.base.Preconditions.checkNotNull;

public class ContextVersion implements Version {

    private final String currentAppVersion;
    private final String currentClientVersion;

    public ContextVersion(final Integer currentAppVersion, final Integer currentClientVersion) {
        this.currentAppVersion = checkNotNull(currentAppVersion).toString();
        this.currentClientVersion = checkNotNull(currentClientVersion).toString();
    }

    @Override
    public Context apply(final Context context) {
        checkNotNull(context);

        final Context.Builder builder = Contexts.newFrom(context);


        if (context.getApplicationId().isPresent() && ! context.getApplicationVersion().isPresent() &&
                ! context.getClientId().isPresent() &&
                ( ! context.getClientVersion().isPresent() || "nc".equals(context.getClientVersion().get())) ) {
            builder
                    .reset(Context.APPLICATION_ID_SHORTNAME)
                    .withClientId(context.getApplicationId().get())
                    .with(Context.CLIENT_VERSION_SHORTNAME, currentClientVersion);
        }

        if ( !  builder.build().getClientId().isPresent()) {
            builder.withClientId("UNKOWN");
        }

        return builder.build();
    }

    public String getCurrentAppVersion() {
        return currentAppVersion;
    }

    public String getCurrentClientVersion() {
        return currentClientVersion;
    }
}
