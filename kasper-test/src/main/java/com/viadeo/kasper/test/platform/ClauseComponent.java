// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class ClauseComponent<CLAUSE extends Clause> extends Clause<CLAUSE> {

    CLAUSE clause;

    public final CLAUSE and;

    ClauseComponent(final CLAUSE clause) {
        super(clause.fixture);
        this.clause = checkNotNull(clause);
        this.and = this.clause;
    }

    public CLAUSE and() {
        return clause;
    }

    public boolean apply() {
        throw new UnsupportedOperationException("apply cannot be called on a clause component");
    }

}
