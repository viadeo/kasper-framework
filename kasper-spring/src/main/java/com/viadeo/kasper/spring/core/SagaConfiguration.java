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
package com.viadeo.kasper.spring.core;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactoryProvider;
import com.viadeo.kasper.core.component.event.saga.repository.InMemorySagaRepository;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.*;
import com.viadeo.kasper.core.component.event.saga.step.facet.FacetApplier;
import com.viadeo.kasper.core.component.event.saga.step.facet.FacetApplierRegistry;
import com.viadeo.kasper.core.component.event.saga.step.facet.MeasuringFacetApplier;
import com.viadeo.kasper.core.component.event.saga.step.facet.SchedulingFacetApplier;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SagaConfiguration {

    @Bean
    public Scheduler stepScheduler(final ObjectMapper objectMapper, final ApplicationContext applicationContext, final SagaManager sagaManager) throws SchedulerException {
        final SchedulerFactory sf = new StdSchedulerFactory();
        return new MethodInvocationSpringScheduler(objectMapper, sf.getScheduler(), sagaManager);
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
