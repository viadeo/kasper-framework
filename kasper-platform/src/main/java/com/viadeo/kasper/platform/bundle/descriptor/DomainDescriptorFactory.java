// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.bundle.descriptor;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.aggregate.ddd.IRepository;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.core.component.command.aggregate.Relation;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.EventListener;
import com.viadeo.kasper.core.component.saga.Saga;
import com.viadeo.kasper.core.component.saga.SagaIdReconciler;
import com.viadeo.kasper.core.component.saga.step.Step;
import com.viadeo.kasper.core.component.saga.step.StepProcessor;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class DomainDescriptorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainDescriptorFactory.class);

    private static final Function<CommandHandler, CommandHandlerDescriptor> TO_COMMAND_HANDLER_DESCRIPTOR_FUNCTION = new Function<CommandHandler, CommandHandlerDescriptor>() {
        @Override
        public CommandHandlerDescriptor apply(final CommandHandler commandHandler) {
            return toCommandHandlerDescriptor(checkNotNull(commandHandler));
        }
    };

    private static final Function<QueryHandler, QueryHandlerDescriptor> TO_QUERY_HANDLER_DESCRIPTOR_FUNCTION = new Function<QueryHandler, QueryHandlerDescriptor>() {
        @Override
        public QueryHandlerDescriptor apply(final QueryHandler queryHandler) {
            return toQueryHandlerDescriptor(checkNotNull(queryHandler));
        }
    };

    private static final Function<EventListener, EventListenerDescriptor> TO_EVENT_LISTENER_DESCRIPTOR_FUNCTION = new Function<EventListener, EventListenerDescriptor>() {
        @Override
        public EventListenerDescriptor apply(final EventListener eventListener) {
            return toEventListenerDescriptor(checkNotNull(eventListener));
        }
    };

    private static final Function<Repository, RepositoryDescriptor> TO_REPOSITORY_DESCRIPTOR_FUNCTION = new Function<Repository, RepositoryDescriptor>() {
        @Override
        public RepositoryDescriptor apply(final Repository repository) {
            return toRepositoryDescriptor(checkNotNull(repository));
        }
    };

    // ------------------------------------------------------------------------

    public final Optional<StepProcessor> stepProcessor;

    public DomainDescriptorFactory() {
        this(null);
    }

    public DomainDescriptorFactory(StepProcessor stepProcessor) {
        this.stepProcessor = Optional.fromNullable(stepProcessor);
    }

    // ------------------------------------------------------------------------

    public DomainDescriptor createFrom(final DomainBundle domainBundle) {
        return new DomainDescriptor(
            domainBundle.getName(),
            domainBundle.getDomain().getClass(),
            Collections2.transform(domainBundle.getQueryHandlers(), TO_QUERY_HANDLER_DESCRIPTOR_FUNCTION),
            Collections2.transform(domainBundle.getCommandHandlers(), TO_COMMAND_HANDLER_DESCRIPTOR_FUNCTION),
            Collections2.transform(domainBundle.getRepositories(), TO_REPOSITORY_DESCRIPTOR_FUNCTION),
            Collections2.transform(domainBundle.getEventListeners(), TO_EVENT_LISTENER_DESCRIPTOR_FUNCTION),
            getSagaDescriptor(domainBundle.getSagas()),
            retrieveEventsFrom(domainBundle.getDomain().getClass())
        );
    }

    public DomainDescriptor createFrom(
            final String domainName,
            final Class<? extends Domain> domainClass,
            final Collection<QueryHandler> queryHandlers,
            final Collection<CommandHandler> commandHandlers,
            final Collection<Repository> repositories,
            final Collection<EventListener> eventListeners,
            final Collection<Saga> sagas
    ) {
        return new DomainDescriptor(
            checkNotNull(domainName),
            checkNotNull(domainClass),
            Collections2.transform(checkNotNull(queryHandlers), TO_QUERY_HANDLER_DESCRIPTOR_FUNCTION),
            Collections2.transform(checkNotNull(commandHandlers), TO_COMMAND_HANDLER_DESCRIPTOR_FUNCTION),
            Collections2.transform(checkNotNull(repositories), TO_REPOSITORY_DESCRIPTOR_FUNCTION),
            Collections2.transform(checkNotNull(eventListeners), TO_EVENT_LISTENER_DESCRIPTOR_FUNCTION),
            getSagaDescriptor(checkNotNull(sagas)),
            retrieveEventsFrom(domainClass)
        );
    }

    private List<SagaDescriptor> getSagaDescriptor(Collection<Saga> sagas) {
        final List<SagaDescriptor> sagaDescriptors = Lists.newArrayList();

        for (Saga saga : sagas) {
            List<SagaDescriptor.StepDescriptor> stepDescriptors = Lists.newArrayList();

            if (stepProcessor.isPresent()) {
                Set<Step> steps = stepProcessor.get().process(saga.getClass(), SagaIdReconciler.NONE);
                for (Step step : steps) {
                    stepDescriptors.add(
                            new SagaDescriptor.StepDescriptor(
                                    step.name(),
                                    step.getSupportedEvent(),
                                    step.getActions()
                            )
                    );
                }
            }

            sagaDescriptors.add(new SagaDescriptor(saga.getClass(), stepDescriptors));
        }

        return sagaDescriptors;
    }
    // ------------------------------------------------------------------------

    public static Collection<Class<? extends Event>> retrieveEventsFrom(final Class<? extends Domain> domainClass) {
        final List<Class<? extends Event>> eventClasses = Lists.newArrayList();

        final ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AssignableTypeFilter(Event.class));

        for (final BeanDefinition bd : scanner.findCandidateComponents(domainClass.getPackage().getName())) {
            try {
                @SuppressWarnings("unchecked")
                Class<Event> eventClass = (Class<Event>) Class.forName(bd.getBeanClassName());
                if ( ! (Modifier.isAbstract(eventClass.getModifiers()) || Modifier.isInterface(eventClass.getModifiers())) ) {
                    eventClasses.add(eventClass);
                }
            } catch (ClassNotFoundException e) {
                LOGGER.error("Failed to found class : '{}'", bd.getBeanClassName());
            }
        }

        return eventClasses;
    }

    @SuppressWarnings("unchecked")
    public static CommandHandlerDescriptor toCommandHandlerDescriptor(final CommandHandler commandHandler) {
        final Class<? extends CommandHandler> commandHandlerClass = commandHandler.getClass();
        final Optional<? extends Class> commandClass =
                ReflectionGenericsResolver.getParameterTypeFromClass(
                    commandHandlerClass,
                    CommandHandler.class,
                    CommandHandler.COMMAND_PARAMETER_POSITION
                );

        return new CommandHandlerDescriptor(commandHandlerClass, commandClass.get());
    }

    @SuppressWarnings("unchecked")
    public static QueryHandlerDescriptor toQueryHandlerDescriptor(final QueryHandler queryHandler) {
        final Class<? extends QueryHandler> queryHandlerClass = queryHandler.getClass();
        final Optional<? extends Class> queryClass = ReflectionGenericsResolver.getParameterTypeFromClass(
            queryHandlerClass,
            QueryHandler.class,
            QueryHandler.PARAMETER_QUERY_POSITION
        );
        final Optional<Class> queryResultClass = (Optional<Class>) ReflectionGenericsResolver.getParameterTypeFromClass(
                queryHandlerClass,
                QueryHandler.class,
                QueryHandler.PARAMETER_RESULT_POSITION
        );

        if ( ! queryResultClass.isPresent()) {
            LOGGER.warn("Failed to identify the query result event : {}", queryHandler.getClass().getName());
        }

        return new QueryHandlerDescriptor(queryHandlerClass, queryClass.get(), queryResultClass.or(QueryResult.class));
    }

    @SuppressWarnings("unchecked")
    public static EventListenerDescriptor toEventListenerDescriptor(final EventListener eventListener) {
        final Class<? extends EventListener> eventListenerClass = eventListener.getClass();
        final Optional<Class> eventClass = (Optional<Class>) ReflectionGenericsResolver.getParameterTypeFromClass(
                eventListenerClass,
                EventListener.class,
                EventListener.EVENT_PARAMETER_POSITION
        );

        if ( ! eventClass.isPresent()) {
            LOGGER.warn("Failed to identify the listened event : {}", eventListener.getClass().getName());
        }

        return new EventListenerDescriptor(eventListenerClass, eventClass.or(Event.class));
    }

    @SuppressWarnings("unchecked")
    public static RepositoryDescriptor toRepositoryDescriptor(final Repository repository) {
        final Class<? extends Repository> repositoryClass = repository.getClass();
        final Optional<? extends Class> optEntityClass = ReflectionGenericsResolver.getParameterTypeFromClass(
            repositoryClass,
            IRepository.class,
            IRepository.ENTITY_PARAMETER_POSITION
        );
        return new RepositoryDescriptor(repositoryClass, toAggregateDescriptor(optEntityClass.get()));
    }

    @SuppressWarnings("unchecked")
    public static AggregateDescriptor toAggregateDescriptor(final Class<? extends AggregateRoot> entityClass){
        AggregateDescriptor aggregateDescriptor;

        if (Relation.class.isAssignableFrom(entityClass)) {
            final Optional<? extends Class> sourceClass = ReflectionGenericsResolver.getParameterTypeFromClass(entityClass, Relation.class, 0);
            final Optional<? extends Class> targetClass = ReflectionGenericsResolver.getParameterTypeFromClass(entityClass, Relation.class, 1);

            final List<Class<? extends Event>> listenedSourceEvents = listEventUsedByEventHandler(entityClass);
            aggregateDescriptor = new AggregateDescriptor(entityClass, sourceClass.get(), targetClass.get(), listenedSourceEvents);
        } else {
            final List<Class<? extends Event>> listenedSourceEvents = listEventUsedByEventHandler(entityClass);
            aggregateDescriptor = new AggregateDescriptor(entityClass, listenedSourceEvents);
        }

        return aggregateDescriptor;
    }

    @SuppressWarnings("unchecked")
    private static List<Class<? extends Event>> listEventUsedByEventHandler(final Class clazz) {
        final List<Class<? extends Event>> listenedSourceEvents = Lists.newArrayList();
        final Method[] methods = checkNotNull(clazz).getDeclaredMethods();

        for (final Method method : methods) {
            if (null != method.getAnnotation(EventHandler.class)) {
                final Class[] types = method.getParameterTypes();
                if ((types.length == 1) && Event.class.isAssignableFrom(types[0])) {
                    listenedSourceEvents.add(types[0]);
                }
            }
        }

        return listenedSourceEvents;
    }

    @SuppressWarnings("unchecked")
    public static Map<Class, Class<? extends Domain>> mapToDomainClassByComponentClass(final DomainDescriptor domainDescriptor){
        final Map<Class, Class<? extends Domain>> domainClassByComponentClass = Maps.newHashMap();

        final Class<? extends Domain> domainClass = domainDescriptor.getDomainClass();

        for(final CommandHandlerDescriptor descriptor : domainDescriptor.getCommandHandlerDescriptors()){
            domainClassByComponentClass.put(descriptor.getReferenceClass(), domainClass);
            domainClassByComponentClass.put(descriptor.getCommandClass(), domainClass);
        }

        for(final QueryHandlerDescriptor descriptor : domainDescriptor.getQueryHandlerDescriptors()){
            domainClassByComponentClass.put(descriptor.getReferenceClass(), domainClass);
            domainClassByComponentClass.put(descriptor.getQueryClass(), domainClass);
            domainClassByComponentClass.put(descriptor.getQueryResultClass(), domainClass);
        }

        for(final RepositoryDescriptor descriptor : domainDescriptor.getRepositoryDescriptors()){
            domainClassByComponentClass.put(descriptor.getReferenceClass(), domainClass);
            domainClassByComponentClass.put(descriptor.getAggregateDescriptor().getReferenceClass(), domainClass);
        }

        for(final EventListenerDescriptor descriptor : domainDescriptor.getEventListenerDescriptors()){
            domainClassByComponentClass.put(descriptor.getReferenceClass(), domainClass);
            domainClassByComponentClass.put(descriptor.getEventClass(), domainClass);
        }

        return domainClassByComponentClass;
    }

}
