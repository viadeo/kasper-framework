package com.viadeo.kasper.test.timelines.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.test.timelines.entities.Timeline;

@XKasperRepository
public class TimelineRepository extends Repository<Timeline> {

    @Override
    protected Optional<Timeline> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSave(Timeline aggregate) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDelete(Timeline aggregate) {
        throw new UnsupportedOperationException();
    }

}
