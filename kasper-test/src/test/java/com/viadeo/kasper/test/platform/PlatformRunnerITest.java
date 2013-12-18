// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.DefaultDomainBundle;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.ddd.Domain;
import junit.framework.Assert;
import org.axonframework.eventhandling.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.test.platform.PlatformRunnerITest.*;

@RunWith(PlatformRunner.class)
@PlatformRunner.Bundles(list = {TestDomainBundleA.class, TestDomainBundleB.class})
@PlatformRunner.InfrastructureContext(configurations = {InfrastructureConfiguration.class})
public class PlatformRunnerITest {

    @Inject
    public Platform platform;

    @Inject
    public EventBus eventBus;

    @Inject
    public CommandGateway commandGateway;

    @Inject
    public QueryGateway queryGateway;

    @Inject
    public TestDomainBundleA testDomainBundleA;

    @Inject
    public TestDomainBundleB testDomainBundleB;

    @Test
    public void inject_platform_shouldBeOk() {
        Assert.assertNotNull(platform);
    }

    @Test
    public void inject_commandGateway_shouldBeOk() {
        Assert.assertNotNull(commandGateway);
    }

    @Test
    public void inject_queryGateway_shouldBeOk() {
        Assert.assertNotNull(queryGateway);
    }

    @Test
    public void inject_eventBus_shouldBeOk() {
        Assert.assertNotNull(eventBus);
    }

    @Test
    public void inject_TestDomainBundleA_shouldBeOk() {
        Assert.assertNotNull(testDomainBundleA);
    }

    @Test
    public void inject_TestDomainBundleB_shouldBeOk() {
        Assert.assertNotNull(testDomainBundleB);
    }

    public static class TestDomainBundleA extends DefaultDomainBundle {
        public TestDomainBundleA() {
            super(new Domain() {
            });
        }
    }

    public static class TestDomainBundleB extends DefaultDomainBundle {
        public TestDomainBundleB(ExecutorService executorService) {
            super(new Domain() {
            });
            checkNotNull(executorService);
        }
    }

    @Configuration
    public static class InfrastructureConfiguration {
        @Bean
        public ExecutorService worker() {
            return Executors.newFixedThreadPool(5);
        }
    }
}
