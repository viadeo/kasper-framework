// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.Relation;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class EntityResolver extends AbstractResolver {

    private static ConcurrentMap<Class<?>, Class<?>> cacheDomains = Maps.newConcurrentMap();

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
    public Optional<Class<? extends Domain>> getDomain(final Class<?> clazz) {

        if (Concept.class.isAssignableFrom(clazz)) {
            return this.conceptResolver.getDomain(clazz);
        }

        if (Relation.class.isAssignableFrom(clazz)) {
            return this.relationResolver.getDomain(clazz);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public void setConceptResolver(final ConceptResolver conceptResolver) {
        this.conceptResolver = checkNotNull(conceptResolver);
    }

    public void setRelationResolver(final RelationResolver relationResolver) {
        this.relationResolver = checkNotNull(relationResolver);
    }

}
