package com.viadeo.kasper.test.platform;

import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.command.interceptor.CommandValidationInterceptor;
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
        return this.when(command, DefaultContextBuilder.get());
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

        return new KasperAggregateResultValidator(executor.when(command, metaContext));
    }

}
