package com.viadeo.kasper.exposition;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.viadeo.kasper.client.platform.NewPlatform;
import com.viadeo.kasper.client.platform.domain.descriptor.Descriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.cqrs.command.CommandHandler;

import java.util.List;

public class HttpCommandExposerPlugin implements HttpExposerPlugin {

    @SuppressWarnings("unchecked")
    public static final Function<Descriptor,Class<? extends CommandHandler>> TO_COMMAND_HANDLER_CLASS_FUNCTION = new Function<Descriptor, Class<? extends CommandHandler>>() {
        @Override
        public Class<? extends CommandHandler> apply(Descriptor descriptor) {
            return descriptor.getReferenceClass();
        }
    };

    private HttpCommandExposer httpCommandExposer;

    @Override
    public void initialize(NewPlatform platform, DomainDescriptor... domainDescriptors) {
        List<Class<? extends CommandHandler>> commandHandlerClasses = Lists.newArrayList();

        for(DomainDescriptor domainDescriptor:domainDescriptors){
            commandHandlerClasses.addAll(Collections2.transform(domainDescriptor.getCommandHandlerDescriptors(), TO_COMMAND_HANDLER_CLASS_FUNCTION));
        }

        this.httpCommandExposer = new HttpCommandExposer(platform.getCommandGateway(), commandHandlerClasses);
    }

    @Override
    public HttpCommandExposer getHttpExposer() {
        Preconditions.checkState(httpCommandExposer != null, "The plugin should be initialized.");
        return httpCommandExposer;
    }
}
