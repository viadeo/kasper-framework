package com.viadeo.kasper.exposition;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.NewPlatform;
import com.viadeo.kasper.client.platform.domain.descriptor.Descriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.cqrs.query.QueryHandler;

import java.util.List;

public class HttpQueryExposerPlugin implements HttpExposerPlugin {

    @SuppressWarnings("unchecked")
    public static final Function<Descriptor,Class<? extends QueryHandler>> TO_QUERY_HANDLER_CLASS_FUNCTION = new Function<Descriptor, Class<? extends QueryHandler>>() {
        @Override
        public Class<? extends QueryHandler> apply(Descriptor descriptor) {
            return descriptor.getReferenceClass();
        }
    };

    private HttpQueryExposer httpQueryExposer;

    @Override
    public void initialize(NewPlatform platform, DomainDescriptor... domainDescriptors) {
        List<Class<? extends QueryHandler>> queryHandlerClasses = Lists.newArrayList();

        for(DomainDescriptor domainDescriptor:domainDescriptors){
            queryHandlerClasses.addAll(Collections2.transform(domainDescriptor.getQueryHandlerDescriptors(), TO_QUERY_HANDLER_CLASS_FUNCTION));
        }

        this.httpQueryExposer = new HttpQueryExposer(platform.getQueryGateway(), queryHandlerClasses);
    }

    @Override
    public HttpQueryExposer getHttpExposer() {
        Preconditions.checkState(httpQueryExposer != null, "The plugin should be initialized.");
        return httpQueryExposer;
    }
}
