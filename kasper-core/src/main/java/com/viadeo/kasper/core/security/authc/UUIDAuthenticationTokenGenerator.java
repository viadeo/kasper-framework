// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.security.authc;

import com.viadeo.kasper.api.context.Context;

import java.io.Serializable;
import java.util.UUID;

public class UUIDAuthenticationTokenGenerator implements AuthenticationTokenGenerator {

    @Override
    public Serializable generate(Context context) {
        return UUID.randomUUID().toString();
    }
}
