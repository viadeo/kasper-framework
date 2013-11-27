package com.viadeo.kasper.client.platform.domain.descriptor;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
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

    public static DomainDescriptor createFrom(DomainBundle domainBundle) {
        return new DomainDescriptor(domainBundle.getDomain().getClass()
                , ImmutableList.<QueryHandlerDescriptor>builder().addAll(retrieveQueryHandlerDescriptors(domainBundle.getQueryHandlers())).build()
                , ImmutableList.<CommandHandlerDescriptor>builder().addAll(retrieveCommandHandlerDescriptors(domainBundle.getCommandHandlers())).build()
                , ImmutableList.<RepositoryDescriptor>builder().addAll(retrieveRepositoryDescriptors(domainBundle.getRepositories())).build()
                , ImmutableList.<EventListenerDescriptor>builder().addAll(retrieveEventListenerDescriptors(domainBundle.getEventListeners())).build()
        );
    }

    public static DomainDescriptor createFrom(
              Class domainClass
            , Collection<QueryHandler> queryHandlers
            , Collection<CommandHandler> commandHandlers
            , Collection<IRepository> repositories
            , Collection<EventListener> eventListeners
    ) {
        return new DomainDescriptor(
                  domainClass
                , ImmutableList.<QueryHandlerDescriptor>builder().addAll(retrieveQueryHandlerDescriptors(queryHandlers)).build()
                , ImmutableList.<CommandHandlerDescriptor>builder().addAll(retrieveCommandHandlerDescriptors(commandHandlers)).build()
                , ImmutableList.<RepositoryDescriptor>builder().addAll(retrieveRepositoryDescriptors(repositories)).build()
                , ImmutableList.<EventListenerDescriptor>builder().addAll(retrieveEventListenerDescriptors(eventListeners)).build()
        );
    }


    public static Collection<CommandHandlerDescriptor> retrieveCommandHandlerDescriptors(Collection<CommandHandler> commandHandlers) {
        List<CommandHandlerDescriptor> descriptors = Lists.newArrayList();

        for (CommandHandler commandHandler : commandHandlers) {
            Class<? extends CommandHandler> commandHandlerClass = commandHandler.getClass();
            Optional<? extends Class> commandClass = ReflectionGenericsResolver.getParameterTypeFromClass(commandHandlerClass, CommandHandler.class, CommandHandler.COMMAND_PARAMETER_POSITION);
            descriptors.add(new CommandHandlerDescriptor(commandHandlerClass, commandClass.get()));
        }
        return descriptors;
    }

    public static Collection<QueryHandlerDescriptor> retrieveQueryHandlerDescriptors(Collection<QueryHandler> queryHandlers) {
        List<QueryHandlerDescriptor> descriptors = Lists.newArrayList();

        for (QueryHandler queryHandler : queryHandlers) {
            Class<? extends QueryHandler> queryHandlerClass = queryHandler.getClass();
            Optional<? extends Class> queryClass = ReflectionGenericsResolver.getParameterTypeFromClass(queryHandlerClass, QueryHandler.class, QueryHandler.PARAMETER_QUERY_POSITION);
            Optional<? extends Class> queryResultClass = ReflectionGenericsResolver.getParameterTypeFromClass(queryHandlerClass, QueryHandler.class, QueryHandler.PARAMETER_RESULT_POSITION);
            descriptors.add(new QueryHandlerDescriptor(queryHandlerClass, queryResultClass.get(), queryClass.get()));
        }

        return descriptors;
    }

    public static Collection<EventListenerDescriptor> retrieveEventListenerDescriptors(Collection<EventListener> eventListeners) {
        List<EventListenerDescriptor> descriptors = Lists.newArrayList();

        for (EventListener eventListener : eventListeners) {
            Class<? extends EventListener> eventListenerClass = eventListener.getClass();
            Optional<? extends Class> eventClass = ReflectionGenericsResolver.getParameterTypeFromClass(eventListenerClass, EventListener.class, EventListener.EVENT_PARAMETER_POSITION);
            descriptors.add(new EventListenerDescriptor(eventListenerClass, eventClass.get()));
        }

        return descriptors;
    }

    public static Collection<RepositoryDescriptor> retrieveRepositoryDescriptors(Collection<IRepository> repositories) {
        List<RepositoryDescriptor> descriptors = Lists.newArrayList();

        for (IRepository repository : repositories) {
            Class<? extends IRepository> repositoryClass = repository.getClass();
            Optional<? extends Class> optEntityClass = ReflectionGenericsResolver.getParameterTypeFromClass(repositoryClass, IRepository.class, IRepository.ENTITY_PARAMETER_POSITION);

            Class entityClass = optEntityClass.get();
            descriptors.add(new RepositoryDescriptor(repositoryClass, retrieveAggregateDescriptor(entityClass)));
        }

        return descriptors;
    }

    public static AggregateDescriptor retrieveAggregateDescriptor(Class entityClass){
        AggregateDescriptor aggregateDescriptor;

        if (Relation.class.isAssignableFrom(entityClass)) {
            Optional<? extends Class> sourceClass = ReflectionGenericsResolver.getParameterTypeFromClass(entityClass, Relation.class, 0);
            Optional<? extends Class> targetClass = ReflectionGenericsResolver.getParameterTypeFromClass(entityClass, Relation.class, 1);

            List<Class> listenedSourceEvents = getEventUsedByEventHandler(entityClass);
            aggregateDescriptor = new AggregateDescriptor(entityClass, sourceClass.get(), targetClass.get(), listenedSourceEvents.toArray(new Class[listenedSourceEvents.size()]));
        } else {
            List<Class> listenedSourceEvents = getEventUsedByEventHandler(entityClass);
            aggregateDescriptor = new AggregateDescriptor(entityClass, listenedSourceEvents.toArray(new Class[listenedSourceEvents.size()]));
        }
        return aggregateDescriptor;
    }

    private static List<Class> getEventUsedByEventHandler(Class clazz){
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
