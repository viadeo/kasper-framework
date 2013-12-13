// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.KasperComponentDescriptor;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.query.exposition.query.QueryFactoryBuilder;
import com.viadeo.kasper.tools.ObjectMapperProvider;

import java.util.List;

public class HttpQueryExposerPlugin extends HttpExposerPlugin<HttpQueryExposer> {

    @SuppressWarnings("unchecked")
    public static final Function<KasperComponentDescriptor,Class<? extends QueryHandler>> TO_QUERY_HANDLER_CLASS_FUNCTION =
            new Function<KasperComponentDescriptor, Class<? extends QueryHandler>>() {
        @Override
        public Class<? extends QueryHandler> apply(final KasperComponentDescriptor descriptor) {
            return descriptor.getReferenceClass();
        }
    };

    // ------------------------------------------------------------------------

    public HttpQueryExposerPlugin(){
        this(ObjectMapperProvider.INSTANCE.mapper());
    }

    public HttpQueryExposerPlugin(final ObjectMapper objectMapper){
        this(new HttpContextDeserializer(), objectMapper);
    }

    public HttpQueryExposerPlugin(final HttpContextDeserializer httpContextDeserializer, final ObjectMapper objectMapper){
        super(httpContextDeserializer, objectMapper);
    }

    @Override
    public void initialize(final Platform platform,
                           final MetricRegistry metricRegistry,
                           final DomainDescriptor... domainDescriptors) {
        final List<Class<? extends QueryHandler>> queryHandlerClasses = Lists.newArrayList();

        for (final DomainDescriptor domainDescriptor:domainDescriptors) {
            queryHandlerClasses.addAll(Collections2.transform(domainDescriptor.getQueryHandlerDescriptors(), TO_QUERY_HANDLER_CLASS_FUNCTION));
        }

        initialize(
                new HttpQueryExposer(
                        platform.getQueryGateway(),
                        queryHandlerClasses,
                        new QueryFactoryBuilder().create(),
                        getContextDeserializer(),
                        getMapper()
                )
        );
    }

}
