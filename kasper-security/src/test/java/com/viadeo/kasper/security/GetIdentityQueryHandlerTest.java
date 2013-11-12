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


public class GetIdentityQueryHandlerTest {

    static final Logger LOGGER = LoggerFactory.getLogger(GetIdentityQueryHandlerTest.class);

    @Test
    public void platformShouldBeBuiltByFactoryWithoutError() throws Exception {
       final PlatformFactory factory = new PlatformFactory();
       Platform platform = factory.getPlatform();
       platform.boot();
       QueryGateway queryGateAway = platform.getQueryGateway();
       Context context = new DefaultContext();
       context.setSecurityToken("0031");
       QueryResponse<IdentityResult> response = queryGateAway.retrieve(new GetIdentityQuery(), context);
       LOGGER.info("response : "+response.getResult().getIdentity());
    }



}
