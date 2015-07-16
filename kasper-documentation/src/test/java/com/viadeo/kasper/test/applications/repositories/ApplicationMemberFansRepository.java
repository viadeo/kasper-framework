// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.applications.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.test.applications.entities.Member_fanOf_Application;

@XKasperRepository
public class ApplicationMemberFansRepository extends Repository<Member_fanOf_Application> {

    @Override
    protected Optional<Member_fanOf_Application> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSave(Member_fanOf_Application aggregate) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDelete(Member_fanOf_Application aggregate) {
        throw new UnsupportedOperationException();
    }

}
