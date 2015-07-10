// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.root.events;

import com.viadeo.kasper.api.documentation.XKasperEvent;

@XKasperEvent(action = "now_connected_to")
public class NewMemberConnectionEvent extends FacebookMemberEvent {
	private static final long serialVersionUID = -4357030340252792722L;

}
