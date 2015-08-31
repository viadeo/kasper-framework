package com.viadeo.kasper.platform.bundle;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.annotation.*;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.platform.builder.BuilderContext;
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

public class SpringBundle extends DefaultDomainBundle {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBundle.class);

    private final AnnotationConfigApplicationContext queryContext;

    private final AnnotationConfigApplicationContext commandContext;

    private final long initTimeInMillis;


    /**
     * A custom type scanner that check both path and annotation
     */
    static class AnnotationAndPathFilter implements TypeFilter {

        private final Class<? extends Annotation> annotation;
        private final List<String> paths;

        public AnnotationAndPathFilter(Class<? extends Annotation> annotation, List<String> paths) {
            this.annotation = annotation;
            this.paths = paths;
        }

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {

            if (! metadataReader.getAnnotationMetadata().isAnnotated(annotation.getName())) {
                return false;
            }

            for (String path : paths) {
                if (metadataReader.getClassMetadata().getClassName().startsWith(path)) {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Spring domain bundle allow you to manage platform stereotypes
     * without the need to create boiler plate injection code.
     * <p>
     * It comes with the price of coupling to the jsr 330, which
     * is not really high and risky
     * </p>
     *
     * @param domain             domain name
     * @param applicationContext parent application context
     */
    public SpringBundle(final Domain domain, ApplicationContext applicationContext) {
        super(domain);

        long startInitTimeInMillis = System.currentTimeMillis();

        int total = 0;
        commandContext = createChildContext(applicationContext);
        ClassPathBeanDefinitionScanner commandScanner = new ClassPathBeanDefinitionScanner(commandContext, false);
        commandScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperCommandHandler.class, makePaths("command")));
        commandScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperRepository.class, makePaths("command")));
        commandScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperEventListener.class, makePaths("command")));
        commandScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperSaga.class, makePaths("command")));
        commandScanner.addIncludeFilter(new AnnotationAndPathFilter(Configuration.class, makePaths("command.spring", "common.spring")));
        commandScanner.addExcludeFilter(new AnnotationAndPathFilter(XKasperUnregistered.class, makePaths("command")));
        total += commandScanner.scan(getClass().getPackage().getName());

        queryContext = createChildContext(applicationContext);
        ClassPathBeanDefinitionScanner queryScanner = new ClassPathBeanDefinitionScanner(queryContext, false);
        queryScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperQueryHandler.class, makePaths("query")));
        queryScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperEventListener.class, makePaths("query")));
        queryScanner.addIncludeFilter(new AnnotationAndPathFilter(XKasperSaga.class, makePaths("command")));
        queryScanner.addIncludeFilter(new AnnotationAndPathFilter(Configuration.class, makePaths("query.spring", "common.spring")));
        queryScanner.addExcludeFilter(new AnnotationAndPathFilter(XKasperUnregistered.class, makePaths("query")));
        total += queryScanner.scan(getClass().getPackage().getName());

        Preconditions.checkState(total > 0, "Bundle %s registered nothing in context, this is probably an error in the package layout", getClass().getName());

        initTimeInMillis = System.currentTimeMillis() - startInitTimeInMillis;
    }

    /**
     * Build filter path relatives to this bundle
     *
     * @param paths the path to append (ex stereotype package)
     * @return a list of path to filter on
     */
    private List<String> makePaths(String... paths) {

        List<String> out = Lists.newArrayList();
        for (String path : paths) {
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
    private AnnotationConfigApplicationContext createChildContext(ApplicationContext parent) {
        AnnotationConfigApplicationContext child = new AnnotationConfigApplicationContext();
        child.setParent(parent);
        child.getEnvironment().setActiveProfiles(parent.getEnvironment().getActiveProfiles());
        PropertySourcesPlaceholderConfigurer singletonObject = new PropertySourcesPlaceholderConfigurer();
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
    public void configure(BuilderContext context) {
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

        long refreshTimeInMillis = System.currentTimeMillis() - startRefreshTimeInMillis;

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
