// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.timelines;

import com.viadeo.kasper.api.documentation.XKasperDomain;
import com.viadeo.kasper.test.root.Facebook;

@XKasperDomain(prefix=Timelines.PREFIX, label=Timelines.NAME)
public class Timelines extends Facebook {

	public static final String PREFIX = "tm";
	public static final String NAME = "Timelines";
	
}
