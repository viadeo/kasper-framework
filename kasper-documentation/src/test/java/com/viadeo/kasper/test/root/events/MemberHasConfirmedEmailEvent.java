// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.root.events;

import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.component.event.Event;

public class MemberHasConfirmedEmailEvent implements Event {

    private final KasperID id;

    protected MemberHasConfirmedEmailEvent(final KasperID id) {
        this.id = id;
    }

    public KasperID getId() {
        return id;
    }
}
