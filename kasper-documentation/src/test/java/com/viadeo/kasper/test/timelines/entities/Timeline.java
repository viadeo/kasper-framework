// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.timelines.entities;

import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.LinkedConcept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.test.root.entities.Member;
import com.viadeo.kasper.test.timelines.Timelines;

@XKasperConcept(domain = Timelines.class, label = Timeline.NAME)
public class Timeline extends Concept {
	private static final long serialVersionUID = 191636014372954922L;
	
	public static final String NAME = "MemberTimeline";

    /* different domain : will not be documented */
    private LinkedConcept<Member> ownedBy;
}
