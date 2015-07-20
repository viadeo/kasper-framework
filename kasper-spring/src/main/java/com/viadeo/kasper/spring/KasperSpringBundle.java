// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.bundle.DefaultDomainBundle;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.annotation.XKasperCommandHandler;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.annotation.XKasperRepository;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.EventListener;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

public class KasperSpringBundle extends DefaultDomainBundle {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperSpringBundle.class);

    private final AnnotationConfigApplicationContext queryContext;

    private final AnnotationConfigApplicationContext commandContext;

    private final long initTimeInMillis;

    // ------------------------------------------------------------------------

    /**
     * A custom type scanner that check both path and annotation
     */
    static class AnnotationAndPathFilter implements TypeFilter {

        private final Class<? extends Annotation> annotation;
        private final List<String> paths;

        public AnnotationAndPathFilter(final Class<? extends Annotation> annotation, final List<String> paths) {
            this.annotation = annotation;
            this.paths = paths;
        }

        @Override
        public boolean match(final MetadataReader metadataReader, final MetadataReaderFactory metadataReaderFactory) throws IOException {

            if ( ! metadataReader.getAnnotationMetadata().isAnnotated(annotation.getName())) {
                return false;
            }

            for (final String path : paths) {
                if (metadataReader.getClassMetadata().getClassName().startsWith(path)) {
                    return true;
                }
            }

            return false;
        }
    }

    // ------------------------------------------------------------------------

    /**
     * Spring domain bundle allow you to manage platform stereotypes
     * without the need to create boiler plate injection code.
     *
     * It comes with the price of coupling to the jsr 330, which
     * is not really high and risky
     *
     * @param domain             domain name
     * @param applicationContext parent application context
     */
    public KasperSpringBundle(final Domain domain, final ApplicationContext applicationContext) {
        super(domain);

        final long startInitTimeInMillis = System.currentTimeMillis();

        int total = 0;

        commandContext = createChildContext(applicationContext);
        final ClassPathBeanDefinitionScanner commandScanner = new ClassPathBeanDefinitionScanner(commandContext, false);
        commandScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperCommandHandler.class, makePaths("command")));
        commandScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperRepository.class, makePaths("command")));
        commandScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperEventListener.class, makePaths("command")));
        commandScanner.addIncludeFilter(new AnnotationAndPathFilter(Configuration.class, makePaths("command.spring", "common.spring")));
        total += commandScanner.scan(getClass().getPackage().getName());

        queryContext = createChildContext(applicationContext);
        final ClassPathBeanDefinitionScanner queryScanner = new ClassPathBeanDefinitionScanner(queryContext, false);
        queryScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperQueryHandler.class, makePaths("query")));
        queryScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperEventListener.class, makePaths("query")));
        queryScanner.addIncludeFilter(new AnnotationAndPathFilter(Configuration.class, makePaths("query.spring", "common.spring")));
        total += queryScanner.scan(getClass().getPackage().getName());

        if (total == 0) {
            LOGGER.warn(String.format("Bundle %s registered nothing in context, this is probably an error in the package layout",getClass().getName()));
        }

        initTimeInMillis = System.currentTimeMillis() - startInitTimeInMillis;
    }

    /**
     * Build filter path relatives to this bundle
     *
     * @param paths the path to append (ex stereotype package)
     * @return a list of path to filter on
     */
    private List<String> makePaths(final String... paths) {

        final List<String> out = Lists.newArrayList();
        for (final String path : paths) {
            out.add(String.format("%s.%s", getClass().getPackage().getName(), path));
        }

        return out;
    }

    /**
     * Create a child context for the infrastructure
     *
     * @param parent infrastructure application context
     * @return child application context
     */
    private AnnotationConfigApplicationContext createChildContext(final ApplicationContext parent) {
        final AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext();
        child.setParent(parent);
        child.getEnvironment().setActiveProfiles(parent.getEnvironment().getActiveProfiles());

        final PropertySourcesPlaceholderConfigurer singletonObject = new PropertySourcesPlaceholderConfigurer();
        singletonObject.setEnvironment(parent.getEnvironment());
        child.getBeanFactory().registerSingleton("bundlePropertySourcePlaceholderConfigurer", singletonObject);

        return child;
    }

    /**
     * Configure is called during parent context setup.
     * it's here for backward compatibility with kasper.
     *
     * @param context platform context
     */
    @Override
    public void configure(final Platform.BuilderContext context) {
        long startRefreshTimeInMillis = System.currentTimeMillis();

        commandContext.refresh();
        commandHandlers.addAll(commandContext.getBeansOfType(CommandHandler.class).values());
        repositories.addAll(commandContext.getBeansOfType(Repository.class).values());
        eventListeners.addAll(commandContext.getBeansOfType(EventListener.class).values());
        commandInterceptorFactories.addAll(commandContext.getBeansOfType(CommandInterceptorFactory.class).values());

        queryContext.refresh();
        eventListeners.addAll(queryContext.getBeansOfType(EventListener.class).values());
        queryHandlers.addAll(queryContext.getBeansOfType(QueryHandler.class).values());
        queryInterceptorFactories.addAll(queryContext.getBeansOfType(QueryInterceptorFactory.class).values());

        final long refreshTimeInMillis = System.currentTimeMillis() - startRefreshTimeInMillis;

        LOGGER.info("{} loaded in {} ms", getName(), (initTimeInMillis + refreshTimeInMillis));
    }

    @VisibleForTesting
    public ApplicationContext getCommandContext() {
        return commandContext;
    }

    @VisibleForTesting
    public ApplicationContext getQueryContext() {
        return queryContext;
    }

}
