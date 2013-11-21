package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.cqrs.command.Command;
import org.axonframework.test.TestExecutor;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperAggregateExecutor implements KasperFixtureCommandExecutor<KasperAggregateResultValidator> {

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
        final Map<String, Object> metaContext = new HashMap<String, Object>() {{
            this.put(Context.METANAME, context);
        }};
        return new KasperAggregateResultValidator(executor.when(command, metaContext));
    }

}
