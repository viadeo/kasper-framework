// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.callback;

import com.viadeo.kasper.security.exception.KasperInvalidSecurityTokenException;
import com.viadeo.kasper.security.exception.KasperMissingSecurityTokenException;

/**
 * Capability to validate a security token.
 */
public interface SecurityTokenValidator {

    void validate(final String securityToken)
            throws KasperMissingSecurityTokenException,
                   KasperInvalidSecurityTokenException;

}
