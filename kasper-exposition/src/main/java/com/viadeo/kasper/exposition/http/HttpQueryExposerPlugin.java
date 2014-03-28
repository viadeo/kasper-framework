// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.QueryHandlerDescriptor;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.query.exposition.query.QueryFactoryBuilder;
import com.viadeo.kasper.tools.ObjectMapperProvider;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpQueryExposerPlugin extends HttpExposerPlugin<HttpQueryExposer> {

    public static final Function<QueryHandlerDescriptor, ExposureDescriptor<Query, QueryHandler>> TO_DESCRIPTOR_FUNCTION =
            new Function<QueryHandlerDescriptor, ExposureDescriptor<Query, QueryHandler>>() {
                @Override
                public ExposureDescriptor<Query, QueryHandler> apply(final QueryHandlerDescriptor descriptor) {
                    checkNotNull(descriptor);
                    return new ExposureDescriptor<>(descriptor.getQueryClass(), descriptor.getReferenceClass());
                }
            };

    // ------------------------------------------------------------------------

    public HttpQueryExposerPlugin() {
        this(ObjectMapperProvider.INSTANCE.mapper());
    }

    public HttpQueryExposerPlugin(final ObjectMapper objectMapper){
        this(new HttpContextDeserializer(), checkNotNull(objectMapper));
    }

    public HttpQueryExposerPlugin(final HttpContextDeserializer httpContextDeserializer,
                                  final ObjectMapper objectMapper) {
        super(checkNotNull(httpContextDeserializer), checkNotNull(objectMapper));
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final Platform platform,
                           final MetricRegistry metricRegistry,
                           final DomainDescriptor... domainDescriptors) {

        final List<ExposureDescriptor<Query, QueryHandler>> exposureDescriptors = Lists.newArrayList();

        for (final DomainDescriptor domainDescriptor : domainDescriptors) {
            exposureDescriptors.addAll(Collections2.transform(
                    domainDescriptor.getQueryHandlerDescriptors(),
                    TO_DESCRIPTOR_FUNCTION)
            );
        }

        initialize(
            new HttpQueryExposer(
                platform.getQueryGateway(),
                platform.getMeta(),
                exposureDescriptors,
                new QueryFactoryBuilder().create(),
                getContextDeserializer(),
                getMapper()
            )
        );
    }

}
