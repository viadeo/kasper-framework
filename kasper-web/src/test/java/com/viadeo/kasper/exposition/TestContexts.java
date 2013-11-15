// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.impl.StringKasperId;

import java.util.UUID;

public class TestContexts {

    public static final String CONTEXT_FULL = "full";
    public static final DefaultContext context_full = new DefaultContext();
    {
        context_full.setSessionCorrelationId(UUID.randomUUID().toString());
        context_full.setFunnelCorrelationId(UUID.randomUUID().toString());
        context_full.setRequestCorrelationId(UUID.randomUUID().toString());
        context_full.setUserId("42");
        context_full.setUserLang("us");
        context_full.setUserCountry("US");
        context_full.setApplicationId("TEST");
        context_full.setSecurityToken(UUID.randomUUID().toString());
        context_full.setFunnelName("MyFunnel");
        context_full.setFunnelVersion("case_1");
    };

}
