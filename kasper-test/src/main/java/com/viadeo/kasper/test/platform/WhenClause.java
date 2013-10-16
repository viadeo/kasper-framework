// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.query.Query;

import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class WhenClause extends Clause<WhenClause> {

    private List<Command> commands = Lists.newArrayList();
    private List<QueryWhenClauseComponent<WhenClause>> queries = Lists.newArrayList();

    // ------------------------------------------------------------------------

    WhenClause(final PlatformFixture fixture) {
        super(checkNotNull(fixture));
    }

    // ------------------------------------------------------------------------

    public WhenClause sendCommand(final Command command) {
        this.commands.add(checkNotNull(command));
        return this;

    }

    public QueryWhenClauseComponent<WhenClause> sendQuery(final Query query) {
        final QueryWhenClauseComponent<WhenClause> queryComp = new QueryWhenClauseComponent<>(this, query);
        this.queries.add(queryComp);
        return queryComp;
    }

    // ------------------------------------------------------------------------

    @Override
    boolean apply() {
        // TODO
        return true;
    }

}
