// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.timelines.command.model.entity;

import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.LinkedConcept;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperConcept;
import com.viadeo.kasper.domain.sample.root.command.model.entity.Member;
import com.viadeo.kasper.domain.sample.timelines.api.Timelines;

@XKasperConcept(domain = Timelines.class, label = Status.NAME)
public class Status extends Concept {
	private static final long serialVersionUID = -7060060580057365932L;

	public static final String NAME = "TimelineStatus";

    private LinkedConcept<Timeline> attachedTo;

    /* different domain : will not be documented */
    private LinkedConcept<Member> publishedBy;

}
