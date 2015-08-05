// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.builder;

import com.viadeo.kasper.platform.ExtraComponent;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static com.google.common.base.Preconditions.checkNotNull;

public final class BuilderContextHelper {

    private BuilderContextHelper() { }

    public static ApplicationContext createApplicationContextFrom(final BuilderContext builderContext) {
        checkNotNull(builderContext);

        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        beanFactory.registerSingleton(
                builderContext.getConfiguration().getClass().getSimpleName(),
                builderContext.getConfiguration()
        );

        beanFactory.registerSingleton(
                builderContext.getEventBus().getClass().getSimpleName(),
                builderContext.getEventBus()
        );

        beanFactory.registerSingleton(
                builderContext.getCommandGateway().getClass().getSimpleName(),
                builderContext.getCommandGateway()
        );

        beanFactory.registerSingleton(
                builderContext.getQueryGateway().getClass().getSimpleName(),
                builderContext.getQueryGateway()
        );

        beanFactory.registerSingleton(
                builderContext.getMetricRegistry().getClass().getSimpleName(),
                builderContext.getMetricRegistry()
        );

        for (final ExtraComponent extraComponent : builderContext.getExtraComponents()) {
            beanFactory.registerSingleton(extraComponent.getKey().getName(), extraComponent.getInstance());
        }

        applicationContext.refresh();

        return applicationContext;
    }

}
