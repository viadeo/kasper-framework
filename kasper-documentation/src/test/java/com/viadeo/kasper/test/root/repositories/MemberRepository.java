// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.root.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperRepository;
import com.viadeo.kasper.core.component.command.repository.Repository;
import com.viadeo.kasper.test.root.entities.Member;

@XKasperRepository
public class MemberRepository extends Repository<Member> {

    @Override
    protected Optional<Member> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSave(Member aggregate) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDelete(Member aggregate) {
        throw new UnsupportedOperationException();
    }
}
