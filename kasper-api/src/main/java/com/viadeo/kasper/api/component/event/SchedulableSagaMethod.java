// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.component.event;

import org.joda.time.DateTime;

/**
 * The <code>SchedulableSagaMethod</code> interface is implemented in order to provide the scheduled date required by the invocation of a <code>Saga</code> method.
 */
public interface SchedulableSagaMethod extends Event {

    /**
     * @return the scheduled date for which a saga method will be invoked.
     */
    DateTime getScheduledDate();

}
