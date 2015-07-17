// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.timelines.api;

import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.domain.sample.root.api.Facebook;

@XKasperDomain(prefix=Timelines.PREFIX, label=Timelines.NAME)
public class Timelines extends Facebook {

	public static final String PREFIX = "tm";
	public static final String NAME = "Timelines";
	
}
