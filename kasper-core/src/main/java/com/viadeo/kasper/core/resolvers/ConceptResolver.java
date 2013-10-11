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
import com.viadeo.kasper.er.annotation.XKasperConcept;

import java.util.concurrent.ConcurrentMap;

public class ConceptResolver extends AbstractResolver<Concept> {

    @Override
    public String getTypeName() {
        return "Concept";
    }

    // ------------------------------------------------------------------------

    @Override
    public Optional<Class<? extends Domain>> getDomain(final Class<? extends Concept> clazz) {

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final XKasperConcept conceptAnnotation = (XKasperConcept)
                clazz.getAnnotation(XKasperConcept.class);

        if (null != conceptAnnotation) {
            final Class<? extends Domain> domain = conceptAnnotation.domain();
            cacheDomains.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

}
