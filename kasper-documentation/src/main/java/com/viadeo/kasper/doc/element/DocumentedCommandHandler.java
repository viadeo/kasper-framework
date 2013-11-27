package com.viadeo.kasper.doc.element;

import com.viadeo.kasper.client.platform.domain.descriptor.CommandHandlerDescriptor;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;

public class DocumentedCommandHandler extends AbstractDomainElement {

    private final DocumentedCommand documentedCommand;

    public DocumentedCommandHandler(DocumentedDomain documentedDomain, CommandHandlerDescriptor commandHandlerDescriptor) {
        super(documentedDomain, DocumentedElementType.COMMAND_HANDLER, commandHandlerDescriptor.getReferenceClass());
        this.documentedCommand = new DocumentedCommand(documentedDomain, this, commandHandlerDescriptor.getCommandClass());
    }

    public LightDocumentedElement<DocumentedCommand> getCommand() {
        return documentedCommand.getLightDocumentedElement();
    }

    @Override
    public LightDocumentedElement<DocumentedCommandHandler> getLightDocumentedElement() {
        return new LightDocumentedElement<DocumentedCommandHandler>(this) {
            public String getCommandName() {
                return documentedElement.getCommand().getName();
            }
        };
    }

    @Override
    public void accept(DocumentedElementVisitor visitor) {
        documentedCommand.accept(visitor);
        visitor.visit(this);
    }

    public static class DocumentedCommand extends AbstractPropertyDomainElement {

        private final DocumentedCommandHandler documentedCommandHandler;

        public DocumentedCommand(DocumentedDomain domain, DocumentedCommandHandler documentedCommandHandler, Class commandClass) {
            super(domain, DocumentedElementType.COMMAND, commandClass);
            this.documentedCommandHandler = documentedCommandHandler;
        }

        public LightDocumentedElement<DocumentedCommandHandler> getCommandHandler() {
            return documentedCommandHandler.getLightDocumentedElement();
        }

        @Override
        public LightDocumentedElement<DocumentedCommand> getLightDocumentedElement() {
            return new LightDocumentedElement<>(this);
        }

        @Override
        public void accept(DocumentedElementVisitor visitor) {
            visitor.visit(this);
        }
    }
}
