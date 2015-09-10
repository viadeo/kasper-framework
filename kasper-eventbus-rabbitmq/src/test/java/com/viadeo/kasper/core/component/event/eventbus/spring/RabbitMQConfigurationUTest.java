package com.viadeo.kasper.core.component.event.eventbus.spring;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RabbitMQConfigurationUTest {

    private RabbitMQConfiguration configuration;

    @Before
    public void setup () {
        configuration = new RabbitMQConfiguration();
    }

    @Test
    public void getAddresses_withSingleHost_isOk() {
        // Given
        Config config = ConfigFactory.parseMap(
                ImmutableMap.<String, Object>builder()
                        .put("infrastructure.rabbitmq.hosts", "miaou")
                        .put("infrastructure.rabbitmq.port", "5672")
                        .build()
        );

        // When
        String addresses = configuration.getAddresses(config);

        // Then
        Assert.assertNotNull(addresses);
        Assert.assertEquals("miaou:5672", addresses);
    }

    @Test
    public void getAddresses_withMultiHosts_isOk() {
        // Given
        Config config = ConfigFactory.parseMap(
                ImmutableMap.<String, Object>builder()
                        .put("infrastructure.rabbitmq.hosts", "miaou1, miaou2")
                        .put("infrastructure.rabbitmq.port", "5672")
                        .build()
        );

        // When
        String addresses = configuration.getAddresses(config);

        // Then
        Assert.assertNotNull(addresses);
        Assert.assertEquals("miaou1:5672,miaou2:5672", addresses);
    }
}