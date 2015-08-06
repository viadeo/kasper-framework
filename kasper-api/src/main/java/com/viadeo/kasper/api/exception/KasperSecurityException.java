// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.exception;

import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;

public class KasperSecurityException extends KasperException {
    private static final long serialVersionUID = 4329882967999237383L;
    private final KasperReason kasperReason;

    // ------------------------------------------------------------------------
    public KasperSecurityException(final String message, final CoreReasonCode coreReasonCode) {
        super(message);
        kasperReason = new KasperReason(coreReasonCode, message);
    }

    public KasperSecurityException(final String message, final Throwable cause, final CoreReasonCode coreReasonCode) {
        super(message, cause);
        kasperReason = new KasperReason(coreReasonCode, message);
    }

    public KasperReason getKasperReason() {
        return kasperReason;
    }
}