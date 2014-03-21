// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.callback;

import com.viadeo.kasper.security.exception.KasperInvalidIpAddressException;
import com.viadeo.kasper.security.exception.KasperMissingIpAddressException;

/**
 * Capability to validate a ipAddress.
 */
public interface IpAddressValidator {

    void validate(final String ipAddress)
            throws KasperMissingIpAddressException,
                   KasperInvalidIpAddressException;

}
