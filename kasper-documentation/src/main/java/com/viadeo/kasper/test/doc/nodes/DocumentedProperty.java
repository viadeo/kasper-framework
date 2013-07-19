package com.viadeo.kasper.test.doc.nodes;

public class DocumentedProperty {

	private final String name;
	private final String type;
	private final Boolean isList;
	
	// ------------------------------------------------------------------------
	
	DocumentedProperty(final String name, final String type, final Boolean isList) {
		this.name = name;
		this.type = type;
		this.isList = isList;
	}
	
	// ------------------------------------------------------------------------

	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
	
	public Boolean isList() {
		return this.isList;
	}
	
}
