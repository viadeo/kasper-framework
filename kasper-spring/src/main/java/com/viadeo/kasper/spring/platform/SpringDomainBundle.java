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
package com.viadeo.kasper.spring.platform;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.platform.builder.PlatformContext;
import com.viadeo.kasper.platform.bundle.DefaultDomainBundle;
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
    public void configure(final PlatformContext context) {
        checkNotNull(context);

        applicationContext.setParent(PlatformContextHelper.createApplicationContextFrom(context));

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
                                                      final PlatformContext context) {
        TypeSafeConfigPropertyPlaceholder configPropertyPlaceholder = new TypeSafeConfigPropertyPlaceholder(context.getConfiguration());
        configPropertyPlaceholder.postProcessBeanFactory(beanFactory);
    }

    protected void doConfigure(final ConfigurableListableBeanFactory beanFactory, final PlatformContext context) {
        /* empty */
    }

    public <E> Optional<E> get(final Class<E> clazz) {
        return Optional.fromNullable(applicationContext.getBean(checkNotNull(clazz)));
    }

}
