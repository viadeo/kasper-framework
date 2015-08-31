// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.root.api.event;

import com.viadeo.kasper.api.annotation.XKasperEvent;
import com.viadeo.kasper.api.component.event.EntityCreatedEvent;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.domain.sample.root.api.Facebook;

@XKasperEvent(action = "created")
public class MemberCreatedEvent extends EntityCreatedEvent<Facebook> {
	private static final long serialVersionUID = -3530058587014151484L;

    protected MemberCreatedEvent(KasperID id) {
        super(id);
    }
}
