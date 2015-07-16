// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.root.listeners;

import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.EventListener;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.test.applications.events.MemberHasDeclaredToBeFanOfAnApplicationEvent;

public class NewFanOfAnApplicationEventListener extends EventListener<MemberHasDeclaredToBeFanOfAnApplicationEvent> {

    @Override
    public EventResponse handle(Context context, MemberHasDeclaredToBeFanOfAnApplicationEvent event) {
        return EventResponse.success();
    }
}
