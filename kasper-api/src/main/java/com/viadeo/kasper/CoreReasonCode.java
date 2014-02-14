// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import static com.google.common.base.Preconditions.checkState;

public enum CoreReasonCode {

    /* Unknown reason */
    UNKNOWN_REASON(0),

    /* Input reasons */
    REQUIRED_INPUT(1001),
    INVALID_INPUT(1002),
    TOO_MANY_ENTRIES(1003),
    CONFLICT(1004),
    INVALID_ID(1005),
    NOT_FOUND(1006),

    /* Security reasons */
    REQUIRE_AUTHENTICATION(2001),
    REQUIRE_AUTHORIZATION(2002),

    /* Internal reasons */
    INTERNAL_COMPONENT_TIMEOUT(3001),
    INTERNAL_COMPONENT_ERROR(3002);

    private static final String CODE_FORMAT = "%04d";

    // ------------------------------------------------------------------------

    private int code;
    private KasperReason reason;

    // ------------------------------------------------------------------------

    CoreReasonCode(final int code) {
        checkState(code < 10000, "Code must be high than 9999");
        this.code = code;
        this.reason = new KasperReason(String.valueOf(this.code), this.name());
    }

    // ------------------------------------------------------------------------

    public KasperReason reason() {
        return this.reason;
    }

    public KasperReason.Builder builder() {
        return KasperReason.Builder.from(reason);
    }

    public String string() {
        return this.toString();
    }

    public int code() {
        return this.code;
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return toString(this.code, this.name());
    }

    public static String toString(final Integer code, final String label) {
         return new StringBuffer()
                    .append(String.format("["+CODE_FORMAT+"]", code))
                    .append(" - ")
                    .append(label)
                .toString();
    }

    public boolean equals(final String other) {
        return this.toString().contentEquals(other);
    }

}
