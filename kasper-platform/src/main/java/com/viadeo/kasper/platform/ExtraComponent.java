// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import com.google.common.base.Objects;

public class ExtraComponent {

    private final Key key;
    private final Object instance;

    public ExtraComponent(String name, Object instance) {
        this(new Key(name, instance.getClass()), instance);
    }

    public ExtraComponent(String name, Class instanceType, Object instance) {
        this(new Key(name, instanceType), instance);
    }

    public ExtraComponent(Key key, Object instance) {
        this.key = key;
        this.instance = instance;
    }

    public Key getKey() {
        return key;
    }

    public Object getInstance() {
        return instance;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key, instance);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ExtraComponent other = (ExtraComponent) obj;
        return Objects.equal(this.key, other.key) && Objects.equal(this.instance, other.instance);
    }

    // --------------------------------------------------------------------

    public static class Key {

        private final String name;
        private final Class clazz;

        public Key(final String name, final Class clazz) {
            this.name = name;
            this.clazz = clazz;
        }

        public Class getClazz() {
            return clazz;
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, clazz);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final Key other = (Key) obj;
            return Objects.equal(this.name, other.name) && Objects.equal(this.clazz, other.clazz);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("name", name)
                    .add("clazz", clazz)
                    .toString();
        }

    }
}
