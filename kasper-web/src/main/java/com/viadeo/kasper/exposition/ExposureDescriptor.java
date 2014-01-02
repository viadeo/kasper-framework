// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition;

import com.google.common.base.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class ExposureDescriptor<INPUT, HANDLER> {
    private final Class<? extends INPUT> input;
    private final Class<? extends HANDLER> handler;

    public ExposureDescriptor(Class<? extends INPUT> input, Class<? extends HANDLER> handler) {
        this.input = checkNotNull(input);
        this.handler = checkNotNull(handler);
    }

    public Class<? extends INPUT> getInput() {
        return input;
    }

    public Class<? extends HANDLER> getHandler() {
        return handler;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(input, handler);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ExposureDescriptor other = (ExposureDescriptor) obj;
        return Objects.equal(this.input, other.input) && Objects.equal(this.handler, other.handler);
    }
}
