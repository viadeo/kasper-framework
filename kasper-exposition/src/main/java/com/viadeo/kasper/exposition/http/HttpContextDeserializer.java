// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.viadeo.kasper.api.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

public interface HttpContextDeserializer {

    Context deserialize(final HttpServletRequest req, final UUID kasperCorrelationId);

}
