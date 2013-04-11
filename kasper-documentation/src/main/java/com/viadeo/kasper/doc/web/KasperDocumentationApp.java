// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.web;

import com.sun.jersey.api.core.PackagesResourceConfig;

public class KasperDocumentationApp extends PackagesResourceConfig {
		
	public KasperDocumentationApp() {
		super(KasperDocumentationApp.class.getPackage().getName());
	}
	
}