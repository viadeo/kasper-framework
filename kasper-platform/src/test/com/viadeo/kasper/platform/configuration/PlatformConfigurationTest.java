// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.configuration;

import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.platform.Platform;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.eventhandling.EventBus;
import org.junit.Test;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

public class PlatformConfigurationTest {

    private void testPlatformConfiguration(final PlatformConfiguration platformConfiguration) throws Exception {

        final CommandBus commandBus = platformConfiguration.commandBus();
        assertSame(commandBus, platformConfiguration.commandBus());

        // --------------------------------------------------------------------

        try {
            platformConfiguration.commandGatewayFactoryBean();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final CommandGatewayFactoryBean cmdGtwFactoryBean = platformConfiguration.commandGatewayFactoryBean(commandBus);
        /* Required by Axon automagic Gateway generation, for this test, as we are not in a real Spring context */
        cmdGtwFactoryBean.afterPropertiesSet();
        assertSame(cmdGtwFactoryBean, platformConfiguration.commandGatewayFactoryBean());

        try {
            platformConfiguration.commandGatewayFactoryBean(commandBus);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        try {
            /* Spring will initialize this with a BeanPostProcessor */
            if (!DefaultPlatformSpringConfiguration.class.isAssignableFrom(platformConfiguration.getClass())) {
                cmdGtwFactoryBean.afterPropertiesSet();
            }
        } catch (final Exception e) {
            throw new KasperException("Unable to bind the gateway", e);
        }

        // --------------------------------------------------------------------

        try {
            platformConfiguration.commandGateway();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final CommandGateway commandGateway = platformConfiguration.commandGateway(cmdGtwFactoryBean);
        assertSame(commandGateway, platformConfiguration.commandGateway());

        try {
            platformConfiguration.commandGateway(cmdGtwFactoryBean);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        // --------------------------------------------------------------------

        final QueryServicesLocator queryServicesLocator = platformConfiguration.queryServicesLocator();
        assertSame(queryServicesLocator, platformConfiguration.queryServicesLocator());

        // --------------------------------------------------------------------

        try {
            platformConfiguration.queryGateway();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final QueryGateway queryGateway = platformConfiguration.queryGateway(queryServicesLocator);
        assertSame(queryGateway, platformConfiguration.queryGateway());

        try {
            platformConfiguration.queryGateway(queryServicesLocator);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        // --------------------------------------------------------------------

        final EventBus eventBus = platformConfiguration.eventBus();
        assertSame(eventBus, platformConfiguration.eventBus());

        // --------------------------------------------------------------------

        final ComponentsInstanceManager componentsInstanceManager = platformConfiguration.getComponentsInstanceManager();
        assertSame(componentsInstanceManager, platformConfiguration.getComponentsInstanceManager());

        // --------------------------------------------------------------------

        try {
            platformConfiguration.annotationRootProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final AnnotationRootProcessor annotationRootProcessor = platformConfiguration.annotationRootProcessor(componentsInstanceManager);
        assertSame(annotationRootProcessor, platformConfiguration.annotationRootProcessor());

        try {
            platformConfiguration.annotationRootProcessor(componentsInstanceManager);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        // --------------------------------------------------------------------

        final DomainLocator domainLocator = platformConfiguration.domainLocator();
        assertSame(domainLocator, platformConfiguration.domainLocator());

        // --------------------------------------------------------------------

        try {
            platformConfiguration.commandHandlersProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final CommandHandlersProcessor commandHandlersProcessor = platformConfiguration.commandHandlersProcessor(commandBus, domainLocator);
        assertSame(commandHandlersProcessor, platformConfiguration.commandHandlersProcessor());

        try {
            platformConfiguration.commandHandlersProcessor(commandBus, domainLocator);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        // --------------------------------------------------------------------

        try {
            platformConfiguration.domainsProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final DomainsProcessor domainsProcessor = platformConfiguration.domainsProcessor(domainLocator);
        assertSame(domainsProcessor, platformConfiguration.domainsProcessor());

        try {
            platformConfiguration.domainsProcessor(domainLocator);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        // --------------------------------------------------------------------

        try {
            platformConfiguration.eventListenersProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final EventListenersProcessor eventListenersProcessor = platformConfiguration.eventListenersProcessor(eventBus);
        assertSame(eventListenersProcessor, platformConfiguration.eventListenersProcessor());

        try {
            platformConfiguration.eventListenersProcessor(eventBus);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        // --------------------------------------------------------------------

        try {
            platformConfiguration.queryServicesProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final QueryServicesProcessor queryServicesProcessor = platformConfiguration.queryServicesProcessor(queryServicesLocator);
        assertSame(queryServicesProcessor, platformConfiguration.queryServicesProcessor());

        try {
            platformConfiguration.queryServicesProcessor(queryServicesLocator);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        // --------------------------------------------------------------------

        try {
            platformConfiguration.repositoriesProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final RepositoriesProcessor repositoriesProcessor = platformConfiguration.repositoriesProcessor(domainLocator, eventBus);
        assertSame(repositoriesProcessor, platformConfiguration.repositoriesProcessor());

        try {
            platformConfiguration.repositoriesProcessor(domainLocator, eventBus);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        // --------------------------------------------------------------------

        try {
            platformConfiguration.serviceFiltersProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final ServiceFiltersProcessor serviceFiltersProcessor = platformConfiguration.serviceFiltersProcessor(queryServicesLocator);
        assertSame(serviceFiltersProcessor, platformConfiguration.serviceFiltersProcessor());

        try {
            platformConfiguration.serviceFiltersProcessor(queryServicesLocator);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        // --------------------------------------------------------------------

        try {
            platformConfiguration.kasperPlatform();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final Platform platform = platformConfiguration.kasperPlatform(commandGateway, queryGateway, eventBus, annotationRootProcessor);
        assertSame(platform, platformConfiguration.kasperPlatform());

        try {
            platformConfiguration.kasperPlatform(commandGateway, queryGateway, eventBus, annotationRootProcessor);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

    }

    // ------------------------------------------------------------------------

    @Test
    public void testDefaultPlatformConfiguration() throws Exception {
        this.testPlatformConfiguration(new DefaultPlatformConfiguration());
    }

    @Test
    public void testDefaultSpringPlatformConfiguration() throws Exception {
        this.testPlatformConfiguration(new DefaultPlatformSpringConfiguration());
    }

}
