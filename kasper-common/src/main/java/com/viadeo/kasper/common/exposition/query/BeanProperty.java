// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition.query;

import com.google.common.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;

public class BeanProperty {

    private final String name;
    private final Class declaringClass;
    private final Annotation[] annotations;
    private final TypeToken typeToken;

    // ------------------------------------------------------------------------

    public BeanProperty(final String name, final Class declaringClass,
                        final Annotation[] annotations, final TypeToken typeToken) {
        super();

        this.name = checkNotNull(name);
        this.declaringClass = checkNotNull(declaringClass);
        this.annotations = Arrays.copyOf(checkNotNull(annotations), annotations.length);
        this.typeToken = checkNotNull(typeToken);
    }

    // ------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public Class getDeclaringClass() {
        return declaringClass;
    }

    public Annotation[] getAnnotations() {
        return Arrays.copyOf(annotations, annotations.length);
    }

    public TypeToken getTypeToken() {
        return typeToken;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {

        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }
        if ( ! getClass().equals(obj.getClass())) {
            return false;
        }

        final BeanProperty other = (BeanProperty) obj;
        if (null == name) {
            if (null != other.name) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        return true;
    }

}
