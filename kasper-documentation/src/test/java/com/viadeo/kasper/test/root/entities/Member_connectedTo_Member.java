package com.viadeo.kasper.test.root.entities;

import com.viadeo.kasper.er.Relation;
import com.viadeo.kasper.er.annotation.XKasperRelation;
import com.viadeo.kasper.test.root.Facebook;

@XKasperRelation(domain = Facebook.class, label = Member_connectedTo_Member.NAME)
public class Member_connectedTo_Member extends Relation<Member, Member> {
	
	private static final long serialVersionUID = 2799008865289493137L;

	public static final String NAME = "connected_to";
	
}
