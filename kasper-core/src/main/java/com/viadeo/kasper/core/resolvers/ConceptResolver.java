// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperConcept;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConceptResolver extends AbstractResolver<Concept> {

    @Override
    public String getTypeName() {
        return "Concept";
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    @Override
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Concept> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final XKasperConcept conceptAnnotation = clazz.getAnnotation(XKasperConcept.class);

        if (null != conceptAnnotation) {
            final Class<? extends Domain> domain = conceptAnnotation.domain();
            DOMAINS_CACHE.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(Class<? extends Concept> conceptClazz) {
        final XKasperConcept annotation =
                checkNotNull(conceptClazz).getAnnotation(XKasperConcept.class);

        String description = "";

        if (null != annotation) {
            description = annotation.description();
        }

        if (description.isEmpty()) {
            description = String.format("The %s concept", this.getLabel(conceptClazz));
        }

        return description;
    }

    // ------------------------------------------------------------------------

    @Override
    public String getLabel(Class<? extends Concept> conceptClazz) {
        final XKasperConcept annotation =
                checkNotNull(conceptClazz).getAnnotation(XKasperConcept.class);

        String label = "";

        if (null != annotation) {
            label = annotation.label().replaceAll(" ", "");
        }

        if (label.isEmpty()) {
            label = String.format(conceptClazz.getSimpleName().replaceAll("Concept", ""));
        }

        return label;
    }

}
