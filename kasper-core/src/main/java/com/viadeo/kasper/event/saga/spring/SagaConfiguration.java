// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.spring;

import com.viadeo.kasper.event.saga.*;
import com.viadeo.kasper.event.saga.step.StepChecker;
import com.viadeo.kasper.event.saga.step.StepProcessor;
import com.viadeo.kasper.event.saga.step.StepResolver;
import com.viadeo.kasper.event.saga.step.Steps;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SagaConfiguration{

    @Bean
    public StepResolver startStepResolver() {
        return new Steps.StartStepResolver();
    }

    @Bean
    public StepResolver basicStepResolver() {
        return new Steps.BasicStepResolver();
    }

    @Bean
    public StepResolver endStepResolver() {
        return new Steps.EndStepResolver();
    }

    @Bean
    public StepChecker stepChecker() {
        return new Steps.Checker();
    }

    @Bean
    public StepProcessor stepProcessor(StepChecker stepChecker, List<StepResolver> stepResolvers) {
        return new StepProcessor(stepChecker, stepResolvers.toArray(new StepResolver[stepResolvers.size()]));
    }

    @Bean
    public SagaFactory sagaFactory(ApplicationContext applicationContext) {
        return new DefaultSagaFactory(applicationContext);
    }

    @Bean
    public SagaRepository sagaRepository(SagaFactory sagaFactory) {
        return new InMemorySagaRepository(sagaFactory);
    }

    @Bean
    public SagaManager sagaManager(SagaFactory sagaFactory, SagaRepository repository, StepProcessor operationProcessor) {
        return new SagaManager(sagaFactory, repository, operationProcessor);
    }
}
