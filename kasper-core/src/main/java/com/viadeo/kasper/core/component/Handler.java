// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component;

import com.viadeo.kasper.api.response.KasperResponse;

/**
 * Determines an <code>Handler</code>.
 *
 * @param <MESSAGE> the message sent to the <code>Handler</code>
 * @param <RESPONSE> the response returned by the <code>Handler</code>
 * @param <INPUT> the input handled by the <code>Handler</code>
 */
public interface Handler<MESSAGE extends KasperMessage<INPUT>, RESPONSE extends KasperResponse, INPUT> {

    RESPONSE handle(MESSAGE message);

    /**
     * @return the input class handled by this <code>Handler</code>.
     */
    Class<INPUT> getInputClass();

    /**
     * @return the handler class
     */
    Class<?> getHandlerClass();

}
