// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.eventhandling.EventBus;

public interface KasperFixture<EXECUTOR extends KasperFixtureExecutor> {

    EXECUTOR given();

    // ------------------------------------------------------------------------

    public CommandBus commandBus();

    public EventBus eventBus();

}