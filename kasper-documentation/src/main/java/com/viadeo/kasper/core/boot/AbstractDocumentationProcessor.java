// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.boot;

import java.lang.annotation.Annotation;

import com.google.common.base.Preconditions;
import com.viadeo.kasper.doc.KasperLibrary;

public abstract class AbstractDocumentationProcessor<T extends Annotation, I> implements IAnnotationProcessor<T, I> {

	private KasperLibrary kasperLibrary;
	
	public void setKasperLibrary(final KasperLibrary kasperLibrary) {
		this.kasperLibrary = Preconditions.checkNotNull(kasperLibrary);
	}
	
	public KasperLibrary getKasperLibrary() {
		return this.kasperLibrary;
	}
	
}
