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
package com.viadeo.kasper.exposition.http;

import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.component.Handler;
import com.viadeo.kasper.platform.Meta;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class HttpExposerUTest {

    private HttpExposer<Object,Handler, KasperResponse> exposer;

    @Before
    public void setUp() throws Exception {
        exposer = new HttpExposer<Object, Handler, KasperResponse>(mock(HttpContextDeserializer.class), mock(Meta.class)) {

            @Override
            protected KasperResponse createErrorResponse(CoreReasonCode code, List<String> reasons) {
                return null;
            }

            @Override
            protected KasperResponse createRefusedResponse(CoreReasonCode code, List<String> reasons) {
                return null;
            }

            @Override
            public KasperResponse doHandle(Object o, Context context) throws Exception {
                return null;
            }

            @Override
            protected String toPath(Class<?> exposedInput) {
                return null;
            }
        };
    }

    @Test
    public void getStatusFrom_withOkAsResponse_returnOk200() {
        // Given
        KasperResponse response = createKasperResponse(KasperResponse.Status.OK);

        // When
        Response.Status status = exposer.getStatusFrom(response);

        // Then
        assertNotNull(status);
        assertEquals(Response.Status.OK, status);
    }

    @Test
    public void getStatusFrom_withAcceptedAsResponse_returnAccepted202() {
        // Given
        KasperResponse response = createKasperResponse(
                KasperResponse.Status.ACCEPTED,
                new KasperReason.Builder().message("accepted for test").build()
        );

        // When
        Response.Status status = exposer.getStatusFrom(response);

        // Then
        assertNotNull(status);
        assertEquals(Response.Status.ACCEPTED, status);
    }

    @Test
    public void getStatusFrom_withRefusedAsResponse_withRequiredInputAsMappedKasperCode_returnBadRequest400() {
        // Given
        KasperResponse response = createKasperResponse(
                KasperResponse.Status.REFUSED,
                new KasperReason(CoreReasonCode.REQUIRED_INPUT)
        );

        // When
        Response.Status status = exposer.getStatusFrom(response);

        // Then
        assertNotNull(status);
        assertEquals(Response.Status.BAD_REQUEST, status);
    }

    @Test
    public void getStatusFrom_withRefusedAsResponse_withoutMappedKasperCode_returnInternalError500() {
        // Given
        KasperResponse response = createKasperResponse(
                KasperResponse.Status.REFUSED,
                new KasperReason("fake error")
        );

        // When
        Response.Status status = exposer.getStatusFrom(response);

        // Then
        assertNotNull(status);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, status);
    }

    @Test
    public void getStatusFrom_withErrorAsResponse_withRequiredInputAsMappedKasperCode_returnBadRequest400() {
        // Given
        KasperResponse response = createKasperResponse(
                KasperResponse.Status.ERROR,
                new KasperReason(CoreReasonCode.REQUIRED_INPUT)
        );

        // When
        Response.Status status = exposer.getStatusFrom(response);

        // Then
        assertNotNull(status);
        assertEquals(Response.Status.BAD_REQUEST, status);
    }

    @Test
    public void getStatusFrom_withErrorAsResponse_withoutMappedKasperCode_returnInternalError500() {
        // Given
        KasperResponse response = createKasperResponse(
                KasperResponse.Status.ERROR,
                new KasperReason("fake error")
        );

        // When
        Response.Status status = exposer.getStatusFrom(response);

        // Then
        assertNotNull(status);
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR, status);
    }

    @Test
    public void getPayloadAsString_shouldReturnRequestBodyAsString() throws IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("application/json");
        String body = "{\"test\":\"toto\"}";
        request.setContent(body.getBytes());

        // when
        String objectToLogForDebug = exposer.getPayloadAsString(request);

        // then
        assertEquals(body, objectToLogForDebug);
    }

    @Test
    public void getPayloadAsString_whenNoContent_shouldReturnEmptyString() throws IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.setContentType("application/json");
        String body = "";
        request.setContent(body.getBytes());

        // when
        String objectToLogForDebug = exposer.getPayloadAsString(request);

        // then
        assertEquals("", objectToLogForDebug);
    }

    private static KasperResponse createKasperResponse(KasperResponse.Status status) {
        return createKasperResponse(status, null);
    }

    private static KasperResponse createKasperResponse(KasperResponse.Status status, KasperReason reason) {
        return new KasperResponse(status, reason);
    }
}
