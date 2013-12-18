// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.utils;

import com.viadeo.kasper.client.platform.Platform;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.client.platform.Platform.*;

public final class BuilderContextHelper {

    private BuilderContextHelper() {
    }

    public static ApplicationContext createApplicationContextFrom(final BuilderContext builderContext) {
        checkNotNull(builderContext);

        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

        final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        beanFactory.registerSingleton(builderContext.getConfiguration().getClass().getSimpleName(), builderContext.getConfiguration());
        beanFactory.registerSingleton(builderContext.getEventBus().getClass().getSimpleName(), builderContext.getEventBus());
        beanFactory.registerSingleton(builderContext.getCommandGateway().getClass().getSimpleName(), builderContext.getCommandGateway());
        beanFactory.registerSingleton(builderContext.getQueryGateway().getClass().getSimpleName(), builderContext.getQueryGateway());
        beanFactory.registerSingleton(builderContext.getMetricRegistry().getClass().getSimpleName(), builderContext.getMetricRegistry());

        for (final Map.Entry<Platform.ExtraComponentKey, Object> entry : builderContext.getExtraComponents().entrySet()) {
            beanFactory.registerSingleton(entry.getKey().getName(), entry.getValue());
        }

        applicationContext.refresh();

        return applicationContext;
    }
}
