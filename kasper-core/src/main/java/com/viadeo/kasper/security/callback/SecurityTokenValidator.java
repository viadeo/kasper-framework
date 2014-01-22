package com.viadeo.kasper.security.callback;

import com.viadeo.kasper.security.KasperInvalidSecurityTokenException;
import com.viadeo.kasper.security.KasperMissingSecurityTokenException;


/**
 * Capability to validate a security token.
 */
public interface SecurityTokenValidator {

    void validate(String securityToken) throws KasperMissingSecurityTokenException, KasperInvalidSecurityTokenException;

}
