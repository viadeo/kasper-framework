// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.interceptor;

import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.cqrs.command.Command;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.Event;

@XKasperUnregistered
class TestDomain implements Domain {
}

@XKasperUnregistered
class TestEvent implements Event {
}

@XKasperUnregistered
class TestCommand implements Command {
}

@XKasperUnregistered
class TestQuery implements Query {
}

@XKasperUnregistered
class TestQueryResult implements QueryResult {
}
