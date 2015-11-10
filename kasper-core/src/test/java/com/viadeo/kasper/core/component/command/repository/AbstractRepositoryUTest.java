// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.codahale.metrics.MetricRegistry;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.id.TestFormats;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.unitofwork.CurrentUnitOfWork;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class AbstractRepositoryUTest {

    private AbstractRepository<ID,Concept> repository;

    @Before
    public void setUp() throws Exception {
        DefaultUnitOfWork unitOfWork = new DefaultUnitOfWork();
        unitOfWork.start();
        CurrentUnitOfWork.set(unitOfWork);

        MetricRegistry metricRegistry = new MetricRegistry();

        repository = spy(new BaseRepository<ID, Concept>(metricRegistry, mock(EventBus.class)) {
            @Override
            protected Optional<Concept> doLoad(ID aggregateIdentifier, Long expectedVersion) {
                return Optional.of(mock(Concept.class));
            }

            @Override protected void doSave(Concept aggregate) { }

            @Override protected void doDelete(Concept aggregate) { }
        });

        repository.setAxonRepository(
                repository.createAxonRepository(metricRegistry, repository)
        );
    }

    @Test
    public void check_aggregate_identifier_during_a_load() {
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);
        repository.load(id);
        verify(repository, times(1)).checkAggregateIdentifier(any(ID.class));

    }

    @Test
    public void check_aggregate_identifier_during_a_get() {
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);
        repository.get(id);
        verify(repository, times(1)).checkAggregateIdentifier(id);
    }

    @Test
    public void check_aggregate_identifier_during_an_has() {
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);
        repository.has(id);
        verify(repository, times(1)).checkAggregateIdentifier(id);
    }

}
