// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.KasperComponentDescriptor;
import com.viadeo.kasper.cqrs.query.QueryHandler;

import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class HttpQueryExposerPlugin implements HttpExposerPlugin {

    private HttpQueryExposer httpQueryExposer;

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public static final Function<KasperComponentDescriptor,Class<? extends QueryHandler>> TO_QUERY_HANDLER_CLASS_FUNCTION =
            new Function<KasperComponentDescriptor, Class<? extends QueryHandler>>() {
        @Override
        public Class<? extends QueryHandler> apply(final KasperComponentDescriptor descriptor) {
            return descriptor.getReferenceClass();
        }
    };

    @Override
    public void initialize(final Platform platform,
                           final MetricRegistry metricRegistry,
                           final DomainDescriptor... domainDescriptors) {
        final List<Class<? extends QueryHandler>> queryHandlerClasses = Lists.newArrayList();

        for (final DomainDescriptor domainDescriptor:domainDescriptors) {
            queryHandlerClasses.addAll(Collections2.transform(domainDescriptor.getQueryHandlerDescriptors(), TO_QUERY_HANDLER_CLASS_FUNCTION));
        }

        this.httpQueryExposer = new HttpQueryExposer(platform.getQueryGateway(), queryHandlerClasses);
    }

    @Override
    public HttpQueryExposer getHttpExposer() {
        checkState(null != httpQueryExposer, "The plugin should be initialized.");
        return httpQueryExposer;
    }

}
