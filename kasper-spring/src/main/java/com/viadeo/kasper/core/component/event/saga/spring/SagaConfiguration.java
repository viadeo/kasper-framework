// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.spring;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.event.saga.factory.DefaultSpringSagaFactoryProvider;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactoryProvider;
import com.viadeo.kasper.core.component.event.saga.repository.InMemorySagaRepository;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.*;
import com.viadeo.kasper.core.component.event.saga.step.facet.FacetApplier;
import com.viadeo.kasper.core.component.event.saga.step.facet.FacetApplierRegistry;
import com.viadeo.kasper.core.component.event.saga.step.facet.MeasuringFacetApplier;
import com.viadeo.kasper.core.component.event.saga.step.facet.SchedulingFacetApplier;
import com.viadeo.kasper.core.component.event.saga.step.quartz.MethodInvocationScheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SagaConfiguration {

    @Bean(initMethod = "initialize", destroyMethod = "shutdown")
    public Scheduler stepScheduler(final ObjectMapper objectMapper, final ApplicationContext applicationContext, final SagaManager sagaManager) throws SchedulerException {
        final SchedulerFactory sf = new StdSchedulerFactory();
        return new MethodInvocationScheduler(objectMapper, sf.getScheduler(), sagaManager);
    }

    @Bean
    public FacetApplierRegistry facetApplierRegistry() {
        return new FacetApplierRegistry();
    }

    @Bean
    public FacetApplier schedulingFacetApplier(final FacetApplierRegistry facetApplierRegistry, final Scheduler scheduler) {
        final SchedulingFacetApplier applier = new SchedulingFacetApplier(scheduler);
        facetApplierRegistry.register(applier);
        return applier;
    }

    @Bean
    public FacetApplier measuringFacetApplier(final FacetApplierRegistry facetApplierRegistry, final MetricRegistry metricRegistry) {
        final MeasuringFacetApplier applier = new MeasuringFacetApplier(metricRegistry);
        facetApplierRegistry.register(applier);
        return applier;
    }

    @Bean
    public StepResolver startStepResolver(final FacetApplierRegistry facetApplierRegistry) {
        return new Steps.StartStepResolver(facetApplierRegistry);
    }

    @Bean
    public StepResolver basicStepResolver(final FacetApplierRegistry facetApplierRegistry) {
        return new Steps.BasicStepResolver(facetApplierRegistry);
    }

    @Bean
    public StepResolver endStepResolver(final FacetApplierRegistry facetApplierRegistry) {
        return new Steps.EndStepResolver(facetApplierRegistry);
    }

    @Bean
    public StepChecker stepChecker() {
        return new Steps.Checker();
    }

    @Bean
    public StepProcessor stepProcessor(final StepChecker stepChecker, final List<StepResolver> stepResolvers) {
        return new StepProcessor(stepChecker, stepResolvers.toArray(new StepResolver[stepResolvers.size()]));
    }

    @Bean
    public SagaFactoryProvider sagaFactoryProvider(final ApplicationContext applicationContext) {
        return new DefaultSpringSagaFactoryProvider(applicationContext);
    }

    @Bean
    public SagaRepository sagaRepository(final SagaFactoryProvider sagaFactoryProvider) {
        return new InMemorySagaRepository(sagaFactoryProvider);
    }

    @Bean
    public SagaManager sagaManager(final SagaFactoryProvider sagaFactoryProvider, final SagaRepository repository, final StepProcessor operationProcessor) {
        return new SagaManager(sagaFactoryProvider, repository, operationProcessor);
    }

}
