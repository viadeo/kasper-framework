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
package com.viadeo.kasper.platform.bundle.descriptor;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.core.component.event.saga.Saga;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaDescriptor implements KasperComponentDescriptor {

    private final Class<? extends Saga> sagaClass;
    private final List<StepDescriptor> stepDescriptors;

    public SagaDescriptor(final Class<? extends Saga> sagaClass, final List<StepDescriptor> stepDescriptors) {
        this.sagaClass = checkNotNull(sagaClass);
        this.stepDescriptors = checkNotNull(stepDescriptors);
    }

    @Override
    public Class getReferenceClass() {
        return sagaClass;
    }

    public List<StepDescriptor> getStepDescriptors() {
        return stepDescriptors;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sagaClass, stepDescriptors);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SagaDescriptor other = (SagaDescriptor) obj;
        return Objects.equal(this.sagaClass, other.sagaClass) && Objects.equal(this.stepDescriptors, other.stepDescriptors);
    }

    public static class StepDescriptor implements KasperComponentDescriptor {

        private final String name;
        private final Class<? extends Event> eventClass;
        private final List<String> actions;

        public StepDescriptor(String name, Class<? extends Event> eventClass, List<String> actions) {
            this.name = checkNotNull(name);
            this.eventClass = checkNotNull(eventClass);
            this.actions = Lists.newArrayList(checkNotNull(actions));
        }

        @Override
        public Class getReferenceClass() {
            return eventClass;
        }

        public String getName() {
            return name;
        }

        public Class<? extends Event> getEventClass() {
            return eventClass;
        }

        public List<String> getActions() {
            return actions;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, eventClass, actions);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final StepDescriptor other = (StepDescriptor) obj;
            return Objects.equal(this.name, other.name) && Objects.equal(this.eventClass, other.eventClass) && Objects.equal(this.actions, other.actions);
        }
    }
}
