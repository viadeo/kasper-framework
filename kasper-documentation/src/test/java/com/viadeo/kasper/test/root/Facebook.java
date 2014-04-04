package com.viadeo.kasper.test.root;

import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;

@XKasperDomain(
        prefix=Facebook.PREFIX,
        label=Facebook.NAME,
        description=Facebook.DESCRIPTION,
        owner = "Mark Zuckerberg <mzuckerberg@facebook.com>"
)
public class Facebook implements Domain {

	public static final String PREFIX = "fb";
	public static final String NAME = "Facebook";
	public static final String DESCRIPTION = "Facebook is an online social networking service. Its name comes from a colloquialism for the directory given to students at some American universities.[5] Facebook was founded on February 4, 2004 by Mark Zuckerberg with his college roommates and fellow Harvard University students Eduardo Saverin, Andrew McCollum, Dustin Moskovitz and Chris Hughes.";
	
}
