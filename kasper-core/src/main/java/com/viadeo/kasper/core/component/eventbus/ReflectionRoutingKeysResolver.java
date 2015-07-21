package com.viadeo.kasper.core.component.eventbus;

import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import org.axonframework.eventhandling.EventListener;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class ReflectionRoutingKeysResolver implements RoutingKeysResolver {

    private final List<Class<Event>> rootClasses = Arrays.asList(Event.class);
    private static final Reflections reflections = new Reflections();

    @Override
    public List<String> resolve(EventListener listener) {

        if (!(listener instanceof com.viadeo.kasper.core.component.event.EventListener)) {
            throw new IllegalArgumentException("routing key has to be an instance of " + com.viadeo.kasper.core.component.event.EventListener.class.getName());
        }

        List<String> routingKeys = Lists.newArrayList();
        Class eventClass = ((com.viadeo.kasper.core.component.event.EventListener) listener).getEventClass();

        if (rootClasses.contains(eventClass)) {
            routingKeys.add("#");
        } else {
            routingKeys.add(eventClass.getName());
            Set subTypes = reflections.getSubTypesOf(eventClass);
            for (Object type : subTypes) {
                routingKeys.add(((Class) type).getName());
            }
        }

        return routingKeys;
    }
}
