// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.command.interceptor.CommandValidationInterceptor;
import com.viadeo.kasper.test.platform.executor.KasperFixtureCommandExecutor;
import org.axonframework.commandhandling.interceptors.JSR303ViolationException;
import org.axonframework.test.TestExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperAggregateExecutor implements KasperFixtureCommandExecutor<KasperAggregateResultValidator> {
    private static final Logger LOGGER = LoggerFactory.getLogger(KasperAggregateExecutor.class);

    private final TestExecutor executor;

    // ------------------------------------------------------------------------

    KasperAggregateExecutor(final TestExecutor executor) {
        this.executor = checkNotNull(executor);
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperAggregateResultValidator when(final Command command) {
        return this.when(command, Contexts.empty());
    }

    @Override
    public KasperAggregateResultValidator when(final Command command, final Context context) {
        final Map<String, Object> metaContext = Maps.newHashMap();
        metaContext.put(Context.METANAME, context);

        try {
            new CommandValidationInterceptor<>(Validation.buildDefaultValidatorFactory()).validate(command);
        } catch (final JSR303ViolationException e) {
            return new KasperAggregateResultValidator(e);
        } catch (final ValidationException ve) {
            LOGGER.warn("No implementation found for BEAN VALIDATION - JSR 303", ve);
        }

        return new KasperAggregateResultValidator(
                new KasperResultValidator(executor.when(command, metaContext))
        );
    }

}
