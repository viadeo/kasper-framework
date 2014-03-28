// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.google.common.collect.Maps;
import com.viadeo.kasper.CoreReasonCode;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.Response.Status;

public final class CoreReasonHttpCodes {

    private static final Map<String, CoreReasonHttpCodesTranslationTable> STRING_CODES = Maps.newHashMap();

    private CoreReasonHttpCodes() { /* Utility class */ }

    // ------------------------------------------------------------------------

    private static enum CoreReasonHttpCodesTranslationTable {
        UNKNOWN_REASON(CoreReasonCode.UNKNOWN_REASON, Status.INTERNAL_SERVER_ERROR),

        REQUIRED_IPUT(CoreReasonCode.REQUIRED_INPUT, Status.BAD_REQUEST),
        INVALID_INPUT(CoreReasonCode.INVALID_INPUT, Status.BAD_REQUEST),
        TOO_MANY_ENTRIES(CoreReasonCode.TOO_MANY_ENTRIES, Status.BAD_REQUEST),
        CONFLICT(CoreReasonCode.CONFLICT, Status.CONFLICT),
        INVALID_ID(CoreReasonCode.INVALID_ID, Status.BAD_REQUEST),
        NOT_FOUND(CoreReasonCode.NOT_FOUND, Status.NOT_FOUND),
        UNSUPPORTED_MEDIA_TYPE(CoreReasonCode.UNSUPPORTED_MEDIA_TYPE, Status.UNSUPPORTED_MEDIA_TYPE),

        REQUIRE_AUTHENTICATION(CoreReasonCode.REQUIRE_AUTHENTICATION, Status.UNAUTHORIZED),
        REQUIRE_AUTHORIZATION(CoreReasonCode.REQUIRE_AUTHORIZATION, Status.FORBIDDEN),
        INVALID_AUTHENTICATION(CoreReasonCode.INVALID_AUTHENTICATION, Status.BAD_REQUEST),

        INTERNAL_COMPONENT_TIMEOUT(CoreReasonCode.INTERNAL_COMPONENT_TIMEOUT, Status.INTERNAL_SERVER_ERROR),
        INTERNAL_COMPONENT_ERROR(CoreReasonCode.INTERNAL_COMPONENT_ERROR, Status.INTERNAL_SERVER_ERROR);

        //---------------------------------------------------------------------


        private final CoreReasonCode code;
        private final Status httpStatus;

        // --------------------------------------------------------------------

        CoreReasonHttpCodesTranslationTable(final CoreReasonCode code, final Status httpStatus) {
            this.code = code;
            this.httpStatus = httpStatus;
            STRING_CODES.put(code.toString(), this);
        }

        // --------------------------------------------------------------------

        public Status getHttpStatus() {
            return this.httpStatus;
        }

        public CoreReasonCode getCode() {
            return this.code;
        }

        public int getStatusCode() {
            return this.httpStatus.getStatusCode();
        }

        public static CoreReasonHttpCodesTranslationTable fromString(final String stringCode) {
           if (STRING_CODES.containsKey(stringCode)) {
               return STRING_CODES.get(stringCode);
           }
           throw new IllegalArgumentException(stringCode + " is not a valid code code");
        }

    }

    // ------------------------------------------------------------------------

    public static int toStatus(final String code) {
        try {
            final CoreReasonHttpCodesTranslationTable mapping = CoreReasonHttpCodesTranslationTable.fromString(checkNotNull(code));
            return mapping.getStatusCode();
        } catch (final IllegalArgumentException e) {
            return Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }
    }

}
