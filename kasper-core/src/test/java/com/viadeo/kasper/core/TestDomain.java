// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core;

import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.api.component.query.QueryResult;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;

@XKasperUnregistered
public class TestDomain implements Domain {

    @XKasperUnregistered
    public static class TestEvent implements Event {
    }

    @XKasperUnregistered
    public static class TestCommand implements Command {
    }

    @XKasperUnregistered
    public static class TestQuery implements Query {
    }

    @XKasperUnregistered
    public static class TestQueryResult implements QueryResult {
    }
}
