package com.viadeo.kasper.core.id;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

public class ImmutablePair<T1, T2> {

    public static <T1, T2> ImmutablePair<T1, T2> of(T1 first, T2 second) {
        return new ImmutablePair<>(first, second);
    }

    public final T1 first;
    public final T2 second;

    private ImmutablePair(T1 first, T2 second) {
        this.first = checkNotNull(first);
        this.second = checkNotNull(second);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) o;
        return Objects.equal(first, that.first)
                && Objects.equal(second, that.second);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
                first
                , second
        );
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
