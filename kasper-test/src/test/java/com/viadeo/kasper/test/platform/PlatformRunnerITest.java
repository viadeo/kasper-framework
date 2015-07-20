// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.bundle.DefaultDomainBundle;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.api.component.Domain;
import org.axonframework.eventhandling.EventBus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.inject.Inject;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.test.platform.PlatformRunnerITest.*;
import static org.junit.Assert.assertNotNull;

@RunWith(PlatformRunner.class)
@PlatformRunner.Bundles({TestDomainBundleA.class, TestDomainBundleB.class})
@PlatformRunner.InfrastructureContext(
        configurations = {InfrastructureConfiguration.class},
        activeProfiles = {"hero"}
)
public class PlatformRunnerITest {

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
    @Profile("hero")
    public static class InfrastructureConfiguration {
        @Bean
        public ExecutorService worker() {
            return Executors.newFixedThreadPool(5);
        }
    }

    // ------------------------------------------------------------------------

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

    @Inject
    public ExecutorService executorService;

    // ------------------------------------------------------------------------

    @Test
    public void inject_platform_shouldBeOk() {
        assertNotNull(platform);
    }

    @Test
    public void inject_commandGateway_shouldBeOk() {
        assertNotNull(commandGateway);
    }

    @Test
    public void inject_queryGateway_shouldBeOk() {
        assertNotNull(queryGateway);
    }

    @Test
    public void inject_eventBus_shouldBeOk() {
        assertNotNull(eventBus);
    }

    @Test
    public void inject_TestDomainBundleA_shouldBeOk() {
        assertNotNull(testDomainBundleA);
    }

    @Test
    public void inject_TestDomainBundleB_shouldBeOk() {
        assertNotNull(testDomainBundleB);
    }

    @Test
    public void inject_ExecutorService_shouldBeOk() {
        assertNotNull(executorService);
    }
}
