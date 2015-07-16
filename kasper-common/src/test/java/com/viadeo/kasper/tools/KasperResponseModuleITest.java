package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.component.event.EventResponse;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class KasperResponseModuleITest {

    private final ObjectMapper mapper = ObjectMapperProvider.INSTANCE.mapper();

    @Test
    public void serde_withException_isOk() throws IOException {
        // Given
        KasperReason reason = new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, new RuntimeException("blabla"));

        // When
        String json = mapper.writeValueAsString(reason);
        KasperReason actualReason = mapper.readValue(json, KasperReason.class);

        // Then
        assertEquals(reason, actualReason);
    }

    @Test
    public void serde_withEventResponse_isOk() throws IOException {
        // Given
        KasperReason reason = new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "blabla");
        KasperResponse response = new EventResponse(KasperResponse.Status.FAILURE, reason);

        // When
        String json = mapper.writeValueAsString(response);
        KasperResponse actualResponse = mapper.readValue(json, EventResponse.class);

        // Then
        assertEquals(response, actualResponse);
    }

    @Test
    public void serde_withCommandResponse_isOk() throws IOException {
        // Given
        KasperReason reason = new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "blabla");
        KasperResponse response = new CommandResponse(KasperResponse.Status.FAILURE, reason);

        // When
        String json = mapper.writeValueAsString(response);
        KasperResponse actualResponse = mapper.readValue(json, CommandResponse.class);

        // Then
        assertEquals(response, actualResponse);
    }

    @Test
    public void serde_withQueryResponse_isOk() throws IOException {
        // Given
        KasperReason reason = new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, "blabla");
        KasperResponse response = new QueryResponse(KasperResponse.Status.FAILURE, reason);

        // When
        String json = mapper.writeValueAsString(response);
        KasperResponse actualResponse = mapper.readValue(json, QueryResponse.class);

        // Then
        assertEquals(response, actualResponse);
    }
}
