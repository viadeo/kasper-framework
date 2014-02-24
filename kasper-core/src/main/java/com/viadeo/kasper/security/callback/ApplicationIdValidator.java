package com.viadeo.kasper.security.callback;

import com.viadeo.kasper.security.KasperInvalidApplicationIdException;
import com.viadeo.kasper.security.KasperMissingApplicationIdException;


/**
 * Capability to validate a applicationId.
 */
public interface ApplicationIdValidator {

    void validate(String applicationId) throws KasperMissingApplicationIdException, KasperInvalidApplicationIdException;

}
