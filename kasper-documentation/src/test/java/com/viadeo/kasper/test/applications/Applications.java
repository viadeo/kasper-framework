package com.viadeo.kasper.test.applications;

import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.test.root.Facebook;

@XKasperDomain(prefix=Applications.PREFIX, label=Applications.NAME, description=Applications.DESCRIPTION)
public class Applications extends Facebook {

	public static final String PREFIX = "apps";
	public static final String NAME = "Applications";
	public static final String DESCRIPTION = "Applications domain";
	
}
