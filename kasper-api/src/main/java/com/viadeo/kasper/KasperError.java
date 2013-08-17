// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

public final class KasperError implements Serializable {

    public static final String REQUIRED_INPUT = "REQUIRED_INPUT";
    public static final String INVALID_INPUT = "INVALID_INPUT";
    public static final String TOO_MANY_ENTRIES = "TOO_MANY_ENTRIES";
    public static final String CONFLICT = "CONFLICT";
    public static final String REQUIRE_AUTHENTICATION = "REQUIRE_AUTHENTICATION";
    public static final String REQUIRE_AUTHORIZATION = "REQUIRE_AUTHORIZATION";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
    public static final String INTERNAL_COMPONENT_TIMEOUT = "INTERNAL_COMPONENT_TIMEOUT";
    public static final String INTERNAL_COMPONENT_ERROR = "INTERNAL_COMPONENT_ERROR";
    public static final String INVALID_ID = "INVALID_ID";
    
    private final String code;
    private final String message;
    private final Optional<String> userMessage;

    // ------------------------------------------------------------------------
    
    public KasperError(final String code, final String message) {
        this(code, message, null);
    }
    
    public KasperError(final String code, final String message, final String userMessage) {
        this.code = checkNotNull(code);
        this.message = checkNotNull(message);
        this.userMessage = Optional.fromNullable(userMessage);
    }

    // ------------------------------------------------------------------------

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Optional<String> getUserMessage() {
        return userMessage;
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return Objects.hashCode(this.code, this.message, this.userMessage);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == Preconditions.checkNotNull(obj)) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final KasperError other = (KasperError) obj;

        return Objects.equal(this.code, other.code) &&
                Objects.equal(this.message, other.message) &&
                 Objects.equal(this.userMessage, other.userMessage);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(this.code)
                .addValue(this.message)
                .addValue(this.userMessage)
                .toString();
    }

}
