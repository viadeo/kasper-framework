package com.viadeo.kasper.security;


import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.exception.KasperSecurityException;

/**
 * Capability to provide an element of identity to the execution Context.
 *
 * Identity elements are userId, user default language, ...
 */
public interface IdentityElementContextProvider {
    void provideIdentityElement(Context context) throws KasperSecurityException;
}
