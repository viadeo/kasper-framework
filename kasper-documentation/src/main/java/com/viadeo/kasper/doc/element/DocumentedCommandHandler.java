// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.viadeo.kasper.client.platform.domain.descriptor.CommandHandlerDescriptor;
import com.viadeo.kasper.cqrs.command.CommandResponse;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;
import com.viadeo.kasper.doc.nodes.DocumentedBean;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DocumentedCommandHandler extends AbstractDomainElement {

    private final DocumentedCommand documentedCommand;

    public static class DocumentedCommand extends AbstractPropertyDomainElement {

        private final DocumentedCommandHandler documentedCommandHandler;
        private final DocumentedBean response;

        public DocumentedCommand(final DocumentedDomain domain,
                                 final DocumentedCommandHandler documentedCommandHandler,
                                 final Class commandClass) {
            super(domain, DocumentedElementType.COMMAND, commandClass);

            this.documentedCommandHandler = documentedCommandHandler;
            this.response = new DocumentedBean(CommandResponse.class);
        }

        public LightDocumentedElement<DocumentedCommandHandler> getCommandHandler() {
            return documentedCommandHandler.getLightDocumentedElement();
        }

        public DocumentedBean getResponse() {
            return response;
        }

        @Override
        public LightDocumentedElement<DocumentedCommand> getLightDocumentedElement() {
            return new LightDocumentedInputElement<>(this);
        }

        @Override
        public void accept(final DocumentedElementVisitor visitor) {
            visitor.visit(this);
        }

        @Override
        public boolean isPublicAccess() {
            return documentedCommandHandler.isPublicAccess();
        }
    }

    // ------------------------------------------------------------------------

    public DocumentedCommandHandler(final DocumentedDomain documentedDomain,
                                    final CommandHandlerDescriptor commandHandlerDescriptor) {
        super(
                checkNotNull(documentedDomain),
                DocumentedElementType.COMMAND_HANDLER,
                checkNotNull(commandHandlerDescriptor).getReferenceClass()
        );
        this.documentedCommand = new DocumentedCommand(
                documentedDomain, this,
                commandHandlerDescriptor.getCommandClass()
        );
    }

    // ------------------------------------------------------------------------

    @Override
    public LightDocumentedElement<DocumentedCommandHandler> getLightDocumentedElement() {
        return new LightDocumentedElement<DocumentedCommandHandler>(this) {
            public String getCommandName() {
                return documentedElement.getCommand().getName();
            }
        };
    }

    @Override
    public void accept(final DocumentedElementVisitor visitor) {
        documentedCommand.accept(visitor);
        visitor.visit(this);
    }

    // ------------------------------------------------------------------------

    public LightDocumentedElement<DocumentedCommand> getCommand() {
        return documentedCommand.getLightDocumentedElement();
    }

}
