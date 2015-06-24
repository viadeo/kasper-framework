// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.spring;

import com.viadeo.kasper.event.saga.*;
import com.viadeo.kasper.event.saga.repository.InMemorySagaRepository;
import com.viadeo.kasper.event.saga.repository.SagaRepository;
import com.viadeo.kasper.event.saga.step.*;
import com.viadeo.kasper.event.saga.step.quartz.MethodInvocationScheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SagaConfiguration{

    @Bean(initMethod = "initialize")
    public Scheduler stepScheduler(ApplicationContext applicationContext) throws SchedulerException {
        SchedulerFactory sf = new StdSchedulerFactory();
        return new MethodInvocationScheduler(sf.getScheduler(), applicationContext);
    }

    @Bean
    public FacetApplierRegistry facetApplierRegistry() {
        return new FacetApplierRegistry();
    }

    @Bean
    public FacetApplier schedulingFacetApplier(final FacetApplierRegistry facetApplierRegistry, final Scheduler scheduler) {
        SchedulingFacetApplier applier = new SchedulingFacetApplier(scheduler);
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
    public SagaFactory sagaFactory(final ApplicationContext applicationContext) {
        return new DefaultSagaFactory(applicationContext);
    }

    @Bean
    public SagaRepository sagaRepository(final SagaFactory sagaFactory) {
        return new InMemorySagaRepository(sagaFactory);
    }

    @Bean
    public SagaManager sagaManager(final SagaFactory sagaFactory, final SagaRepository repository, final StepProcessor operationProcessor) {
        return new SagaManager(sagaFactory, repository, operationProcessor);
    }
}
