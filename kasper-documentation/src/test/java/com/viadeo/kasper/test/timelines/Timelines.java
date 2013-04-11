package com.viadeo.kasper.test.timelines;

import com.viadeo.kasper.ddd.annotation.XKasperDomain;
import com.viadeo.kasper.test.root.Facebook;

@XKasperDomain(prefix=Timelines.PREFIX, label=Timelines.NAME)
public class Timelines extends Facebook {

	public static final String PREFIX = "tm";
	public static final String NAME = "Timelines";
	
}
