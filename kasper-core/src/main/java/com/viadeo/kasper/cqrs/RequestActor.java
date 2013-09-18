// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs;

import com.viadeo.kasper.context.Context;

public interface RequestActor<INPUT, OUTPUT> {

    OUTPUT process(INPUT input, Context context, RequestActorsChain<INPUT, OUTPUT> chain) throws Exception;

}
