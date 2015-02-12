package com.viadeo.kasper.event;

import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.KasperResponse;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventResponse extends KasperResponse {

    public static EventResponse error(final KasperReason reason) {
        return new EventResponse(Status.ERROR, checkNotNull(reason));
    }

    public static EventResponse refused(final KasperReason reason) {
        return new EventResponse(Status.REFUSED, checkNotNull(reason));
    }

    public static EventResponse ok() {
        return new EventResponse();
    }

    public EventResponse() {
        super();
    }

    private EventResponse(final Status status, final KasperReason reason) {
        super(status, reason);
    }
}
