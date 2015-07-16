// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.root.entities;

import com.viadeo.kasper.core.component.command.aggregate.LinkedConcept;
import com.viadeo.kasper.core.component.command.aggregate.Relation;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XKasperRelation;
import com.viadeo.kasper.test.root.Facebook;

@XKasperRelation(domain = Facebook.class, label = Member_connectedTo_Member.NAME)
@Deprecated
public class Member_connectedTo_Member extends Relation<Member, Member> {
	
	private static final long serialVersionUID = 2799008865289493137L;

	public static final String NAME = "connected_to";

    public LinkedConcept<Member> other;

}
