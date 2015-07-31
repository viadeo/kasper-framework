package com.viadeo.kasper.core.component;

import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.KasperResponse;

/**
 * Determines an <code>Handler</code>.
 *
 * @param <RESPONSE> the response returned by the <code>Handler</code>
 * @param <INPUT> the input handled by the <code>Handler</code>
 */
public interface Handler<RESPONSE extends KasperResponse, INPUT> {

    RESPONSE handle(Context context, INPUT input);

    /**
     * @return the input class handled by this <code>Handler</code>.
     */
    Class<INPUT> getInputClass();

    /**
     * @return the handler class
     */
    Class<?> getHandlerClass();

}
