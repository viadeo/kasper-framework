package com.viadeo.kasper.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Client.class)
@SuppressStaticInitializationFor("com.viadeo.kasper.client.KasperClient")
public class KasperClientBuilderUTest {

    private Client client;

    @Before
    public void setUp() throws Exception {
        client = mock(Client.class);

        PowerMockito.mockStatic(Client.class);
        when(Client.create(any(ClientConfig.class))).thenReturn(client);
    }

    @Test
    public void create_twoTimes_withoutSpecifiedClient_shouldAddOnlyOneRetryFilter() {
        // Given
        final KasperClientBuilder builder = new KasperClientBuilder();

        // When
        final KasperClient kasperClient1 = builder.create();
        final KasperClient kasperClient2 = builder.create();

        // Then
        assertNotNull(kasperClient1);
        assertNotNull(kasperClient2);
        assertFalse(kasperClient1 == kasperClient2);

        verify(client).addFilter(any(RetryFilter.class));
        verifyNoMoreInteractions(client);
    }

    @Test
    public void create_twoTimes_withSpecifiedClient_shouldAddNoRetryFilter() {
        // Given
        final KasperClientBuilder builder = new KasperClientBuilder().client(client);
        reset(client);

        // When
        final KasperClient kasperClient1 = builder.create();
        final KasperClient kasperClient2 = builder.create();

        // Then
        assertNotNull(kasperClient1);
        assertNotNull(kasperClient2);
        assertFalse(kasperClient1 == kasperClient2);

        verifyNoMoreInteractions(client);
    }

    @Test
    public void numberOfRetries_withInitializedClient_shouldRemoveOldThenAddNewRetryFilter() {
        // Given
        final KasperClientBuilder builder = new KasperClientBuilder().client(client);
        reset(client);

        // When
        builder.numberOfRetries(2);

        // Then
        verify(client).removeFilter(any(RetryFilter.class));
        verify(client).addFilter(any(RetryFilter.class));
        verifyNoMoreInteractions(client);
    }

    @Test
    public void numberOfRetries_withoutInitializedClient_shouldAddNoRetryFilter() {
        // Given
        final KasperClientBuilder builder = new KasperClientBuilder();

        // When
        builder.numberOfRetries(2);

        // Then
        verifyZeroInteractions(client);
    }

    @Test
    public void client_withClient_shouldAddRetryFilter() {
        // Given
        final KasperClientBuilder builder = new KasperClientBuilder();

        // When
        builder.client(client);

        // Then
        verify(client).isFilterPreset(any(RetryFilter.class));
        verify(client).addFilter(any(RetryFilter.class));
        verifyNoMoreInteractions(client);
    }

    @Test
    public void client_withClient_containingSameInstanceOfRetryFilter_shouldAddNoRetryFilter() {
        // Given
        final KasperClientBuilder builder = new KasperClientBuilder();
        final RetryFilter retryFilter = builder.getOptionalRetryFilter().get();

        when(client.isFilterPreset(retryFilter)).thenReturn(true);

        // When
        builder.client(client);

        // Then
        verify(client).isFilterPreset(any(RetryFilter.class));
        verifyNoMoreInteractions(client);
    }
}
