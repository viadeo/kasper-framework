// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.applications.command.model.entity;

import com.viadeo.kasper.core.component.command.aggregate.Relation;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperRelation;
import com.viadeo.kasper.domain.sample.applications.api.Applications;
import com.viadeo.kasper.domain.sample.root.command.model.entity.Member;

@XKasperRelation(domain = Applications.class, label = Member_fanOf_Application.NAME, description = Member_fanOf_Application.DESCRIPTION)
public class Member_fanOf_Application extends Relation<Member, Application> {
	private static final long serialVersionUID = -6384465229521499408L;
	
	public static final String NAME = "fan_of";
	public static final String DESCRIPTION = "Tha Member is a big fan of this application";

}
