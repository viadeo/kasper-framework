// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.event.Event;
import org.axonframework.eventhandling.annotation.EventHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class EntityResolver extends AbstractResolver<Entity> {

    private ConceptResolver conceptResolver;
    private RelationResolver relationResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Entity";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Entity> clazz) {

        if (Concept.class.isAssignableFrom(clazz)) {
            return this.conceptResolver.getDomainClass((Class<? extends Concept>) clazz);
        }

        if (Relation.class.isAssignableFrom(clazz)) {
            return this.relationResolver.getDomainClass((Class<? extends Relation>) clazz);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(final Class<? extends Entity> clazz) {
        return String.format("The %s entity", this.getLabel(clazz));
    }

    @Override
    public String getLabel(final Class<? extends Entity> clazz) {
        return clazz.getSimpleName().replace("Entity", "");
    }

    // ------------------------------------------------------------------------

    public void setConceptResolver(final ConceptResolver conceptResolver) {
        this.conceptResolver = checkNotNull(conceptResolver);
    }

    public void setRelationResolver(final RelationResolver relationResolver) {
        this.relationResolver = checkNotNull(relationResolver);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Collection<Class<? extends Event>> getListenedSourceEvents(final Class<? extends Entity> clazz) {
        final List<Class<? extends Event>> listenedSourceEvents = Lists.newArrayList();

        final Method[] methods = checkNotNull(clazz).getDeclaredMethods();
        for (Method method : methods) {
            if (null != method.getAnnotation(EventHandler.class)) {
                final Class[] types = method.getParameterTypes();
                if (types.length == 1) {
                    if (Event.class.isAssignableFrom(types[0])) {
                        listenedSourceEvents.add((Class<? extends Event>) types[0]);
                    }
                }
            }
        }

        return Collections.unmodifiableCollection(listenedSourceEvents);
    }

}
