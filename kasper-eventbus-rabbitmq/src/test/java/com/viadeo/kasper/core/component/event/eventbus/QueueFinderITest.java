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
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.sun.jersey.api.client.ClientHandlerException;
import com.typesafe.config.Config;
import com.viadeo.kasper.spring.core.KasperConfiguration;
import com.viadeo.kasper.spring.core.KasperContextConfiguration;
import com.viadeo.kasper.spring.core.KasperIDConfiguration;
import com.viadeo.kasper.spring.core.KasperObjectMapperConfiguration;
import io.github.fallwizard.rabbitmq.mgmt.RabbitMgmtService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = {
                KasperConfiguration.class,
                KasperContextConfiguration.class,
                KasperIDConfiguration.class,
                KasperObjectMapperConfiguration.class,
                MetricRegistry.class
        }
)
public class QueueFinderITest {

    @Inject
    Config config;

    private RabbitMgmtService.Builder builder;

    @Before
    public void setUp() throws Exception {
        builder = RabbitMgmtService.builder()
                .host(config.getString("infrastructure.rabbitmq.mgmt.hostname"))
                .port(config.getInt("infrastructure.rabbitmq.mgmt.port"))
                .credentials("guest", "guest");
    }

    @Test
    public void getObsoleteQueueNames_fromRabbitMQ_usingManagementPlugin_isOk() throws Exception {
        // Given
        RabbitMgmtService rabbitMgmtService = builder.build();
        QueueFinder queueFinder = createQueueFinder(rabbitMgmtService);

        // When
        Collection<QueueInfo> obsoleteQueueNames = queueFinder.getObsoleteQueueNames();

        // Then
        assertNotNull(obsoleteQueueNames);
    }

    @Test(expected = ClientHandlerException.class)
    public void getObsoleteQueueNames_fromUnreachableRabbitMQ_throwException() throws Exception {
        // Given
        RabbitMgmtService rabbitMgmtService = builder.host("gnarf").build();

        QueueFinder queueFinder = createQueueFinder(rabbitMgmtService);

        // When
        queueFinder.getObsoleteQueueNames();

        // Then throws exception
    }

    @Test(expected = ClientHandlerException.class)
    public void getObsoleteQueueNames_withInvalidCredential_throwException() throws Exception {
        // Given
        RabbitMgmtService rabbitMgmtService = builder.host("gnarf").credentials("chuck", "michel").build();
        QueueFinder queueFinder = createQueueFinder(rabbitMgmtService);

        // When
        queueFinder.getObsoleteQueueNames();

        // Then throws exception
    }

    private QueueFinder createQueueFinder(RabbitMgmtService rabbitMgmtService) {
        return new QueueFinder(
                new AMQPComponentNameFormatter(),
                rabbitMgmtService,
                "/",
                mock(Environment.class),
                "platform-test",
                "platform-test_default_dead-letter"
        );
    }
}
