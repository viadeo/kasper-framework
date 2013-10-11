// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.ddd.Domain;

public interface Resolver {

    String getTypeName();

    Optional<Class<? extends Domain>> getDomain(Class<?> clazz);

    Optional<String> getDomainLabel(Class<?> clazz);

}
