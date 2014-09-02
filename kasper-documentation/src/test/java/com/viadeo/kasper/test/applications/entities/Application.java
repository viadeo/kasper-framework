package com.viadeo.kasper.test.applications.entities;

import com.viadeo.kasper.er.Concept;
import com.viadeo.kasper.er.LinkedConcept;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.test.applications.Applications;
import com.viadeo.kasper.test.root.entities.Member;

@XKasperConcept(domain = Applications.class, label = Application.NAME)
public class Application extends Concept {
	private static final long serialVersionUID = 7663957891087399105L;

	public static final String NAME = "Application";

    /* different domain : will not be documented */
    private LinkedConcept<Member> createdBy;
	
}
