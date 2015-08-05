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

/**
 * Represents a Message carrying an input with its context.
 *
 * @param <INPUT> the input class carried by this <code>KasperMessage</code>
 */
public abstract class KasperMessage<INPUT> implements Serializable {

    private final INPUT input;
    private final Context context;

    /**
     * Initialize a new message from the specified context and input
     * @param context the context
     * @param input the input
     */
    public KasperMessage(Context context, INPUT input) {
        this.input = checkNotNull(input);
        this.context = checkNotNull(context);
    }

    /**
     * @return the carried context
     */
    public Context getContext() {
        return context;
    }

    /**
     * @return the carried input
     */
    public INPUT getInput() {
        return input;
    }
}
