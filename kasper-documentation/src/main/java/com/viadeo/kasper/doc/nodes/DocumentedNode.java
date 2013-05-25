// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.doc.KasperLibrary;

import javax.xml.bind.annotation.XmlTransient;

public class DocumentedNode extends RetBase {
	private static final long serialVersionUID = -2064570621327638305L;

	private final transient KasperLibrary kasperLibrary;

	private final String typePlural;

	private String className = null;
	protected String label = null;
	private String description = null;
	private String url = null;

	// ------------------------------------------------------------------------

	public static final String XML_PLURALTYPE = "pluralType";
	public static final String XML_NAME = "name";
	public static final String XML_DESCRIPTION = "description";
	public static final String XML_PAGEURL = "path";

	// ------------------------------------------------------------------------

	protected DocumentedNode(final KasperLibrary kl, final String type, final String typePlural) {
		super(type);

		this.typePlural = Preconditions.checkNotNull(typePlural);
		this.kasperLibrary = kl;
	}

	public DocumentedNode(final DocumentedNode node) {
		super(node.getType());

		this.typePlural = node.getTypePlural();
		this.className = node.getName();
		this.description = node.getDescription();
		this.url = node.getURL();
		this.label = node.getLabel();
		this.kasperLibrary = node.getKasperLibrary();		
	}

	public DocumentedNode toSimpleNode() {
		if (DocumentedNode.class.equals(this.getClass())) {
			return this;
		}
		return new DocumentedNode(this);
	}
	
	// ------------------------------------------------------------------------

	public String getTypePlural() {
		return this.typePlural;
	}
	
	public DocumentedNode setUrl(final String url) {
		this.url = url;
		return this;
	}

	public String getURL() {
		if (null != this.url) {
			return this.url;
		} else {
			return String.format("/%s/%s", getType(), className);
		}
	}

	// ------------------------------------------------------------------------

	@JsonIgnore
	@XmlTransient
	public KasperLibrary getKasperLibrary() {
		return this.kasperLibrary;
	}

	// ------------------------------------------------------------------------

	public DocumentedNode setName(final String name) {
		this.className = name;
		return this;
	}

	public String getName() {
		return this.className;
	}

	// ------------------------------------------------------------------------

	public DocumentedNode setLabel(final String label) {
		this.label = label;
		return this;
	}

	public String getLabel() {
		return (null != this.label) ? this.label : getName();
	}	
	
	// ------------------------------------------------------------------------

	public DocumentedNode setDescription(final String description) {
		this.description = description;
		return this;
	}

	public String getDescription() {
		return this.description;
	}

}
