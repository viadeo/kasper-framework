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

@XKasperConcept(domain = Timelines.class, label = Timeline.NAME)
public class Timeline extends Concept {
	private static final long serialVersionUID = 191636014372954922L;
	
	public static final String NAME = "MemberTimeline";

    /* different domain : will not be documented */
    private LinkedConcept<Member> ownedBy;
}
