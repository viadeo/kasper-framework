// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component;

import com.viadeo.kasper.api.context.Context;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperMessage<INPUT> implements Serializable {

    private final INPUT input;
    private final Context context;

    public KasperMessage(Context context, INPUT input) {
        this.input = checkNotNull(input);
        this.context = checkNotNull(context);
    }

    public Context getContext() {
        return context;
    }

    public INPUT getInput() {
        return input;
    }
}
