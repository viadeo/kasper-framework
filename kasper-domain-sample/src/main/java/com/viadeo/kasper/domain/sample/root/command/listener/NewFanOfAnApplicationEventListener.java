// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.root.command.listener;

import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.AutowiredEventListener;
import com.viadeo.kasper.domain.sample.applications.api.event.MemberHasDeclaredToBeFanOfAnApplicationEvent;

public class NewFanOfAnApplicationEventListener extends AutowiredEventListener<MemberHasDeclaredToBeFanOfAnApplicationEvent> {

    @Override
    public EventResponse handle(Context context, MemberHasDeclaredToBeFanOfAnApplicationEvent event) {
        return EventResponse.success();
    }
}
