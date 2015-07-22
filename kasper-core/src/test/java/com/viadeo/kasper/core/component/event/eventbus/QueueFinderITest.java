package com.viadeo.kasper.core.component.event.eventbus;

import com.sun.jersey.api.client.ClientHandlerException;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.event.eventbus.AMQPComponentNameFormatter;
import com.viadeo.kasper.core.component.event.eventbus.QueueFinder;
import com.viadeo.kasper.core.component.event.eventbus.QueueInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import rabbitmq.mgmt.RabbitMgmtService;

import javax.inject.Inject;
import java.util.Collection;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        classes = AMQPClusterITest.TestConfiguration.class
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