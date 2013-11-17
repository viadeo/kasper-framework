// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.impl.DefaultKasperId;
import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.mockito.AdditionalMatchers.gt;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ActionRepositoryFacadeTest {

    // ------------------------------------------------------------------------
    // doSave()
    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithNoCreationDate_shouldGenerateItsOwnCreationDateOnSave() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryFacade repo = new ActionRepositoryFacade(krepo);
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        assertNull(agr.getCreationDate());

        // When
        repo.doSave(agr);

        // Then
        verify(agr).setCreationDate(any(DateTime.class));
        verify(agr).setModificationDate(any(DateTime.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithNoModificationDateAndVersionNull_shouldSetModificationDateOnSave() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryFacade repo = new ActionRepositoryFacade(krepo);
        final DateTime t = DateTime.now();
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        doReturn(t).when(agr).getCreationDate();
        doReturn(null).when(agr).getVersion();

        // When
        repo.doSave(agr);

        // Then
        verify(agr).setModificationDate(t);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithNoModificationDateAndVersion_shouldSetModificationDateOnSave() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryFacade repo = new ActionRepositoryFacade(krepo);
        final DateTime t = DateTime.now().minusDays(1);
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        doReturn(t).when(agr).getCreationDate();
        doReturn(1L).when(agr).getVersion();

        // When
        repo.doSave(agr);

        // Then
        verify(agr).setModificationDate((DateTime) gt(t));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithNoVersion_shouldDoSaveOnSave() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryFacade repo = new ActionRepositoryFacade(krepo);
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
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryFacade repo = new ActionRepositoryFacade(krepo);
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        doReturn(0L).when(agr).getVersion();

        // When
        repo.doSave(agr);

        // Then
        verify(krepo).doUpdate(any(AggregateRoot.class));
        verifyNoMoreInteractions(krepo);
    }

    // ------------------------------------------------------------------------
    // doDelete()
    // ------------------------------------------------------------------------

     @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithNoCreationDate_shouldGenerateItsOwnCreationDateOnDelete() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryFacade repo = new ActionRepositoryFacade(krepo);
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        assertNull(agr.getCreationDate());

        // When
        repo.doDelete(agr);

        // Then
        verify(agr).setCreationDate(any(DateTime.class));
        verify(agr).setModificationDate(any(DateTime.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithNoModificationDateAndVersionNull_shouldSetModificationDateOnDelete() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryFacade repo = new ActionRepositoryFacade(krepo);
        final DateTime t = DateTime.now();
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        doReturn(t).when(agr).getCreationDate();
        doReturn(null).when(agr).getVersion();

        // When
        repo.doDelete(agr);

        // Then
        verify(agr).setModificationDate(t);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithNoModificationDateAndVersion_shouldSetModificationDateOnDelete() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryFacade repo = new ActionRepositoryFacade(krepo);
        final DateTime t = DateTime.now().minusDays(1);
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        doReturn(t).when(agr).getCreationDate();
        doReturn(1L).when(agr).getVersion();

        // When
        repo.doDelete(agr);

        // Then
        verify(agr).setModificationDate((DateTime) gt(t));
    }

    // ------------------------------------------------------------------------
    // combined
    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithNotUpdatedModificationDate_shouldUpdateModificationDateOnSave() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryFacade repo = new ActionRepositoryFacade(krepo);
        final DateTime t = DateTime.now().minusDays(1);
        final KasperID agrId = DefaultKasperId.random();
        doReturn(agrId).when(agr).getEntityId();
        doReturn(t).when(agr).getCreationDate();
        doReturn(t).when(agr).getModificationDate();
        doReturn(agr).when(krepo).doLoad(anyObject(), any(Long.class));

        // When
        repo.doLoad(agrId, 0L);
        repo.doSave(agr);

        // Then
        verify(agr).setModificationDate((DateTime) gt(t));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithNotUpdatedModificationDate_shouldUpdateModificationDateOnDelete() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryFacade repo = new ActionRepositoryFacade(krepo);
        final DateTime t = DateTime.now().minusDays(1);
        final KasperID agrId = DefaultKasperId.random();
        doReturn(t).when(agr).getCreationDate();
        doReturn(t).when(agr).getModificationDate();
        doReturn(agrId).when(agr).getEntityId();
        doReturn(agr).when(krepo).doLoad(anyObject(), any(Long.class));

        // When
        repo.doLoad(agrId, 0L);
        repo.doDelete(agr);

        // Then
        verify(agr).setModificationDate((DateTime) gt(t));
    }


}
