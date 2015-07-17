// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.timelines.command.repository;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperRepository;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.domain.sample.timelines.command.model.entity.Status;

@XKasperRepository
public class StatusRepository extends Repository<Status> {

    @Override
    protected Optional<Status> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSave(Status aggregate) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDelete(Status aggregate) {
        throw new UnsupportedOperationException();
    }

}
