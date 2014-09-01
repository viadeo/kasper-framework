// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.cqrs;

import com.google.common.base.Strings;
import com.google.common.net.InetAddresses;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.security.exception.KasperInvalidIpAddressException;
import com.viadeo.kasper.security.exception.KasperMissingIpAddressException;

public class IpAddressInterceptor<C extends Object> implements Interceptor<C, Object> {

    private static final String ERROR_MSG = "X-FORWARDED-FOR header must be filled with a valid ip Address";

    @Override
    public Object process(C c, Context context, InterceptorChain<C, Object> chain) throws Exception {
        if (Strings.isNullOrEmpty(context.getIpAddress())) {
            throw new KasperMissingIpAddressException(
                    ERROR_MSG,
                    CoreReasonCode.INVALID_INPUT
            );
        }

        if (!InetAddresses.isInetAddress(context.getIpAddress())) {
            throw new KasperInvalidIpAddressException(
                    ERROR_MSG,
                    CoreReasonCode.INVALID_INPUT
            );
        }

        return chain.next(c, context);
    }
}
