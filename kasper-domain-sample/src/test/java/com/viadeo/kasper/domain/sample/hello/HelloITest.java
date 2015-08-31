// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello;

import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.DefaultKasperId;
import com.viadeo.kasper.domain.sample.hello.api.command.SendHelloToBuddyCommand;
import com.viadeo.kasper.domain.sample.hello.api.query.GetAllHelloMessagesSentToBuddyQuery;
import com.viadeo.kasper.domain.sample.hello.api.query.results.HelloMessagesResult;
import com.viadeo.kasper.platform.Platforms;
import com.viadeo.kasper.platform.builder.SpringPlatform;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class HelloITest {

    @Test
    public void create_a_platform_with_hello_domain() throws Exception {
        SpringPlatform platform = Platforms.newSpringPlatformBuilder()
                .addBundle(HelloBundle.class)
                .build();
        platform.start();

        try {
            CommandResponse commandResponse = platform.getCommandGateway().sendCommandAndWaitForAResponse(
                    new SendHelloToBuddyCommand(DefaultKasperId.random(), "welcome", "chuck"),
                    Contexts.empty()
            );

            assertNotNull(commandResponse);
            assertTrue(commandResponse.isOK());

            QueryResponse<HelloMessagesResult> queryResponse = platform.getQueryGateway().retrieve(
                    new GetAllHelloMessagesSentToBuddyQuery("chuck"),
                    Contexts.empty()
            );

            assertNotNull(queryResponse);
            assertTrue(queryResponse.isOK());
            assertTrue(queryResponse.getResult().getList().size() == 1);
            assertEquals("welcome", queryResponse.getResult().getList().iterator().next().getMessage());
        } finally {
            platform.stop();
        }

    }
}
