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
package com.viadeo.kasper.common.serde;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.viadeo.kasper.api.component.command.CommandResponse;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.component.query.QueryResponse;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;
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
