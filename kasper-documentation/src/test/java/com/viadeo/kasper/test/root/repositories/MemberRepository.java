package com.viadeo.kasper.test.root.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.ddd.repository.Repository;
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
