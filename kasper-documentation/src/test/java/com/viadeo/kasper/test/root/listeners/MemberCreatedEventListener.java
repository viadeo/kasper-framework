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
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.test.root.Facebook;
import com.viadeo.kasper.test.root.events.MemberCreatedEvent;

@XKasperEventListener( domain = Facebook.class )
public class MemberCreatedEventListener extends EventListener<MemberCreatedEvent> {

    @Override
    public EventResponse handle(Context context, MemberCreatedEvent event) {
        return EventResponse.success();
    }
}
