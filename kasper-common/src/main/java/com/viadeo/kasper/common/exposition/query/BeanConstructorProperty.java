// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition.query;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

class BeanConstructorProperty {

    private final int position;
    @SuppressWarnings("unused")
    private final Annotation[] annotations;
    private final String name;
    private final Type type;

    // ------------------------------------------------------------------------

    public BeanConstructorProperty(final int position, final Annotation[] annotations,
                                   final String name, final Type type) {
        this.position = position;
        this.annotations = Arrays.copyOf(checkNotNull(annotations), annotations.length);;
        this.name = checkNotNull(name);
        this.type = checkNotNull(type);
    }

    // ------------------------------------------------------------------------

    public int position() {
        return position;
    }

    public Annotation[] annotations() {
        return annotations;
    }

    public String name() {
        return name;
    }

    public Type type() {
        return type;
    }

}
