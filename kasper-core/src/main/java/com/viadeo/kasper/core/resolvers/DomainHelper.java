// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class DomainHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainHelper.class);

    private final Map<Class, Class<? extends Domain>> domainClassByComponentClasses;

    // ------------------------------------------------------------------------

    public DomainHelper() {
        domainClassByComponentClasses = Maps.newConcurrentMap();
    }

    // ------------------------------------------------------------------------

    public void add(final Map<Class, Class<? extends Domain>> domainClassByComponentClasses) {
        this.domainClassByComponentClasses.putAll(checkNotNull(domainClassByComponentClasses));
        LOGGER.debug("registered components : {}", domainClassByComponentClasses);
    }

    public void add(final Class componentClass, final Class<? extends Domain> domainClass) {
        this.domainClassByComponentClasses.put(checkNotNull(componentClass), checkNotNull(domainClass));
    }

    public Class<? extends Domain> getDomainClassOf(final Class componentClass){
        checkNotNull(componentClass);
        LOGGER.debug("getDomainClassOf {}, {}", componentClass, domainClassByComponentClasses);
        return domainClassByComponentClasses.get(componentClass);
    }

}
