package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class ReflectionRoutingKeysResolver implements RoutingKeysResolver {

    private final List<Class<Event>> rootClasses = Arrays.asList(Event.class);
    private static final Reflections reflections = new Reflections();

    @Override
    public List<String> resolve(org.axonframework.eventhandling.EventListener eventListener) {

        if (!(eventListener instanceof EventListener)) {
            throw new IllegalArgumentException("routing key has to be an instance of " + EventListener.class.getName());
        }

        final Set<Class<?>> eventClasses = ((EventListener) eventListener).getEventClasses();
        final List<String> routingKeys = Lists.newArrayList();

        for (final Class eventClass : eventClasses) {
            if (rootClasses.contains(eventClass)) {
                routingKeys.add("#");
            } else {
                routingKeys.add(eventClass.getName());
                Set subTypes = reflections.getSubTypesOf(eventClass);
                for (Object type : subTypes) {
                    routingKeys.add(((Class) type).getName());
                }
            }
        }

        return routingKeys;
    }
}
