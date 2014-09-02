package com.viadeo.kasper.test.timelines.entities;

import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.LinkedConcept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.test.root.entities.Member;
import com.viadeo.kasper.test.timelines.Timelines;

@XKasperConcept(domain = Timelines.class, label = Status.NAME)
public class Status extends Concept {
	private static final long serialVersionUID = -7060060580057365932L;

	public static final String NAME = "TimelineStatus";

    private LinkedConcept<Timeline> attachedTo;

    /* different domain : will not be documented */
    private LinkedConcept<Member> publishedBy;

}
