// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.repository;

import com.codahale.metrics.MetricRegistry;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.api.id.DefaultKasperId;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class MetricsRepositoryFacadeTest {

    // ------------------------------------------------------------------------

    public MetricsRepositoryFacadeTest() {
        KasperMetrics.setMetricRegistry(new MetricRegistry());
    }

    // ------------------------------------------------------------------------
    // doSave()
    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithNoVersion_shouldDoSaveOnSave() {
        // Given
        final AggregateRoot agr = mock(AggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final MetricsRepositoryFacade<AggregateRoot> repo = new MetricsRepositoryFacade<AggregateRoot>(krepo);
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        doReturn(null).when(agr).getVersion();

        // When
        repo.doSave(agr);

        // Then
        verify(krepo).doSave(any(AggregateRoot.class));
        verifyNoMoreInteractions(krepo);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithVersion_shouldDoUpdateOnSave() {
        // Given
        final AggregateRoot agr = mock(AggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final MetricsRepositoryFacade<AggregateRoot> repo = new MetricsRepositoryFacade<AggregateRoot>(krepo);
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        doReturn(0L).when(agr).getVersion();

        // When
        repo.doSave(agr);

        // Then
        verify(krepo).doUpdate(any(AggregateRoot.class));
        verifyNoMoreInteractions(krepo);
    }

}
