// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id;

import com.viadeo.kasper.api.id.Format;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractConverter implements Converter {

    private final String vendor;
    private final Format source;
    private final Format target;

    // ------------------------------------------------------------------------

    public AbstractConverter(final String vendor, final Format source, final Format target) {
        this.vendor = checkNotNull(vendor);
        this.source = checkNotNull(source);
        this.target = checkNotNull(target);
    }

    // ------------------------------------------------------------------------

    public String getVendor() {
        return vendor;
    }

    @Override
    public Format getSource() {
        return source;
    }

    @Override
    public Format getTarget() {
        return target;
    }

}
