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

import com.google.common.collect.Lists;
import com.viadeo.kasper.doc.initializer.DocumentedElementVisitor;
import com.viadeo.kasper.platform.bundle.descriptor.SagaDescriptor;

import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class DocumentedSaga extends AbstractDomainElement {

    private final List<DocumentedSagaStep> steps;

    public DocumentedSaga(final DocumentedDomain domain, final SagaDescriptor descriptor) {
        super(checkNotNull(domain), DocumentedElementType.SAGA, checkNotNull(descriptor).getReferenceClass());
        this.steps = Lists.newArrayList();

        for (SagaDescriptor.StepDescriptor stepDescriptor : descriptor.getStepDescriptors()) {
            this.steps.add(new DocumentedSagaStep(this, stepDescriptor));
        }
    }

    @Override
    public void accept(final DocumentedElementVisitor visitor) {
        visitor.visit(this);
        for (DocumentedSagaStep step : steps) {
            visitor.visit(step.getEvent());
        }
    }

    public List<DocumentedSagaStep> getSteps() {
        return steps;
    }

    public static class DocumentedSagaStep implements Serializable {

        private final DocumentedEventListener.DocumentedEvent documentedEvent;
        private final SagaDescriptor.StepDescriptor stepDescriptor;

        public DocumentedSagaStep(
                DocumentedSaga saga,
                SagaDescriptor.StepDescriptor stepDescriptor
        ) {
            this.stepDescriptor = stepDescriptor;
            this.documentedEvent = new DocumentedEventListener.DocumentedEvent(
                    saga,
                    stepDescriptor.getEventClass()
            );
        }

        public String getName() {
            return stepDescriptor.getName();
        }

        public List<String> getActions() {
            return stepDescriptor.getActions();
        }

        public DocumentedEventListener.DocumentedEvent getEvent() {
            return documentedEvent;
        }
    }
}
