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
package com.viadeo.kasper.core.component.event.saga;

import com.google.common.collect.Lists;
import com.viadeo.kasper.core.component.event.saga.factory.DefaultSagaFactoryProvider;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactoryProvider;
import com.viadeo.kasper.core.component.event.saga.repository.InMemorySagaRepository;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.StepChecker;
import com.viadeo.kasper.core.component.event.saga.step.StepProcessor;
import com.viadeo.kasper.core.component.event.saga.step.StepResolver;
import com.viadeo.kasper.core.component.event.saga.step.Steps;
import com.viadeo.kasper.core.component.event.saga.step.facet.FacetApplierRegistry;

import java.util.List;

public class DefaultSagaManager extends SagaManager {

    public DefaultSagaManager(
            final SagaFactoryProvider sagaFactoryProvider,
            final SagaRepository repository,
            final StepProcessor operationProcessor
    ) {
        super(sagaFactoryProvider, repository, operationProcessor);
    }

    public static DefaultSagaManager build() {
        final SagaFactoryProvider sagaFactoryProvider = new DefaultSagaFactoryProvider();
        final FacetApplierRegistry facetApplierRegistry = new FacetApplierRegistry();
        final List<StepResolver> stepResolvers = Lists.newArrayList((StepResolver)
                new Steps.StartStepResolver(facetApplierRegistry),
                new Steps.BasicStepResolver(facetApplierRegistry),
                new Steps.EndStepResolver(facetApplierRegistry)
        );

        final StepChecker stepChecker = new Steps.Checker();
        final StepProcessor operationProcessor = new StepProcessor(
                stepChecker,
                stepResolvers.toArray(new StepResolver[stepResolvers.size()])
        );
        final SagaRepository repository = new InMemorySagaRepository(sagaFactoryProvider);

        return new DefaultSagaManager(sagaFactoryProvider, repository, operationProcessor);
    }

}
