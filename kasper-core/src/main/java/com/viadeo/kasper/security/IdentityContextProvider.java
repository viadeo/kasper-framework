// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
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
