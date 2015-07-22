// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.root.command.listener;

import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.domain.sample.root.api.Facebook;
import com.viadeo.kasper.domain.sample.root.api.event.MemberCreatedEvent;

@XKasperEventListener( domain = Facebook.class )
public class MemberCreatedEventListener extends EventListener<MemberCreatedEvent> {

    @Override
    public EventResponse handle(Context context, MemberCreatedEvent event) {
        return EventResponse.success();
    }
}
