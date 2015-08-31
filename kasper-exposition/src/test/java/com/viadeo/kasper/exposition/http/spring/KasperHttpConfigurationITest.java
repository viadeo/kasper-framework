// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.spring;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.viadeo.kasper.domain.sample.hello.HelloBundle;
import com.viadeo.kasper.platform.Platforms;
import com.viadeo.kasper.platform.builder.SpringPlatform;
import org.junit.After;
import org.junit.Test;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class KasperHttpConfigurationITest {

    private SpringPlatform platform;

    @After
    public void tearDown() throws Exception {
        if (platform != null) {
            platform.stop();
        }
    }

    @Test
    public void new_spring_platform_with_exposition_is_ready_to_use() {
        platform = Platforms.newSpringPlatformBuilder()
                .add(KasperHttpConfiguration.class)
                .build()
                .start();

        ClientResponse response = Client.create()
                .resource("http://localhost:8080/")
                .get(ClientResponse.class);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void new_spring_platform_with_bundle_declaring_a_command_must_be_exposed() {
        platform = Platforms.newSpringPlatformBuilder()
                .add(KasperHttpConfiguration.class)
                .addBundle(HelloBundle.class)
                .build()
                .start();

        String data = "{\"idToUse\":\"1b758705-f828-419d-863e-4802ca01d73a\", \"message\":\"Hello\", \"forBuddy\":\"Chuck\"}";

        ClientResponse response = Client.create()
                .resource("http://localhost:8080/kasper/command/SendHelloToBuddy")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, data);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void new_spring_platform_with_bundle_declaring_a_query_must_be_exposed() {
        platform = Platforms.newSpringPlatformBuilder()
                .add(KasperHttpConfiguration.class)
                .addBundle(HelloBundle.class)
                .build()
                .start();

        String data = "{\"forBuddy\":\"Chuck\"}";

        ClientResponse response = Client.create()
                .resource("http://localhost:8080/kasper/query/GetAllHelloMessagesSentToBuddy")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, data);

        assertNotNull(response);
        assertEquals(200, response.getStatus());
    }

    @Test
    public void new_spring_platform_with_declared_event_must_be_exposed() {
        platform = Platforms.newSpringPlatformBuilder()
                .add(KasperHttpConfiguration.class)
                .addBundle(HelloBundle.class)
                .build()
                .start();

        String data = "{\"entityId\":\"1b758705-f828-419d-863e-4802ca01d73a\", \"message\":\"Hello\", \"forBuddy\":\"Chuck\"}";

        ClientResponse response = Client.create()
                .resource("http://localhost:8080/kasper/event/HelloCreated")
                .type(MediaType.APPLICATION_JSON_TYPE)
                .post(ClientResponse.class, data);

        assertNotNull(response);
        assertEquals(202, response.getStatus());
    }
}
