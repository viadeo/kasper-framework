package com.viadeo.kasper.core.ids;

import com.viadeo.kasper.api.Format;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractConverter implements Converter {

    private final String vendor;
    private final Format source;
    private final Format target;

    public AbstractConverter(String vendor, Format source, Format target) {
        this.vendor = checkNotNull(vendor);
        this.source = checkNotNull(source);
        this.target = checkNotNull(target);
    }

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
