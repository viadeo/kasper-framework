// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.callback;

import com.viadeo.kasper.security.exception.KasperInvalidApplicationIdException;
import com.viadeo.kasper.security.exception.KasperMissingApplicationIdException;

/**
 * Capability to validate a applicationId.
 */
public interface ApplicationIdValidator {

    void validate(String applicationId)
            throws KasperMissingApplicationIdException,
                   KasperInvalidApplicationIdException;

}
