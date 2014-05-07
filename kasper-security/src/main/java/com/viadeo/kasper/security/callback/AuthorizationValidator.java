// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.callback;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.security.exception.KasperUnauthorizedException;

public interface AuthorizationValidator {

    //permission/role resolver

    void validate(Context context, Class<?> clazz)
            throws KasperUnauthorizedException;
}
