// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.core.component.annotation.XKasperSaga;
import com.viadeo.kasper.core.component.event.saga.exception.SagaInstantiationException;
import com.viadeo.kasper.core.component.event.saga.exception.SagaPersistenceException;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.StepProcessor;
import com.viadeo.kasper.core.component.event.saga.step.Steps;
import com.viadeo.kasper.core.component.event.saga.step.facet.FacetApplierRegistry;
import com.viadeo.kasper.core.component.event.saga.step.facet.MeasuringFacetApplier;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.DomainResolver;
import com.viadeo.kasper.core.resolvers.ResolverFactory;
import com.viadeo.kasper.core.resolvers.SagaResolver;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;


public class MeasuredSagaExecutorUTest {

    MetricRegistry metricRegistry;
    SagaRepository sagaRepository;
    MeasuredSagaExecutor<MySaga> sagaExecutor;

    @Before
    public void setUp() throws Exception {
        metricRegistry = spy(new MetricRegistry());

        SagaResolver sagaResolver = new SagaResolver();
        sagaResolver.setDomainResolver(new DomainResolver());

        ResolverFactory resolverFactory = new ResolverFactory();
        resolverFactory.setSagaResolver(sagaResolver);

        KasperMetrics.setResolverFactory(resolverFactory);

        FacetApplierRegistry applierRegistry = new FacetApplierRegistry();
        applierRegistry.register(new MeasuringFacetApplier(metricRegistry));

        StepProcessor stepProcessor = new StepProcessor(
                new Steps.Checker(),
                new Steps.StartStepResolver(applierRegistry),
                new Steps.EndStepResolver(applierRegistry),
                new Steps.BasicStepResolver(applierRegistry)
        );

        sagaRepository = mock(SagaRepository.class);
        when(sagaRepository.load(any(Class.class), anyObject())).thenReturn(Optional.absent());

        sagaExecutor = new MeasuredSagaExecutor<>(
                metricRegistry,
                MySaga.class,
                stepProcessor.process(MySaga.class, SagaIdReconciler.NONE),
                new SagaFactory() {
                    @Override
                    public <SAGA extends Saga> SAGA create(Object identifier, Class<SAGA> sagaClass) throws SagaInstantiationException {
                        return (SAGA) new MySaga(Integer.parseInt(identifier.toString()));
                    }
                },
                sagaRepository
        );
    }

    @Test
    public void verify_metrics_publication_in_executing_method() throws SagaPersistenceException {
        when(sagaRepository.load(any(Class.class), eq(1))).thenReturn(Optional.of(new MySaga(1)));

        sagaExecutor.execute(1, "onTimeoutReached", false);

        verify(metricRegistry).timer("mydomain.saga.mysaga.request-handle-time");
        verify(metricRegistry).timer("mydomain.saga.mysaga.persist-handle-time");
        verify(metricRegistry).timer("mydomain.saga.mysaga.onTimeoutReached.invoke-handle-time");
    }

    @Test
    public void verify_metrics_publication_in_executing_start_step() {
        sagaExecutor.execute(Contexts.empty(), new StartEvent(2));

        verify(metricRegistry).timer("mydomain.saga.mysaga.request-handle-time");
        verify(metricRegistry, times(2)).timer("mydomain.saga.mysaga.persist-handle-time");
        verify(metricRegistry).timer("mydomain.saga.mysaga.start.invoke-handle-time");
    }

    @Test
    public void verify_metrics_publication_in_executing_end_step() throws SagaPersistenceException {
        when(sagaRepository.load(any(Class.class), eq(3))).thenReturn(Optional.of(new MySaga(3)));

        sagaExecutor.execute(Contexts.empty(), new EndEvent(3));

        verify(metricRegistry).timer("mydomain.saga.mysaga.request-handle-time");
        verify(metricRegistry).timer("mydomain.saga.mysaga.persist-handle-time");
        verify(metricRegistry).timer("mydomain.saga.mysaga.end.invoke-handle-time");
    }

    public static class StartEvent implements Event {

        private final int id;

        public StartEvent(int id) {
            this.id = id;
        }

        public Integer getId() { return id; }
    }

    public static class EndEvent implements Event {

        private final int id;

        public EndEvent(int id) {
            this.id = id;
        }

        public Integer getId() { return id; }
    }

    @XKasperDomain(label = "myDomain", prefix = "myDomain")
    public class MyDomain implements Domain { }

    @XKasperSaga(domain = MyDomain.class, description = "fake")
    public static class MySaga implements Saga {

        private final int id;

        public MySaga(int id) {
            this.id = id;
        }

        @XKasperSaga.Start(getter = "getId")
        @XKasperSaga.Schedule(methodName = "onTimeoutReached", delay = 5, unit = TimeUnit.MINUTES, end = true)
        public void start(StartEvent event) {

        }

        @XKasperSaga.End(getter = "getId")
        public void end(EndEvent event) {

        }

        public void onTimeoutReached() {

        }

        @Override
        public Optional<SagaIdReconciler> getIdReconciler() {
            return Optional.absent();
        }
    }
}
