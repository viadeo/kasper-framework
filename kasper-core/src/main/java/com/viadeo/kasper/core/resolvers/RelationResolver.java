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
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.Relation;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XBidirectional;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperRelation;

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
