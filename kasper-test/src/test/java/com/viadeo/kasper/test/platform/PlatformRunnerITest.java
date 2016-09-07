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
package com.viadeo.kasper.test.platform;

import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.bundle.DefaultDomainBundle;
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
            super(new Domain() { }, "bundleA");
        }
    }

    public static class TestDomainBundleB extends DefaultDomainBundle {
        public TestDomainBundleB(ExecutorService executorService) {
            super(new Domain() { }, "bundleB");
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
