package com.viadeo.kasper.test.timelines.entities;

import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.test.timelines.Timelines;

@XKasperConcept(domain = Timelines.class, label = Timeline.NAME)
public class Timeline extends Concept {
	private static final long serialVersionUID = 191636014372954922L;
	
	public static final String NAME = "MemberTimeline";
	
}
