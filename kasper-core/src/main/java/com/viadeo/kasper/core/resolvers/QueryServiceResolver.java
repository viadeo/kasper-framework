// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryPayload;
import com.viadeo.kasper.cqrs.query.QueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.concurrent.ConcurrentMap;

public class QueryServiceResolver extends AbstractResolver<QueryService> {

    private static final ConcurrentMap<Class, Class> cacheQuery = Maps.newConcurrentMap();
    private static final ConcurrentMap<Class, Class> cacheQueryPayload = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "QueryService";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends QueryService> clazz) {

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final XKasperQueryService annotation = clazz.getAnnotation(XKasperQueryService.class);

        if (null != annotation) {
            final Class<? extends Domain> domain = annotation.domain();
            cacheDomains.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public Class<? extends Query> getQueryClass(final Class<? extends QueryService> clazz) {
        if (cacheQuery.containsKey(clazz)) {
            return cacheQuery.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Query>> queryClazz =
                (Optional<Class<? extends Query>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz, QueryService.class, QueryService.PARAMETER_QUERY_POSITION);

        if (!queryClazz.isPresent()) {
            throw new KasperException("Unable to find query type for query service " + clazz.getClass());
        }

        cacheQuery.put(clazz, queryClazz.get());
        return queryClazz.get();
    }

    // ------------------------------------------------------------------------

    public Class<? extends QueryPayload> getQueryPayloadClass(final Class<? extends QueryService> clazz) {
        if (cacheQueryPayload.containsKey(clazz)) {
            return cacheQueryPayload.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends QueryPayload>> queryPayloadClazz =
                (Optional<Class<? extends QueryPayload>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz, QueryService.class, QueryService.PARAMETER_PAYLOAD_POSITION);

        if (!queryPayloadClazz.isPresent()) {
            throw new KasperException("Unable to find query payload type for query service " + clazz.getClass());
        }

        cacheQueryPayload.put(clazz, queryPayloadClazz.get());
        return queryPayloadClazz.get();
    }

}
