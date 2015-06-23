// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.spring;

import com.viadeo.kasper.event.saga.*;
import com.viadeo.kasper.event.saga.step.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SagaConfiguration{

    @Autowired(required = false)
    List<FacetApplier> facetAppliers;

    private FacetApplier[] getOrderedFacetAppliers() {
        final FacetApplier[] facetAppliers;

        if (this.facetAppliers == null) {
            facetAppliers = new FacetApplier[0];
        } else {
            facetAppliers = this.facetAppliers.toArray(new FacetApplier[this.facetAppliers.size()]);
        }

        return facetAppliers;
    }

    @Bean/*(initMethod = "initialize")*/
    public Scheduler sagaScheduler(ApplicationContext applicationContext) {
        //TODO
        return new Scheduler() { };
    }

    @Bean
    public SchedulingFacetApplier schedulingFacetApplier(final Scheduler scheduler) {
        return new SchedulingFacetApplier(scheduler);
    }

    @Bean
    public StepResolver startStepResolver() {
        return new Steps.StartStepResolver(getOrderedFacetAppliers());
    }

    @Bean
    public StepResolver basicStepResolver() {
        return new Steps.BasicStepResolver(getOrderedFacetAppliers());
    }

    @Bean
    public StepResolver endStepResolver() {
        return new Steps.EndStepResolver(getOrderedFacetAppliers());
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
