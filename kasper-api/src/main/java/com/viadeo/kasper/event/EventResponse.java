package com.viadeo.kasper.event;

import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.KasperResponse;

public class EventResponse extends KasperResponse {

    /**
     * Get a temporarily unavailable response which is identified as a particular failure.
     *
     * @param reason a reason
     * @return a temporarily unavailable response
     */
    public static EventResponse temporarilyUnavailable(final KasperReason reason) {
        return failure(reason).temporary();
    }

    /**
     * Get an error response which is an expected part of normal operations, are dealt with immediately and the system
     * will continue to operate at the same capacity following an error. For example, an error discovered
     * during input validation that will be communicated to the client as part of normal processing.
     *
     * @param reason a reason
     * @return an event response
     */
    public static EventResponse error(final KasperReason reason) {
        return new EventResponse(Status.ERROR, reason);
    }

    /**
     * Get a failure response which is an unexpected and can require intervention before the system can resume at the
     * same level of operation. This does not mean that failures are always fatal, rather that some capacity of the
     * system will be reduced following a failure.
     *
     * @param reason a reason
     * @return a failure response
     */
    public static EventResponse failure(final KasperReason reason) {
        return new EventResponse(Status.FAILURE, reason);
    }

    /**
     * Get a success response.
     *
     * @return a success response
     */
    public static EventResponse success() {
        return new EventResponse(Status.SUCCESS, null);
    }

    /**
     * @return an ignore response
     * @deprecated prefer to use one of other kind of response
     */
    @Deprecated
    public static EventResponse ignored() {
        return new EventResponse(Status.OK, null);
    }

    private boolean temporary;

    public EventResponse(final Status status, final KasperReason reason) {
        super(status, reason);
    }

    @Override
    public boolean isOK() {
        return getStatus().equals(Status.SUCCESS) || super.isOK();
    }

    public boolean isAnError() {
        return getStatus() == Status.ERROR;
    }

    public boolean isAFailure() {
        return getStatus() == Status.FAILURE;
    }

    private EventResponse temporary() {
        temporary = true;
        return this;
    }

    public boolean isTemporary() {
        return temporary;
    }
}
