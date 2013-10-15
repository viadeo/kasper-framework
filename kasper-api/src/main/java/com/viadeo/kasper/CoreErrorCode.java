// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

public enum CoreErrorCode {

    /* Unknown error */
    UNKNOWN_ERROR(0),

    /* Input errors */
    REQUIRED_INPUT(1001),
    INVALID_INPUT(1002),
    TOO_MANY_ENTRIES(1003),
    CONFLICT(1004),
    INVALID_ID(1005),
    NOT_FOUND(1006),

    /* Security errors */
    REQUIRE_AUTHENTICATION(2001),
    REQUIRE_AUTHORIZATION(2002),

    /* Internal errors */
    INTERNAL_COMPONENT_TIMEOUT(3001),
    INTERNAL_COMPONENT_ERROR(3002);

    // ------------------------------------------------------------------------

    private int code;
    private KasperError error;

    // ------------------------------------------------------------------------

    CoreErrorCode(final int code) {
        this.code = code;
        this.error = new KasperError(String.valueOf(this.code), this.name());
    }

    // ------------------------------------------------------------------------

    public KasperError error() {
        return this.error;
    }

    public KasperError.Builder builder() {
        return KasperError.Builder.from(error);
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
        return new StringBuffer()
                    .append(String.format("[%04d]", this.code))
                    .append(" - ")
                    .append(this.name())
                .toString();
    }

}
