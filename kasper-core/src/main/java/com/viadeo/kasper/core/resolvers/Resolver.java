// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.Domain;

import java.util.List;

public interface Resolver<T> {

    String getTypeName();

    Optional<Class<? extends Domain>> getDomainClass(Class<? extends T> clazz);

    String getDomainLabel(Class<? extends T> clazz);

    String getLabel(Class<? extends T> clazz);

    String getDescription(Class<? extends T> clazz);

    boolean isPublic(Class<? extends T> clazz);

    boolean isDeprecated(Class<? extends T> clazz);

    Optional<List<String>> getAliases(Class<? extends T> clazz);

    void clearCache();

}
