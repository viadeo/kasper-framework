// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;
import com.viadeo.kasper.doc.KasperLibrary;

public abstract class DocumentedDomainNode extends DocumentedNode {
	private static final long serialVersionUID = -1730425943452064427L;

	private String domainName;
	
	// ------------------------------------------------------------------------
	
	protected DocumentedDomainNode(final KasperLibrary kl, final String type, final String pluralType) {
		super(kl, type, pluralType);
	}		
	
	// ------------------------------------------------------------------------
	
	public DocumentedNode getDomain() {
		final Optional<DocumentedDomain> domain = this.getKasperLibrary().getDomainFromName(this.domainName);
		if (domain.isPresent()) {
			return new DocumentedNode(domain.get());
		}
		return null;
	}

	// ------------------------------------------------------------------------
	
	@JsonIgnore
	public String getDomainName() {
		return this.domainName;
	}
	
	public DocumentedDomainNode setDomainName(final String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	// ------------------------------------------------------------------------
	
	@Override
	public String getURL() {
		return String.format("/%s/%s/%s/%s", DocumentedDomain.TYPE_NAME, getDomain().getName(), getType(), getName());		
	}
	
}
