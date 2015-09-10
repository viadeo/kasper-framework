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
