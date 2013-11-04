// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.viadeo.kasper.annotation.Immutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public final class KasperReason implements Serializable, Immutable {
    private static final long serialVersionUID = 7839349411722371919L;

    private final UUID id;
    private final String code;
    private final List<String> messages;

    // ------------------------------------------------------------------------

    public static final class Builder {

        private String code = CoreReasonCode.UNKNOWN_REASON.string();
        private Collection<String> messages;

        // -----

        public static Builder empty() {
            return new Builder();
        }

        public static Builder from(final String code) {
            return from(code, new String[0]);
        }

        public static Builder from(final String code, final Collection<String> messages) {
            return from(code, messages.toArray(new String[1]));
        }

        public static Builder from(final String code, final String...messages) {
            final Builder builder = new Builder();
            builder.code = checkNotNull(code);
            builder.messages = Lists.newArrayList(messages);
            return builder;
        }

        public static Builder from(final KasperReason reason) {
            checkNotNull(reason);
            return from(reason.getCode(), reason.getMessages());
        }

        public static Builder from(final CoreReasonCode code, final Collection<String> messages) {
            return from(code.string(), messages.toArray(new String[0]));
        }

        public static Builder from(final CoreReasonCode code, final String...messages) {
            return from(code.string(), messages);
        }

        // -----

        public KasperReason build() {
            if (null == messages) {
                this.messages = Lists.newArrayList();
            }
            return new KasperReason(code, messages);
        }

        // -----

        public Builder code(final String code) {
            this.code = checkNotNull(code);
            return this;
        }

        public Builder code(final CoreReasonCode code) {
            this.code = checkNotNull(code).toString();
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

        public Builder reason(final KasperReason reason) {
            this.code = reason.code;
            this.messages = reason.messages;
            return this;
        }

    }

    // ------------------------------------------------------------------------

    public KasperReason(final String code, final String...messages) {
        this.id = UUID.randomUUID();
        this.code = checkNotNull(code);
        this.messages = ImmutableList.copyOf(checkNotNull(messages));
    }

    public KasperReason(final String code, final Collection<String> messages) {
        this.id = UUID.randomUUID();
        this.code = checkNotNull(code);
        this.messages = ImmutableList.copyOf(checkNotNull(messages));
    }

    public KasperReason(final UUID id, final String code, final Collection<String> messages) {
        this.id = checkNotNull(id);
        this.code = checkNotNull(code);
        this.messages = ImmutableList.copyOf(checkNotNull(messages));
    }

    public KasperReason(final CoreReasonCode code, final String message) {
        this(checkNotNull(code).toString(), checkNotNull(message));
    }

    public KasperReason(final CoreReasonCode code, final String...messages) {
        this(checkNotNull(code).toString(), checkNotNull(messages));
    }

    public KasperReason(final CoreReasonCode code, final Collection<String> messages) {
        this(checkNotNull(code).toString(), checkNotNull(messages));
    }

    // ------------------------------------------------------------------------

    public UUID getId() {
        return this.id;
    }

    public String getCode() {
        return code;
    }

    public Collection<String> getMessages() {
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
        if (this == checkNotNull(obj)) {
            return true;
        }
        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        final KasperReason other = (KasperReason) obj;

        return Objects.equal(this.code, other.code) &&
               Objects.equal(this.messages, other.messages);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(this.id)
                .addValue(this.code)
                .addValue(this.messages)
                .toString();
    }

}
