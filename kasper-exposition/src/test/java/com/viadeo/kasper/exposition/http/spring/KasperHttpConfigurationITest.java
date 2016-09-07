// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
import com.viadeo.kasper.spring.platform.Platforms;
import com.viadeo.kasper.spring.platform.SpringPlatform;
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
