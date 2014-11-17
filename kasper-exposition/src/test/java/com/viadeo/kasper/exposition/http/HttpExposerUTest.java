package com.viadeo.kasper.exposition.http;

import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.KasperResponse;
import com.viadeo.kasper.client.platform.Meta;
import com.viadeo.kasper.context.Context;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class HttpExposerUTest {

    private HttpExposer<Object,KasperResponse> exposer;

    @Before
    public void setUp() throws Exception {
        exposer = new HttpExposer<Object, KasperResponse>(mock(HttpContextDeserializer.class), mock(Meta.class)) {

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

    private static KasperResponse createKasperResponse(KasperResponse.Status status) {
        return createKasperResponse(status, null);
    }

    private static KasperResponse createKasperResponse(KasperResponse.Status status, KasperReason reason) {
        return new KasperResponse(status, reason);
    }
}
