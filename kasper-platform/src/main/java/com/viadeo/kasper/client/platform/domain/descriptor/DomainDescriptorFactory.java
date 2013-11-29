package com.viadeo.kasper.client.platform.domain.descriptor;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.eventhandling.annotation.EventHandler;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DomainDescriptorFactory {

    private static final Function<CommandHandler, CommandHandlerDescriptor> toCommandHandlerDescriptor = new Function<CommandHandler, CommandHandlerDescriptor>() {
        @Override
        public CommandHandlerDescriptor apply(CommandHandler commandHandler) {
            return toCommandHandlerDescriptor(commandHandler);
        }
    };

    private static final Function<QueryHandler, QueryHandlerDescriptor> toQueryHandlerDescriptor = new Function<QueryHandler, QueryHandlerDescriptor>() {
        @Override
        public QueryHandlerDescriptor apply(QueryHandler queryHandler) {
            return toQueryHandlerDescriptor(queryHandler);
        }
    };

    private static final Function<EventListener, EventListenerDescriptor> toEventListenerDescriptor = new Function<EventListener, EventListenerDescriptor>() {
        @Override
        public EventListenerDescriptor apply(EventListener eventListener) {
            return toEventListenerDescriptor(eventListener);
        }
    };

    private static final Function<IRepository, RepositoryDescriptor> toRepositoryDescriptor = new Function<IRepository, RepositoryDescriptor>() {
        @Override
        public RepositoryDescriptor apply(IRepository repository) {
            return toRepositoryDescriptor(repository);
        }
    };

    public DomainDescriptor createFrom(DomainBundle domainBundle) {
        return new DomainDescriptor(domainBundle.getDomain().getClass()
                , Collections2.transform(domainBundle.getQueryHandlers(), toQueryHandlerDescriptor)
                , Collections2.transform(domainBundle.getCommandHandlers(), toCommandHandlerDescriptor)
                , Collections2.transform(domainBundle.getRepositories(), toRepositoryDescriptor)
                , Collections2.transform(domainBundle.getEventListeners(), toEventListenerDescriptor)
        );
    }

    public DomainDescriptor createFrom(
              Class domainClass
            , Collection<QueryHandler> queryHandlers
            , Collection<CommandHandler> commandHandlers
            , Collection<IRepository> repositories
            , Collection<EventListener> eventListeners
    ) {
        return new DomainDescriptor(
                  domainClass
                , Collections2.transform(queryHandlers, toQueryHandlerDescriptor)
                , Collections2.transform(commandHandlers, toCommandHandlerDescriptor)
                , Collections2.transform(repositories, toRepositoryDescriptor)
                , Collections2.transform(eventListeners, toEventListenerDescriptor)
        );
    }


    public static CommandHandlerDescriptor toCommandHandlerDescriptor(CommandHandler commandHandler) {
        Class<? extends CommandHandler> commandHandlerClass = commandHandler.getClass();
        Optional<? extends Class> commandClass = ReflectionGenericsResolver.getParameterTypeFromClass(commandHandlerClass, CommandHandler.class, CommandHandler.COMMAND_PARAMETER_POSITION);
        return new CommandHandlerDescriptor(commandHandlerClass, commandClass.get());
    }

    public static QueryHandlerDescriptor toQueryHandlerDescriptor(QueryHandler queryHandler) {
        Class<? extends QueryHandler> queryHandlerClass = queryHandler.getClass();
        Optional<? extends Class> queryClass = ReflectionGenericsResolver.getParameterTypeFromClass(queryHandlerClass, QueryHandler.class, QueryHandler.PARAMETER_QUERY_POSITION);
        Optional<? extends Class> queryResultClass = ReflectionGenericsResolver.getParameterTypeFromClass(queryHandlerClass, QueryHandler.class, QueryHandler.PARAMETER_RESULT_POSITION);
        return new QueryHandlerDescriptor(queryHandlerClass, queryClass.get(), queryResultClass.get());
    }

    public static EventListenerDescriptor toEventListenerDescriptor(EventListener eventListener) {
        Class<? extends EventListener> eventListenerClass = eventListener.getClass();
        Optional<? extends Class> eventClass = ReflectionGenericsResolver.getParameterTypeFromClass(eventListenerClass, EventListener.class, EventListener.EVENT_PARAMETER_POSITION);
        return new EventListenerDescriptor(eventListenerClass, eventClass.get());
    }

    public static RepositoryDescriptor toRepositoryDescriptor(IRepository repository) {
        Class<? extends IRepository> repositoryClass = repository.getClass();
        Optional<? extends Class> optEntityClass = ReflectionGenericsResolver.getParameterTypeFromClass(repositoryClass, IRepository.class, IRepository.ENTITY_PARAMETER_POSITION);
        return new RepositoryDescriptor(repositoryClass, toAggregateDescriptor(optEntityClass.get()));
    }

    public static AggregateDescriptor toAggregateDescriptor(Class entityClass){
        AggregateDescriptor aggregateDescriptor;

        if (Relation.class.isAssignableFrom(entityClass)) {
            Optional<? extends Class> sourceClass = ReflectionGenericsResolver.getParameterTypeFromClass(entityClass, Relation.class, 0);
            Optional<? extends Class> targetClass = ReflectionGenericsResolver.getParameterTypeFromClass(entityClass, Relation.class, 1);

            List<Class> listenedSourceEvents = listEventUsedByEventHandler(entityClass);
            aggregateDescriptor = new AggregateDescriptor(entityClass, sourceClass.get(), targetClass.get(), listenedSourceEvents.toArray(new Class[listenedSourceEvents.size()]));
        } else {
            List<Class> listenedSourceEvents = listEventUsedByEventHandler(entityClass);
            aggregateDescriptor = new AggregateDescriptor(entityClass, listenedSourceEvents.toArray(new Class[listenedSourceEvents.size()]));
        }
        return aggregateDescriptor;
    }

    private static List<Class> listEventUsedByEventHandler(Class clazz){
        List<Class> listenedSourceEvents = Lists.newArrayList();
        Method[] methods = checkNotNull(clazz).getDeclaredMethods();
        for (Method method : methods) {
            if (null != method.getAnnotation(EventHandler.class)) {
                final Class[] types = method.getParameterTypes();
                if ((types.length == 1) && Event.class.isAssignableFrom(types[0])) {
                    listenedSourceEvents.add(types[0]);
                }
            }
        }
        return listenedSourceEvents;
    }
}
