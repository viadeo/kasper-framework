// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandBus;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.mongodb.util.MyAsserts.assertNotNull;
import static com.mongodb.util.MyAsserts.assertTrue;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

public class PlatformConfigurationTest {

    @Test
    public void testDefaultPlatformConfiguration() throws Exception {
        this.testPlatformConfiguration(new DefaultPlatformConfiguration());
    }

    @Test
    public void testDefaultSpringPlatformConfiguration() throws Exception {
        this.testPlatformConfiguration(new DefaultPlatformSpringConfiguration());
    }

    // ------------------------------------------------------------------------

    /* FIXME: add tests for resolvers */
    @SuppressWarnings("unused")
    private void testPlatformConfiguration(final PlatformConfiguration pf) throws Exception {


        final DomainResolver domainResolver =
                this.testCachedComponent(pf, "domainResolver");

        final CommandHandlerResolver commandHandlerResolver =
                this.testCachedComponent(pf, "commandHandlerResolver", domainResolver);

        final EventResolver eventResolver =
                this.testCachedComponent(pf, "eventResolver", domainResolver);

        final EventListenerResolver eventListenerResolver =
                this.testCachedComponent(pf, "eventListenerResolver", domainResolver);

        final QueryHandlerResolver queryHandlerResolver =
                this.testCachedComponent(pf, "queryHandlerResolver", domainResolver);

        final QueryHandlersLocator queryHandlersLocator=
                this.testCachedComponent(pf, "queryHandlersLocator", queryHandlerResolver);

        final QueryResolver queryResolver =
                this.testCachedComponent(pf, "queryResolver", domainResolver, queryHandlerResolver, queryHandlersLocator);

        final QueryResultResolver queryResultResolver =
                this.testCachedComponent(pf, "queryResultResolver", domainResolver, queryHandlerResolver, queryHandlersLocator);
        
        final ConceptResolver conceptResolver =
                this.testCachedComponent(pf, "conceptResolver", domainResolver);

        final RelationResolver relationResolver =
                this.testCachedComponent(pf, "relationResolver", domainResolver, conceptResolver);

        final EntityResolver entityResolver =
                this.testCachedComponent(pf, "entityResolver", conceptResolver, relationResolver, domainResolver);

        final RepositoryResolver repositoryResolver =
                this.testCachedComponent(pf, "repositoryResolver", entityResolver, domainResolver);

        final DomainLocator domainLocator =
                this.testCachedComponent(pf, "domainLocator", commandHandlerResolver, repositoryResolver);

        final CommandResolver commandResolver =
                this.testCachedComponent(pf, "commandResolver", domainLocator, domainResolver, commandHandlerResolver);

        final ResolverFactory resolverFactory =
                this.testCachedComponent(pf, "resolverFactory",
                        domainResolver,
                        commandResolver,
                        commandHandlerResolver,
                        eventListenerResolver,
                        queryResolver,
                        queryResultResolver,
                        queryHandlerResolver,
                        repositoryResolver,
                        entityResolver,
                        conceptResolver,
                        relationResolver,
                        eventResolver);

        final CommandBus commandBus =
                this.testCachedComponent(pf, "commandBus");

        final CommandGateway commandGateway =
                this.testCachedComponent(pf, "commandGateway", commandBus);

        final QueryGateway queryGateway =
                this.testCachedComponent(pf, "queryGateway", queryHandlersLocator);

        final KasperEventBus eventBus =
                this.testCachedComponent(pf, "eventBus");

        final ComponentsInstanceManager componentsInstanceManager =
                this.testCachedComponent(pf, "getComponentsInstanceManager");

        final AnnotationRootProcessor annotationRootProcessor =
                this.testCachedComponent(pf, "annotationRootProcessor", componentsInstanceManager);

        final CommandHandlersProcessor commandHandlersProcessor =
                this.testCachedComponent(pf, "commandHandlersProcessor", commandBus, domainLocator, eventBus, commandHandlerResolver);

        final DomainsProcessor domainsProcessor =
                this.testCachedComponent(pf, "domainsProcessor", domainLocator);

        final EventListenersProcessor eventListenersProcessor =
                this.testCachedComponent(pf, "eventListenersProcessor", eventBus, commandGateway);

        final QueryHandlersProcessor queryHandlersProcessor =
                this.testCachedComponent(pf, "queryHandlersProcessor", queryHandlersLocator);

        final RepositoriesProcessor repositoriesProcessor=
                this.testCachedComponent(pf, "repositoriesProcessor", domainLocator, eventBus);

        final QueryHandlerFiltersProcessor queryHandlerFiltersProcessor =
                this.testCachedComponent(pf, "queryHandlerFiltersProcessor", queryHandlersLocator);

        final Platform platform =
                this.testCachedComponent(pf, "kasperPlatform",
                                  commandGateway, queryGateway,
                                  eventBus, annotationRootProcessor);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private <T> T testCachedComponent(final PlatformConfiguration pf,
                                      final String confName,
                                      final Object... parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method methodWithParameters = null;
        Method methodEmpty = null;

        final Method[] methods = pf.getClass().getMethods();
        for (final Method method : methods) {
            if (method.getName().contentEquals(confName)) {
                if (0 == method.getParameterTypes().length) {
                    methodEmpty = method;
                } else {
                    methodWithParameters = method;
                }
            }
        }

        System.out.println(confName);
        assertNotNull(methodEmpty);

        if (null == methodWithParameters) {
            final Object ret1 = methodEmpty.invoke(pf);
            final Object ret2 = methodEmpty.invoke(pf);
            assertSame(ret1, ret2);
            return (T) ret1;
        } else {
            try {
                methodEmpty.invoke(pf);
                fail();
            } catch (final InvocationTargetException e) {
                assertTrue(KasperException.class.equals(e.getTargetException().getClass()));
            }

            final Object ret1 = methodWithParameters.invoke(pf, parameters);
            final Object ret2 = methodEmpty.invoke(pf);

            assertSame(ret1, ret2);

            try {
                methodWithParameters.invoke(pf, parameters);
                fail();
            } catch (final InvocationTargetException e) {
                assertTrue(KasperException.class.equals(e.getTargetException().getClass()));
            }

            return (T) ret1;
        }
    }

    private String up(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

}
