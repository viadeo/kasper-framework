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
