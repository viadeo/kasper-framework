package com.viadeo.kasper.query.exposition;

import com.google.common.reflect.TypeToken;

import java.lang.annotation.Annotation;

public class BeanProperty {
    private final String name;
    private final Class<?> declaringClass;
    private final Annotation[] annotations;
    private final TypeToken<?> typeToken;
    
    public BeanProperty(String name, Class<?> declaringClass, Annotation[] annotations, TypeToken<?> typeToken) {
        super();
        this.name = name;
        this.declaringClass = declaringClass;
        this.annotations = annotations;
        this.typeToken = typeToken;
    }

    public String getName() {
        return name;
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public Annotation[] getAnnotations() {
        return annotations;
    }

    public TypeToken<?> getTypeToken() {
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
        if (!getClass().equals(obj.getClass())) {
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
