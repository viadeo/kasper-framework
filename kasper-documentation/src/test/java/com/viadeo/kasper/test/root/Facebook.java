package com.viadeo.kasper.test.root;

import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;

@XKasperDomain(prefix=Facebook.PREFIX, label=Facebook.NAME, description=Facebook.DESCRIPTION)
public class Facebook implements Domain {

	public static final String PREFIX = "fb";
	public static final String NAME = "Facebook";
	public static final String DESCRIPTION = "Root Facebook domain";
	
}
