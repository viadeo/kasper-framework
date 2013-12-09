package com.viadeo.kasper.client.platform.domain;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.EventListener;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

public class SpringDomainBundle extends DefaultDomainBundle {

    private final AnnotationConfigApplicationContext applicationContext;

    public SpringDomainBundle(Domain domain, Class springConfigurations, BeanDescriptor... beans) {
        this(domain, Lists.newArrayList(springConfigurations), beans);
    }

    public SpringDomainBundle(Domain domain, List<Class> springConfigurations, BeanDescriptor... beans) {
        super(domain, new DomainResolver().getLabel(domain.getClass()));
        this.applicationContext = initialize(springConfigurations, beans);
    }

    protected AnnotationConfigApplicationContext initialize(List<Class> springConfigurations, BeanDescriptor... beans) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();

        for (Class springConfiguration : springConfigurations) {
            applicationContext.register(springConfiguration);
        }

        for (BeanDescriptor bean : beans) {
            if (bean.getName().isPresent()) {
                beanFactory.registerSingleton(bean.getName().get(), bean.getObject());
            } else {
                beanFactory.registerSingleton(bean.getClazz().getSimpleName(), bean.getObject());
            }
        }

        return applicationContext;
    }

    @Override
    public final void configure(Platform.BuilderContext context) {
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        doConfigure(beanFactory, context);
        applicationContext.refresh();

        this.commandHandlers.addAll(applicationContext.getBeansOfType(CommandHandler.class).values());
        this.repositories.addAll(applicationContext.getBeansOfType(Repository.class).values());
        this.queryHandlers.addAll(applicationContext.getBeansOfType(QueryHandler.class).values());
        this.eventListeners.addAll(applicationContext.getBeansOfType(EventListener.class).values());
    }

    protected void doConfigure(final ConfigurableListableBeanFactory beanFactory, final  Platform.BuilderContext context) {}

    public <E> Optional<E> get(Class<E> clazz) {
        return Optional.fromNullable(applicationContext.getBean(clazz));
    }

    public static class BeanDescriptor {

        private final Class clazz;
        private final Object object;
        private final Optional<String> name;

        public BeanDescriptor(Object object) {
            this(object.getClass(), object);
        }

        public BeanDescriptor(Class clazz, Object object) {
            this(Optional.<String>absent(), clazz, object);
        }

        public BeanDescriptor(String name, Object object) {
            this(Optional.of(name), object.getClass(), object);
        }

        protected BeanDescriptor(Optional<String> name, Class clazz, Object object) {
            this.clazz = clazz;
            this.object = object;
            this.name = name;
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
}
