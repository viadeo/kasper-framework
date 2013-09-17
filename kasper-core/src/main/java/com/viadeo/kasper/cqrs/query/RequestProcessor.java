package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.context.Context;

public interface RequestProcessor<INPUT, OUTPUT> {
    OUTPUT process(INPUT input, Context context, RequestProcessorChain<INPUT, OUTPUT> chain) throws Exception;
}