// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.applications.api;

import com.viadeo.kasper.api.annotation.XKasperDomain;
import com.viadeo.kasper.domain.sample.root.api.Facebook;

@XKasperDomain(prefix=Applications.PREFIX, label=Applications.NAME, description=Applications.DESCRIPTION)
public class Applications extends Facebook {

	public static final String PREFIX = "apps";
	public static final String NAME = "Applications";
	public static final String DESCRIPTION = "Applications domain";
	
}
