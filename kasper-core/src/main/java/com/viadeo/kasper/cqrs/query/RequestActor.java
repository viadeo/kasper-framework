package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.context.Context;

public interface RequestActor<INPUT, OUTPUT> {
    OUTPUT process(INPUT input, Context context, RequestActorChain<INPUT, OUTPUT> chain) throws Exception;
}