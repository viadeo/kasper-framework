// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.interceptor;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.core.interceptor.BaseSecurityInterceptor;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.exception.KasperSecurityException;
import com.viadeo.kasper.security.SecurityConfiguration;

public class CommandSecurityInterceptor<C extends Command> extends BaseSecurityInterceptor
        implements Interceptor<C, CommandResponse> {

    public CommandSecurityInterceptor(SecurityConfiguration securityConfiguration) {
        super(securityConfiguration);
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandResponse process(final C input,
                                   final Context context,
                                   final InterceptorChain<C, CommandResponse> chain) throws Exception {
        try {
            addSecurityIdentity(context);
        } catch (final KasperSecurityException e) {
            return CommandResponse.error(
                    new KasperReason(
                            CoreReasonCode.INVALID_INPUT.name(),
                            e.getMessage()
                    ));
        }
        return chain.next(input, context);
    }

}
