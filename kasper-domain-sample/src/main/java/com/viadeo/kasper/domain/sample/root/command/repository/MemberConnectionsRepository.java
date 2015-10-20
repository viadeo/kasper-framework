// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.root.command.repository;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.annotation.XKasperRepository;
import com.viadeo.kasper.core.component.command.repository.AutowiredRepository;
import com.viadeo.kasper.domain.sample.root.command.model.entity.Member_connectedTo_Member;

@SuppressWarnings("deprecation")
@XKasperRepository
public class MemberConnectionsRepository extends AutowiredRepository<KasperID,Member_connectedTo_Member> {

    @Override
    protected Optional<Member_connectedTo_Member> doLoad(KasperID aggregateIdentifier, Long expectedVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSave(Member_connectedTo_Member aggregate) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doDelete(Member_connectedTo_Member aggregate) {
        throw new UnsupportedOperationException();
    }
}
