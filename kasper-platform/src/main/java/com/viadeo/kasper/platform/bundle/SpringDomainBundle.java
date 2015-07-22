// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.bundle;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.configuration.TypeSafeConfigPropertyPlaceholder;
import com.viadeo.kasper.platform.utils.BuilderContextHelper;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class SpringDomainBundle extends DefaultDomainBundle {

    protected final AnnotationConfigApplicationContext applicationContext;

    public static class BeanDescriptor {

        private final Class clazz;
        private final Object object;
        private final Optional<String> name;

        public BeanDescriptor(final Object object) {
            this(object.getClass(), checkNotNull(object));
        }

        public BeanDescriptor(final Class clazz, final Object object) {
            this(Optional.<String>absent(), checkNotNull(clazz), checkNotNull(object));
        }

        public BeanDescriptor(final String name, final Object object) {
            this(Optional.of(checkNotNull(name)), checkNotNull(object).getClass(), object);
        }

        protected BeanDescriptor(final Optional<String> name, final Class clazz, final Object object) {
            this.clazz = checkNotNull(clazz);
            this.object = checkNotNull(object);
            this.name = checkNotNull(name);
        }

        public Class getClazz() {
            return clazz;
        }

        public Object getObject() {
            return object;
        }

        public Optional<String> getName() {
            return name;
        }

    }

    // ------------------------------------------------------------------------

    public SpringDomainBundle(final Domain domain,
                              final Class springConfigurations,
                              final BeanDescriptor... beans) {
        this(
            checkNotNull(domain),
            Lists.newArrayList(checkNotNull(springConfigurations)),
            checkNotNull(beans)
        );
    }

    public SpringDomainBundle(final Domain domain,
                              final List<Class> springConfigurations,
                              final BeanDescriptor... beans) {
        super(
            checkNotNull(domain),
            new DomainResolver().getLabel(domain.getClass())
        );
        this.applicationContext = initialize(
            checkNotNull(springConfigurations),
            checkNotNull(beans)
        );

    }

    // ------------------------------------------------------------------------

    protected AnnotationConfigApplicationContext initialize(final List<Class> springConfigurations,
                                                            final BeanDescriptor... beans) {
        final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        for (final Class springConfiguration : springConfigurations) {
            applicationContext.register(springConfiguration);
        }

        for (final BeanDescriptor bean : beans) {
            if (bean.getName().isPresent()) {
                beanFactory.registerSingleton(bean.getName().get(), bean.getObject());
            } else {
                beanFactory.registerSingleton(bean.getClazz().getSimpleName(), bean.getObject());
            }
        }

        return applicationContext;
    }

    @Override
    public void configure(final Platform.BuilderContext context) {
        checkNotNull(context);

        applicationContext.setParent(BuilderContextHelper.createApplicationContextFrom(context));

        final ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        configureConfigPropertyPlaceHolder(beanFactory, context);

        doConfigure(beanFactory, context);

        applicationContext.refresh();

        this.commandHandlers.addAll(applicationContext.getBeansOfType(CommandHandler.class).values());
        this.repositories.addAll(applicationContext.getBeansOfType(Repository.class).values());
        this.queryHandlers.addAll(applicationContext.getBeansOfType(QueryHandler.class).values());
        this.eventListeners.addAll(applicationContext.getBeansOfType(EventListener.class).values());
        this.sagas.addAll(applicationContext.getBeansOfType(Saga.class).values());
        this.queryInterceptorFactories.addAll(applicationContext.getBeansOfType(QueryInterceptorFactory.class).values());
        this.commandInterceptorFactories.addAll(applicationContext.getBeansOfType(CommandInterceptorFactory.class).values());
        this.eventInterceptorFactories.addAll(applicationContext.getBeansOfType(EventInterceptorFactory.class).values());
    }

    protected void configureConfigPropertyPlaceHolder(final ConfigurableListableBeanFactory beanFactory,
                                                      final  Platform.BuilderContext context) {
        TypeSafeConfigPropertyPlaceholder configPropertyPlaceholder = new TypeSafeConfigPropertyPlaceholder(context.getConfiguration());
        configPropertyPlaceholder.postProcessBeanFactory(beanFactory);
    }

    protected void doConfigure(final ConfigurableListableBeanFactory beanFactory, final  Platform.BuilderContext context) {
        /* empty */
    }

    public <E> Optional<E> get(final Class<E> clazz) {
        return Optional.fromNullable(applicationContext.getBean(checkNotNull(clazz)));
    }

}
