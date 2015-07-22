// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.LinkedConcept;
import com.viadeo.kasper.core.component.command.aggregate.Relation;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.aggregate.ddd.Entity;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.eventhandling.annotation.EventHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class EntityResolver extends AbstractResolver<Entity> {

    private ConceptResolver conceptResolver;
    private RelationResolver relationResolver;

    // ------------------------------------------------------------------------

    public EntityResolver() { }

    public EntityResolver(final ConceptResolver conceptResolver, final RelationResolver relationResolver) {
        this.conceptResolver = checkNotNull(conceptResolver);
        this.relationResolver = checkNotNull(relationResolver);
    }

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Entity";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Entity> clazz) {

        if (Concept.class.isAssignableFrom(checkNotNull(clazz))) {
            return this.conceptResolver.getDomainClass((Class<? extends Concept>) clazz);
        }

        if (Relation.class.isAssignableFrom(clazz)) {
            return this.relationResolver.getDomainClass((Class<? extends Relation>) clazz);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public String getDescription(final Class<? extends Entity> clazz) {

        if (Concept.class.isAssignableFrom(checkNotNull(clazz))) {
            return conceptResolver.getDescription((Class<? extends Concept>) clazz);
        }

        if (Relation.class.isAssignableFrom(clazz)) {
            return relationResolver.getDescription((Class<? extends Relation>) clazz);
        }

        return String.format("The %s entity", this.getLabel(clazz));
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getLabel(final Class<? extends Entity> clazz) {

        if (Concept.class.isAssignableFrom(checkNotNull(clazz))) {
            return conceptResolver.getLabel((Class<? extends Concept>) clazz);
        }

        if (Relation.class.isAssignableFrom(clazz)) {
            return relationResolver.getLabel((Class<? extends Relation>) clazz);
        }

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
        for (final Method method : methods) {
            if (null != method.getAnnotation(EventHandler.class)) {
                final Class[] types = method.getParameterTypes();
                if ((types.length == 1) && Event.class.isAssignableFrom(types[0])) {
                    listenedSourceEvents.add((Class<? extends Event>) types[0]);
                }
            }
        }

        return Collections.unmodifiableCollection(listenedSourceEvents);
    }

    // ------------------------------------------------------------------------

    public List<Class<? extends Concept>> getComponentConcepts(final Class<? extends AggregateRoot> conceptClazz) {
        final List<Class<? extends Concept>> linkedConcepts = Lists.newArrayList();

        for (final Field field : checkNotNull(conceptClazz).getDeclaredFields()) {
            if (LinkedConcept.class.isAssignableFrom(field.getType())) {

                @SuppressWarnings("unchecked") // Safe
                final Optional<Class<? extends Concept>> linkedConceptClazz =
                        (Optional<Class<? extends Concept>>)
                                ReflectionGenericsResolver.getParameterTypeFromClass(
                                        field,
                                        LinkedConcept.class,
                                        LinkedConcept.CONCEPT_PARAMETER_POSITION
                                );

                if ( ! linkedConceptClazz.isPresent()) {
                    throw new KasperException(String.format(
                            "Unable to find concept type for linked field %s in concept %s",
                            field.getName(),
                            conceptClazz.getClass().getSimpleName()
                    ));
                }

                linkedConcepts.add(linkedConceptClazz.get());
            }
        }

        return linkedConcepts;
    }

}
