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
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.LinkedConcept;
import com.viadeo.kasper.core.component.command.aggregate.Relation;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.aggregate.ddd.Entity;
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
