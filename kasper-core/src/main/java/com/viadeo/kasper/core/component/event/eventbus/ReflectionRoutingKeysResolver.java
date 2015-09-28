package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.listener.EventDescriptor;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class ReflectionRoutingKeysResolver implements RoutingKeysResolver {

    private final List<Class<Event>> rootClasses = Arrays.asList(Event.class);
    private static final Reflections reflections = new Reflections();

    @Override
    public RoutingKeys resolve(org.axonframework.eventhandling.EventListener eventListener) {

        if (!(eventListener instanceof EventListener)) {
            throw new IllegalArgumentException("routing key has to be an instance of " + EventListener.class.getName());
        }

        final Set<EventDescriptor> eventDescriptors = ((EventListener) eventListener).getEventDescriptors();
        final Set<RoutingKeys.RoutingKey> routingKeys = Sets.newHashSet();

        for (final EventDescriptor eventDescriptor : eventDescriptors) {
            final Class eventClass = eventDescriptor.getEventClass();

            if (rootClasses.contains(eventClass)) {
                routingKeys.add(new RoutingKeys.RoutingKey("#", eventDescriptor.isDeprecated()));
            } else {
                routingKeys.add(new RoutingKeys.RoutingKey(eventClass.getName(), eventDescriptor.isDeprecated()));
                final Set subTypes = reflections.getSubTypesOf(eventClass);
                for (final Object type : subTypes) {
                    routingKeys.add(new RoutingKeys.RoutingKey(((Class) type).getName(), eventDescriptor.isDeprecated()));
                }
            }
        }

        return new RoutingKeys(routingKeys);
    }
}
