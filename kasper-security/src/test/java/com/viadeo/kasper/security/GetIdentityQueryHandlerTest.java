// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security;

import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.configuration.PlatformFactory;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContext;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.security.query.GetIdentityQuery;
import com.viadeo.kasper.security.query.results.IdentityResult;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;


public class GetIdentityQueryHandlerTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetIdentityQueryHandlerTest.class);

    @Test
    public void platformShouldBeBuiltByFactoryWithoutError() throws Exception {
// Given
       final PlatformFactory factory = new PlatformFactory();
       final Platform platform = factory.getPlatform();
       final Context context = new DefaultContext();
       context.setSecurityToken("0031");
       platform.boot();
// When
       final QueryGateway queryGateAway = platform.getQueryGateway();
       QueryResponse<IdentityResult> response = queryGateAway.retrieve(new GetIdentityQuery(), context);
// Then ... to be completed
        LOGGER.info("response : "+response.getResult().getIdentity());
       assertEquals(1, response.getResult().getIdentity());
    }



}
