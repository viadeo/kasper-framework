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
package com.viadeo.kasper.core.component.event.saga;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactoryProvider;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.component.event.saga.step.StepProcessor;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class SagaManager {

    private final SagaFactoryProvider sagaFactoryProvider;
    private final SagaRepository repository;
    private final StepProcessor operationProcessor;
    private final Map<Class<? extends Saga>, SagaExecutor> descriptors;

    // ------------------------------------------------------------------------

    public SagaManager(
            final SagaFactoryProvider sagaFactoryProvider,
            final SagaRepository repository,
            final StepProcessor operationProcessor
    ) {
        this.operationProcessor = checkNotNull(operationProcessor);
        this.sagaFactoryProvider = checkNotNull(sagaFactoryProvider);
        this.repository = checkNotNull(repository);
        this.descriptors = Maps.newHashMap();
    }

    // ------------------------------------------------------------------------

    public <SAGA extends Saga> SagaExecutor<SAGA> register(final SAGA saga) {
        checkNotNull(saga);

        @SuppressWarnings("unchecked")
        final Class<SAGA> sagaClass = (Class<SAGA>) saga.getClass();

        checkState(
            !descriptors.containsKey(sagaClass),
            String.format("The specified saga is already registered : %s", sagaClass.getName())
        );

        final SagaFactory factory = checkNotNull(sagaFactoryProvider.getOrCreate(saga));
        final SagaIdReconciler reconciler = saga.getIdReconciler().or(SagaIdReconciler.NONE);
        final Set<Step> steps = operationProcessor.process(sagaClass, reconciler);
        final SagaExecutor<SAGA> executor = createSagaExecutor(sagaClass, factory, steps);

        descriptors.put(sagaClass, executor);

        repository.initStoreFor(sagaClass);

        return executor;
    }

    protected <SAGA extends Saga> SagaExecutor<SAGA> createSagaExecutor(Class<SAGA> sagaClass, SagaFactory factory, Set<Step> steps) {
        return new SagaExecutor<>(sagaClass, steps, factory, repository);
    }

    public Optional<SagaExecutor> get(final Class<? extends Saga> sagaClass) {
        checkNotNull(sagaClass);
        return Optional.fromNullable(descriptors.get(sagaClass));
    }

    public StepProcessor getStepProcessor() {
        return this.operationProcessor;
    }

    @VisibleForTesting
    public void clear() {
        descriptors.clear();
    }

}
