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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.viadeo.kasper.annotation.Immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public final class KasperError implements Serializable, Immutable {
    private static final long serialVersionUID = 7839349411722371919L;
    
    private final String code;
    private final List<String> messages;
    private final KasperError enclosed;

    // ------------------------------------------------------------------------

    public final static class Builder {

        KasperError enclosed = null;
        String code = null;
        List<String> messages;

        // -----

        public static Builder empty() {
            return new Builder();
        }

        public static Builder from(final String code) {
            return from(null, code, new String[0]);
        }

        public static Builder from(final String code, final String...messages) {
            return from(null, code, messages);
        }

        public static Builder from(final KasperError error) {
            return from(checkNotNull(error), CoreErrorCode.UNKNOWN_ERROR, "Unknown error");
        }

        public static Builder from(final KasperError error, final CoreErrorCode code, final String...messages) {
            return from(checkNotNull(error), code.toString(), messages);
        }

        public static Builder from(final KasperError error, final String code, final String...messages) {
            final Builder builder = new Builder();
            builder.enclosed = error;
            builder.code = checkNotNull(code);
            builder.messages = Lists.newArrayList(messages);
            return builder;
        }

        // -----

        public KasperError build() {
            return new KasperError(enclosed, code, messages);
        }

        // -----

        public Builder code(final String code) {
            this.code = checkNotNull(code);
            return this;
        }

        public Builder code(final CoreErrorCode code) {
            this.code = checkNotNull(code).toString();
            return this;
        }

        public Builder enclosed(final KasperError enclosed) {
            this.enclosed = checkNotNull(enclosed);
            return this;
        }

        public Builder message(final String message) {
            checkNotNull(message);
            if (null == this.messages) {
                this.messages = Lists.newArrayList();
            }
            this.messages.add(message);
            return this;
        }

        public Builder messages(final Collection<String> messages) {
            this.messages = ImmutableList.copyOf(checkNotNull(messages));
            return this;
        }

        public Builder messages(final String...messages) {
            this.messages = ImmutableList.copyOf(messages);
            return this;
        }

        public Builder error(final KasperError error) {
            this.enclosed = error.enclosed;
            this.code = error.code;
            this.messages = error.messages;
            return this;
        }

    }

    // ------------------------------------------------------------------------

    public KasperError(final String code, final String...messages) {
        this.enclosed = null;
        this.code = checkNotNull(code);
        this.messages = ImmutableList.copyOf(checkNotNull(messages));
    }

    public KasperError(final KasperError enclosed,final String code, final String...messages) {
        this.enclosed= enclosed;
        this.code = checkNotNull(code);
        this.messages = ImmutableList.copyOf(checkNotNull(messages));
    }
    
    public KasperError(final String code, final Collection<String> messages) {
        this.enclosed = null;
        this.code = checkNotNull(code);
        this.messages = ImmutableList.copyOf(checkNotNull(messages));
    }

    public KasperError(final KasperError enclosed, final String code, final Collection<String> messages) {
        this.enclosed = enclosed;
        this.code = checkNotNull(code);
        this.messages = ImmutableList.copyOf(checkNotNull(messages));
    }

    public KasperError(final CoreErrorCode code, final String message) {
        this(checkNotNull(code).toString(), checkNotNull(message));
    }

    public KasperError(final KasperError enclosed, final CoreErrorCode code, final String message) {
        this(enclosed,checkNotNull(code).toString(), checkNotNull(message));
    }

    public KasperError(final CoreErrorCode code, final String...messages) {
        this(checkNotNull(code).toString(), checkNotNull(messages));
    }

    public KasperError(final CoreErrorCode code, final Collection<String> messages) {
        this(checkNotNull(code).toString(), checkNotNull(messages));
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

    public boolean isEnclosing() {
        return (null != this.enclosed);
    }

    public KasperError getEnclosed() {
        return this.enclosed;
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
