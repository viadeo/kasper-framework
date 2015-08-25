// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.ContextHelper;
import com.viadeo.kasper.common.exposition.HttpContextHeaders;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

public class HttpContextWithVersionDeserializer implements HttpContextDeserializer {

    private ContextHelper contextHelper;

    public HttpContextWithVersionDeserializer(ContextHelper contextHelper) {
        this.contextHelper = contextHelper;
    }

    @Override
    public Context deserialize(HttpServletRequest req, UUID kasperCorrelationId) {
        HashMap<String,String> map = Maps.newHashMap();
        map.put(Context.KASPER_CID_SHORTNAME, kasperCorrelationId.toString());

        for (String headerName : Collections.list(req.getHeaderNames())) {
            Optional<HttpContextHeaders> httpContextHeader = HttpContextHeaders.fromHeader(headerName);
            if (httpContextHeader.isPresent()) {
                map.put(httpContextHeader.get().toPropertyKey(), req.getHeader(headerName));
            } else {
                map.put(headerName, req.getHeader(headerName));
            }
        }

        return contextHelper.createFrom(map);
    }
}
