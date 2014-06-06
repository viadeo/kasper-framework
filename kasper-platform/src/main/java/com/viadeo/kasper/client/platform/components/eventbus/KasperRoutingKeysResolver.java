package com.viadeo.kasper.client.platform.components.eventbus;

import com.google.common.collect.Lists;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.eventhandling.amqp.RoutingKeysResolver;
import org.axonframework.eventhandling.EventListener;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.List;
import java.util.Set;


public class KasperRoutingKeysResolver implements RoutingKeysResolver {

    private final List<Class<IEvent>> rootClasses = Arrays.asList(IEvent.class);
    private final Reflections reflections = new Reflections();

    @Override
    public List<String> resolve(EventListener listener) {

        if (!(listener instanceof com.viadeo.kasper.event.EventListener)) {
            throw new IllegalArgumentException("routing key has to be an instance of " + com.viadeo.kasper.event.EventListener.class.getName());
        }

        List<String> routingKeys = Lists.newArrayList();
        Class eventClass = ((com.viadeo.kasper.event.EventListener) listener).getEventClass();

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
