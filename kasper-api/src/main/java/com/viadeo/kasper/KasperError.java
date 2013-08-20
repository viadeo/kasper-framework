// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.viadeo.kasper.annotation.Immutable;

import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class KasperError implements Serializable, Immutable {

    private static final long serialVersionUID = 7839349411722371919L;
    
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
    private final List<String> messages;

    // ------------------------------------------------------------------------
    
    public KasperError(final String code, final String...messages) {
        this.code = checkNotNull(code);
        this.messages = ImmutableList.copyOf(messages);
    }
    
    public KasperError(final String code, final List<String> messages) {
        this.code = checkNotNull(code);
        this.messages = ImmutableList.copyOf(messages);
    }

    // ------------------------------------------------------------------------

    public String getCode() {
        return code;
    }

    public List<String> getMessages() {
        return messages;
    }
    
    public boolean hasMessage(String message) {
        return messages.contains(message);
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return Objects.hashCode(this.code, this.messages);
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
                Objects.equal(this.messages, other.messages);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(this.code)
                .addValue(this.messages)
                .toString();
    }

}
