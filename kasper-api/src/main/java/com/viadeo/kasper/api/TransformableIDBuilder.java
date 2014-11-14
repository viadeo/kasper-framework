package com.viadeo.kasper.api;

import static com.google.common.base.Preconditions.checkNotNull;

public class TransformableIDBuilder extends SimpleIDBuilder {

    private final IDTransformer transformer;

    public TransformableIDBuilder(final IDTransformer transformer, final Format... formats) {
        super(formats);
        this.transformer = checkNotNull(transformer);
    }

    @Override
    public ID build(String urn) {
        ID id = super.build(urn);
        id.setIDTransformer(transformer);
        return id;
    }
}
