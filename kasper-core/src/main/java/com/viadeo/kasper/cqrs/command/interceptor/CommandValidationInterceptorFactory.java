// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.interceptor;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.Interceptor;
import com.viadeo.kasper.core.interceptor.InterceptorChain;
import com.viadeo.kasper.cqrs.command.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;

public class CommandValidationInterceptorFactory extends CommandInterceptorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandValidationInterceptorFactory.class);

    @Override
    @SuppressWarnings("unchecked")
    public Optional<InterceptorChain<Command, Command>> create(TypeToken<?> type) {
        final Interceptor<Command, Command> interceptor;

        try {
            interceptor = new CommandValidationInterceptor(Validation.buildDefaultValidatorFactory());
        } catch (final ValidationException ve) {
            LOGGER.warn("Unexpected error when instantiating interceptor for `{}` : No implementation found for BEAN VALIDATION - JSR 303", type.getRawType(), ve);
            return Optional.absent();
        }

        return Optional.of(InterceptorChain.makeChain(interceptor));
    }
}
