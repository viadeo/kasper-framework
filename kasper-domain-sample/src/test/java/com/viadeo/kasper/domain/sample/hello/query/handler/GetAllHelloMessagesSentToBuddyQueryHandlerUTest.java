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
import org.mockito.runners.MockitoJUnitRunner;

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
