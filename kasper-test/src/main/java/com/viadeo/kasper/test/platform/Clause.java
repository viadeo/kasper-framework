// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Clause<CLAUSE extends Clause> implements IPlatformFixture {

    PlatformFixture fixture;

    public final CLAUSE and;

    @SuppressWarnings("uchecked")
    Clause(final PlatformFixture fixture) {
        this.fixture = checkNotNull(fixture);
        this.and = (CLAUSE) this;
    }

    @Override
    public GivenClause given() {
        return this.fixture.given();
    }

    @Override
    public WhenClause when() {
        return this.fixture.when();
    }

    @Override
    public ThenClause then() {
        return this.fixture.then();
    }

    @SuppressWarnings("unchecked")
    public CLAUSE and() {
        return (CLAUSE) this;
    }

    // ------------------------------------------------------------------------

    @Override
    public ThenClause ensure() {
        return this.fixture.ensure();
    }

    // ------------------------------------------------------------------------

    abstract boolean apply();

}
