package com.viadeo.kasper.security;


import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.exception.KasperSecurityException;

/**
 * Capability to provide identity elements to the execution Context.
 * Identity elements are userId and user default language and country.
 */
public interface IdentityContextProvider {
    void provideIdentity(Context context) throws KasperSecurityException;
}
