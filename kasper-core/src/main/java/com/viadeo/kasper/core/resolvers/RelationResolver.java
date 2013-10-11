// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.RootConcept;
import com.viadeo.kasper.er.annotation.XBidirectional;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.concurrent.ConcurrentMap;

public class RelationResolver extends AbstractResolver<Relation> {

    private static final ConcurrentMap<Class, Class> cacheSources = Maps.newConcurrentMap();
    private static final ConcurrentMap<Class, Class> cacheTargets = Maps.newConcurrentMap();

    private ConceptResolver conceptResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Relation";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Relation> clazz) {

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final XKasperRelation relationAnnotation = clazz.getAnnotation(XKasperRelation.class);

        if (null != relationAnnotation) {
            final Class<? extends Domain> domain = relationAnnotation.domain();
            cacheDomains.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getLabel(Class<? extends Relation> clazz) {
        final XKasperRelation relationAnnotation = clazz.getAnnotation(XKasperRelation.class);

        if ((null != relationAnnotation) && ! relationAnnotation.label().isEmpty()) {
            return relationAnnotation.label().replaceAll(" ", "");
        }

        return clazz.getSimpleName()
                .replace("Relation", "");
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(Class<? extends Relation> clazz) {
        final XKasperRelation annotation = clazz.getAnnotation(XKasperRelation.class);

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
    public Class<? extends RootConcept> getSourceEntityClass(final Class<? extends Relation> clazz) {
        if (cacheSources.containsKey(clazz)) {
            return (Class<? extends RootConcept>) cacheSources.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends RootConcept>> sourceClazz =
                (Optional<Class<? extends RootConcept>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz, Relation.class, Relation.SOURCE_PARAMETER_POSITION);

        if (!sourceClazz.isPresent()) {
            throw new KasperException("Unable to find source concept type for relation " + clazz.getClass());
        }

        cacheSources.put(clazz, sourceClazz.get());
        return sourceClazz.get();
    }

    @SuppressWarnings("unchecked")
    public Class<? extends RootConcept> getTargetEntityClass(final Class<? extends Relation> clazz) {
        if (cacheTargets.containsKey(clazz)) {
            return (Class<? extends RootConcept>) cacheTargets.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends RootConcept>> targetClazz =
                (Optional<Class<? extends RootConcept>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz, Relation.class, Relation.TARGET_PARAMETER_POSITION);

        if (!targetClazz.isPresent()) {
            throw new KasperException("Unable to find target concept type for relation " + clazz.getClass());
        }

        cacheTargets.put(clazz, targetClazz.get());
        return targetClazz.get();
    }

    // ------------------------------------------------------------------------

    public boolean isBidirectional(final Class<? extends Relation> clazz) {
        final XBidirectional biDirAnno = clazz.getAnnotation(XBidirectional.class);
        return (null != biDirAnno);
    }

    public Optional<String> biDirectionalVerb(final Class<? extends Relation> clazz) {
        final XBidirectional biDirAnno = clazz.getAnnotation(XBidirectional.class);
        if ((null != biDirAnno) &&  ! biDirAnno.verb().isEmpty()) {
            return Optional.of(biDirAnno.verb());
        }
        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public void setConceptResolver(final ConceptResolver conceptResolver) {
        this.conceptResolver = conceptResolver;
    }

}
