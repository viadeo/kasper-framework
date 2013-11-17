// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.impl;

import com.viadeo.kasper.impl.DefaultKasperId;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ActionRepositoryVersionFacadeTest {

    // ------------------------------------------------------------------------
    // doSave()
    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithVersionNotNull_shouldBeIncrementedOnSave() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryVersionFacade repo = new ActionRepositoryVersionFacade(krepo);
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        doReturn(0L).when(agr).getVersion();

        // When
        repo.doSave(agr);

        // Then
        verify(agr).setVersion(1L);
    }

    // ------------------------------------------------------------------------
    // doLoad()
    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithVersionNull_shouldBeSetToZeroOnLoad() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryVersionFacade repo = new ActionRepositoryVersionFacade(krepo);
        doReturn(agr).when(krepo).doLoad(anyObject(), any(Long.class));
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        doReturn(null).when(agr).getVersion();

        // When
        repo.doLoad(agr.getEntityId(), 0L);

        // Then
        verify(agr).setVersion(0L);
    }

    // ------------------------------------------------------------------------
    // doDelete()
    // ------------------------------------------------------------------------

    @Test
    @SuppressWarnings("unchecked")
    public void testAggregateWithVersionNotNull_shouldBeIncrementedOnDelete() {
        // Given
        final AbstractAggregateRoot agr = mock(AbstractAggregateRoot.class);
        final Repository krepo = mock(Repository.class);
        final ActionRepositoryVersionFacade repo = new ActionRepositoryVersionFacade(krepo);
        doReturn(DefaultKasperId.random()).when(agr).getEntityId();
        doReturn(0L).when(agr).getVersion();

        // When
        repo.doDelete(agr);

        // Then
        verify(agr).setVersion(1L);
    }

}
