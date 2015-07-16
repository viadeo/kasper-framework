// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.annotation.XBidirectional;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class RelationResolver extends AbstractResolver<Relation> {

    private static final ConcurrentMap<Class, Class> SOURCES_CACHE = Maps.newConcurrentMap();
    private static final ConcurrentMap<Class, Class> TARGETS_CACHE = Maps.newConcurrentMap();

    private ConceptResolver conceptResolver;

    // ------------------------------------------------------------------------

    public RelationResolver() { }

    public RelationResolver(final ConceptResolver conceptResolver) {
        this.conceptResolver = checkNotNull(conceptResolver);
    }

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Relation";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Relation> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final XKasperRelation relationAnnotation = clazz.getAnnotation(XKasperRelation.class);

        if (null != relationAnnotation) {
            final Class<? extends Domain> domain = relationAnnotation.domain();
            DOMAINS_CACHE.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public String getVerb(final Class<? extends Relation> clazz) {
        final XKasperRelation relationAnnotation =
                checkNotNull(clazz).getAnnotation(XKasperRelation.class);

        final String verb;
        if ((null != relationAnnotation) && (! relationAnnotation.verb().isEmpty())) {
            verb = relationAnnotation.verb();
        } else {
            verb = "";

        }
        return verb;
    }

    @Override
    public String getLabel(final Class<? extends Relation> clazz) {
        final XKasperRelation relationAnnotation =
                checkNotNull(clazz).getAnnotation(XKasperRelation.class);

        if ((null != relationAnnotation) && (! relationAnnotation.label().isEmpty())) {
            return relationAnnotation.label().replaceAll(" ", "");
        }

        return clazz.getSimpleName().replace("Relation", "");
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(Class<? extends Relation> clazz) {
        final XKasperRelation annotation =
                checkNotNull(clazz).getAnnotation(XKasperRelation.class);

        String description = "";

        if (null != annotation) {
            description = annotation.description();
        }

        if (description.isEmpty()) {
            description = String.format("The %s relation between %s and %s",
                    this.getLabel(clazz),
                    conceptResolver.getLabel(this.getSourceEntityClass(clazz)),
                    conceptResolver.getLabel(this.getTargetEntityClass(clazz))
            );
        }

        return description;
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends Concept> getSourceEntityClass(final Class<? extends Relation> clazz) {

        if (SOURCES_CACHE.containsKey(checkNotNull(clazz))) {
            return (Class<? extends Concept>) SOURCES_CACHE.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Concept>> sourceClazz =
                (Optional<Class<? extends Concept>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                Relation.class,
                                Relation.SOURCE_PARAMETER_POSITION
                        );

        if ( ! sourceClazz.isPresent()) {
            throw new KasperException("Unable to find source concept type for relation " + clazz.getClass());
        }

        SOURCES_CACHE.put(clazz, sourceClazz.get());
        return sourceClazz.get();
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Concept> getTargetEntityClass(final Class<? extends Relation> clazz) {

        if (TARGETS_CACHE.containsKey(checkNotNull(clazz))) {
            return (Class<? extends Concept>) TARGETS_CACHE.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Concept>> targetClazz =
                (Optional<Class<? extends Concept>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                Relation.class,
                                Relation.TARGET_PARAMETER_POSITION
                        );

        if  ( ! targetClazz.isPresent()) {
            throw new KasperException("Unable to find target concept type for relation " + clazz.getClass());
        }

        TARGETS_CACHE.put(clazz, targetClazz.get());
        return targetClazz.get();
    }

    // ------------------------------------------------------------------------

    public boolean isBidirectional(final Class<? extends Relation> clazz) {
        final XBidirectional biDirAnno =
                checkNotNull(clazz).getAnnotation(XBidirectional.class);
        return (null != biDirAnno);
    }

    public Optional<String> biDirectionalVerb(final Class<? extends Relation> clazz) {
        final XBidirectional biDirAnno =
                checkNotNull(clazz).getAnnotation(XBidirectional.class);

        if ((null != biDirAnno) && (! biDirAnno.inverse_verb().isEmpty())) {
            return Optional.of(biDirAnno.inverse_verb());
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public void setConceptResolver(final ConceptResolver conceptResolver) {
        this.conceptResolver = checkNotNull(conceptResolver);
    }

}
