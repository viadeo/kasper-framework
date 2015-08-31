// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.validator;

import com.viadeo.kasper.api.component.command.Command;

public interface KasperFixtureEventResultValidator<VALIDATOR extends KasperFixtureEventResultValidator> {

    VALIDATOR expectEventNotificationOn(Class... eventListenerClasses);

    VALIDATOR expectZeroEventNotification();

    VALIDATOR expectExactSequenceOfCommands(Command... commands);
}
