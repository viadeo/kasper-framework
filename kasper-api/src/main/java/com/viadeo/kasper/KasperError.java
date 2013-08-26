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
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class KasperError implements Serializable, Immutable {
    private static final long serialVersionUID = 7839349411722371919L;
    
    private final String code;
    private final List<String> messages;

    // ------------------------------------------------------------------------

    public KasperError(final String code, final String message) {
        this.code = checkNotNull(code);
        this.messages = new ImmutableList.Builder<String>().add(message).build();
    }

    public KasperError(final String code, final String...messages) {
        this.code = checkNotNull(code);
        this.messages = ImmutableList.copyOf(messages);
    }
    
    public KasperError(final String code, final Collection<String> messages) {
        this.code = checkNotNull(code);
        this.messages = ImmutableList.copyOf(messages);
    }

    public KasperError(final CoreErrorCode code, final String message) {
        this(checkNotNull(code).toString(), message);
    }

    public KasperError(final CoreErrorCode code, final String...messages) {
        this(checkNotNull(code).toString(), messages);
    }

    public KasperError(final CoreErrorCode code, final Collection<String> messages) {
        this(checkNotNull(code).toString(), messages);
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
        if (!getClass().equals(obj.getClass())) {
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
