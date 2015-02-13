package com.viadeo.kasper.event;

import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.KasperResponse;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventResponse extends KasperResponse {

    public static EventResponse rollback(final KasperReason reason) {
        return new EventResponse(Status.ROLLBACK, checkNotNull(reason));
    }

    public static EventResponse rejected(final KasperReason reason) {
        return new EventResponse(Status.REJECTED, reason);
    }

    public static EventResponse ignored() {
        return new EventResponse(Status.IGNORED, null);
    }

    public static EventResponse success() {
        return new EventResponse(Status.SUCCESS, null);
    }

    public EventResponse(final Status status, final KasperReason reason) {
        super(status, reason);
    }
}
