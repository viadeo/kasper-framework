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
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.bundle.descriptor.CommandHandlerDescriptor;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpCommandExposerPlugin extends HttpExposerPlugin<HttpCommandExposer> {

    public static final Function<CommandHandlerDescriptor, ExposureDescriptor<Command, CommandHandler>> TO_DESCRIPTOR_FUNCTION =
            new Function<CommandHandlerDescriptor, ExposureDescriptor<Command, CommandHandler>>() {
                @Override
                public ExposureDescriptor<Command, CommandHandler> apply(final CommandHandlerDescriptor descriptor) {
                    checkNotNull(descriptor);
                    return new ExposureDescriptor<>(descriptor.getCommandClass(), descriptor.getReferenceClass());
                }
            };

    // ------------------------------------------------------------------------

    public HttpCommandExposerPlugin(){
        this(ObjectMapperProvider.INSTANCE.mapper());
    }

    public HttpCommandExposerPlugin(final ObjectMapper objectMapper){
        this(new SimpleHttpContextDeserializer(), objectMapper);
    }

    public HttpCommandExposerPlugin(final HttpContextDeserializer httpContextDeserializer, final ObjectMapper objectMapper){
        super(httpContextDeserializer, objectMapper);
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final Platform platform, final MetricRegistry metricRegistry, final DomainDescriptor... domainDescriptors) {
        final List<ExposureDescriptor<Command, CommandHandler>> exposureDescriptors = Lists.newArrayList();

        for (final DomainDescriptor domainDescriptor:domainDescriptors) {
            exposureDescriptors.addAll(Collections2.transform(
                    domainDescriptor.getCommandHandlerDescriptors(),
                    TO_DESCRIPTOR_FUNCTION
            ));
        }

        initialize(
            new HttpCommandExposer(
                platform.getCommandGateway(),
                platform.getMeta(),
                exposureDescriptors,
                getContextDeserializer(),
                getMapper()
            )
        );
    }

}
