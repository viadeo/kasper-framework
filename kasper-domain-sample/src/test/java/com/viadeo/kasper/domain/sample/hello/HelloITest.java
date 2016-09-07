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
package com.viadeo.kasper.domain.sample.hello;

import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.DefaultKasperId;
import com.viadeo.kasper.domain.sample.hello.api.command.SendHelloToBuddyCommand;
import com.viadeo.kasper.domain.sample.hello.api.query.GetAllHelloMessagesSentToBuddyQuery;
import com.viadeo.kasper.domain.sample.hello.api.query.results.HelloMessagesResult;
import com.viadeo.kasper.spring.platform.Platforms;
import com.viadeo.kasper.spring.platform.SpringPlatform;
import org.junit.Test;

import static org.junit.Assert.*;

public class HelloITest {

    @Test
    public void create_a_platform_with_hello_domain() throws Exception {
        SpringPlatform platform = Platforms.newSpringPlatformBuilder()
                .addBundle(HelloBundle.class)
                .build()
                .start();

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
