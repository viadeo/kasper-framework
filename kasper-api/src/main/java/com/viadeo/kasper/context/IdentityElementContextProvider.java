package com.viadeo.kasper.context;


/**
 * Capability to provide an element of identity to the execution Context.
 *
 * Identity elements are userId, user default language, ...
 */
public interface IdentityElementContextProvider {
    void provideIdentityElement(Context context);
}
