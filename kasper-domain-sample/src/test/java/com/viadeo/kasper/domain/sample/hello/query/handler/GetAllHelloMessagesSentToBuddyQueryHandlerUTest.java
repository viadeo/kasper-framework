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
package com.viadeo.kasper.domain.sample.hello.query.handler;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.id.DefaultKasperId;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.domain.sample.hello.api.query.GetAllHelloMessagesSentToBuddyQuery;
import com.viadeo.kasper.domain.sample.hello.api.query.results.HelloMessageResult;
import com.viadeo.kasper.domain.sample.hello.api.query.results.HelloMessagesResult;
import com.viadeo.kasper.domain.sample.hello.common.db.KeyValueStore;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @see GetAllHelloMessagesSentToBuddyQueryHandler
 */
@RunWith(MockitoJUnitRunner.class)
public class GetAllHelloMessagesSentToBuddyQueryHandlerUTest {

    private static final String BUDDY = "buddy";

    @InjectMocks
    private GetAllHelloMessagesSentToBuddyQueryHandler handler;

    @Mock
    private KeyValueStore keyValueStore;

    @Test
    public void retrieve_withoutHelloMessage_shouldReturnEmptyResult() {
        // Given
        final GetAllHelloMessagesSentToBuddyQuery query = new GetAllHelloMessagesSentToBuddyQuery(BUDDY);
        doReturn(false).when(keyValueStore).has(BUDDY);

        // When
        final QueryResponse<HelloMessagesResult> response = handler.handle(query);

        // Then
        assertTrue(response.getResult().getList().isEmpty());
        verify(keyValueStore).has(BUDDY);
        verifyNoMoreInteractions(keyValueStore);
    }

    @Test
    public void retrieve_withHelloMessage_shouldReturnExistingHelloMessage() {
        // Given
        final GetAllHelloMessagesSentToBuddyQuery query = new GetAllHelloMessagesSentToBuddyQuery(BUDDY);

        doReturn(true).when(keyValueStore).has(BUDDY);
        final HelloMessageResult message = new HelloMessageResult(DefaultKasperId.random(), 1L, new DateTime(), "hello!");
        final Map<KasperID, HelloMessageResult> messagesById = new HashMap<>();
        messagesById.put(new DefaultKasperId(), message);
        doReturn(Optional.of(messagesById)).when(keyValueStore).get(BUDDY);

        // When
        final QueryResponse<HelloMessagesResult> response = handler.handle(query);

        // Then
        assertEquals(1, response.getResult().getList().size());
        assertSame(message, response.getResult().getList().iterator().next());
    }

}
