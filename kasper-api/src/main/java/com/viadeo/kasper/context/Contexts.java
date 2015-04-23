// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Contexts {

    private Contexts() {}

    public static Context.Builder newFrom(final Context context) {
        return new Context.Builder(checkNotNull(context));
    }

    public static Context.Builder builder() {
        return new Context.Builder();
    }

    public static Context.Builder builder(UUID kasperCorrelationId) {
        return new Context.Builder(kasperCorrelationId);
    }

    public static Context empty() {
        return builder()
                .withApplicationId("UNKNOWN")
                .withUserLang("fr")
                .withUserCountry("FR")
                .withIpAddress("0.0.0.0")
                .build();
    }
}
