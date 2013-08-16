// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

class BeanConstructorProperty {
    private final int position;
    @SuppressWarnings("unused")
    private final Annotation[] annotations;
    private final String name;
    private final Type type;

    public BeanConstructorProperty(final int position, final Annotation[] annotations,
                                   final String name, final Type type) {
        this.position = position;
        this.annotations = annotations;
        this.name = name;
        this.type = type;
    }

    public int position() {
        return position;
    }

    public Annotation[] annotations(){
        return annotations;
    }

    public String name() {
        return name;
    }

    public Type type() {
        return type;
    }
}
