package com.viadeo.kasper.api.domain.event;

import com.viadeo.kasper.api.domain.response.CoreReasonCode;
import com.viadeo.kasper.api.domain.response.KasperReason;
import com.viadeo.kasper.api.domain.event.EventResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class EventResponseUTest {

    @Test
    public void success_isNotAnError_isNotAFailure_isNotTemporary() {
        EventResponse response = EventResponse.success();
        assertNotNull(response);
        assertTrue(response.isOK());
        assertFalse(response.isAnError());
        assertFalse(response.isAFailure());
        assertFalse(response.isTemporary());
    }

    @Test
    public void failure_isNotATemporary() {
        EventResponse response = EventResponse.failure(new KasperReason(CoreReasonCode.CONFLICT, "Fake message"));
        assertNotNull(response);
        assertFalse(response.isOK());
        assertFalse(response.isAnError());
        assertTrue(response.isAFailure());
        assertFalse(response.isTemporary());
    }

    @Test
    public void temporarilyUnavailable_isATemporaryFailure() {
        EventResponse response = EventResponse.temporarilyUnavailable(new KasperReason(CoreReasonCode.CONFLICT, "Fake message"));
        assertNotNull(response);
        assertFalse(response.isOK());
        assertFalse(response.isAnError());
        assertTrue(response.isAFailure());
        assertTrue(response.isTemporary());
    }

    @Test
    public void error_isNotTemporary() {
        EventResponse response = EventResponse.error(new KasperReason(CoreReasonCode.CONFLICT, "Fake message"));
        assertNotNull(response);
        assertFalse(response.isOK());
        assertTrue(response.isAnError());
        assertFalse(response.isAFailure());
        assertFalse(response.isTemporary());
    }
}
