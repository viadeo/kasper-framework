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
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.tools.ObjectMapperProvider;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpCommandExposerPlugin extends HttpExposerPlugin<HttpCommandExposer> {

    @SuppressWarnings("unchecked")
    public static final Function<KasperComponentDescriptor,Class<? extends CommandHandler>> TO_COMMAND_HANDLER_CLASS_FUNCTION =
            new Function<KasperComponentDescriptor, Class<? extends CommandHandler>>() {
        @Override
        public Class<? extends CommandHandler> apply(final KasperComponentDescriptor descriptor) {
            return checkNotNull(descriptor).getReferenceClass();
        }
    };

    // ------------------------------------------------------------------------

    public HttpCommandExposerPlugin(){
        this(ObjectMapperProvider.INSTANCE.mapper());
    }

    public HttpCommandExposerPlugin(final ObjectMapper objectMapper){
        this(new HttpContextDeserializer(), objectMapper);
    }

    public HttpCommandExposerPlugin(final HttpContextDeserializer httpContextDeserializer, final ObjectMapper objectMapper){
        super(httpContextDeserializer, objectMapper);
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final Platform platform, final MetricRegistry metricRegistry, final DomainDescriptor... domainDescriptors) {
        final List<Class<? extends CommandHandler>> commandHandlerClasses = Lists.newArrayList();

        for (final DomainDescriptor domainDescriptor:domainDescriptors) {
            commandHandlerClasses.addAll(Collections2.transform(
                domainDescriptor.getCommandHandlerDescriptors(),
                TO_COMMAND_HANDLER_CLASS_FUNCTION
            ));
        }

        initialize(
            new HttpCommandExposer(
                platform.getCommandGateway(),
                commandHandlerClasses,
                getContextDeserializer(),
                getMapper()
            )
        );
    }

}
