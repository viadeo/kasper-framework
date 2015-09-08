package com.viadeo.kasper.core.component.event.eventbus;

import com.google.common.base.Optional;

public interface WithSource<E> {
    Optional<E> getSource();
}
