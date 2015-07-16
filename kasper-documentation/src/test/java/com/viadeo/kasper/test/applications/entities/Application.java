// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.applications.entities;

import com.viadeo.kasper.core.component.command.aggregate.Concept;
import com.viadeo.kasper.core.component.command.aggregate.LinkedConcept;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperConcept;
import com.viadeo.kasper.test.applications.Applications;
import com.viadeo.kasper.test.root.entities.Member;

@XKasperConcept(domain = Applications.class, label = Application.NAME)
public class Application extends Concept {
	private static final long serialVersionUID = 7663957891087399105L;

	public static final String NAME = "Application";

    /* different domain : will not be documented */
    private LinkedConcept<Member> createdBy;
	
}
