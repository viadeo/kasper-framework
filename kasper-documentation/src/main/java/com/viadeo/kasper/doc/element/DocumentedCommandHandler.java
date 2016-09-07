// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;
import com.viadeo.kasper.doc.nodes.DocumentedBean;
import com.viadeo.kasper.platform.bundle.descriptor.CommandHandlerDescriptor;

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

        @Override
        public DocumentedAuthorization getAuthorization() {
            return documentedCommandHandler.getAuthorization();
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
