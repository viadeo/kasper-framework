// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

public class DocumentedProperty {

	private final String name;
	private final String type;
	private final Boolean isList;
    private final Boolean mandatory;
	
	// ------------------------------------------------------------------------
	
	DocumentedProperty(final String name, final String type, final Boolean isList, Boolean mandatory) {
		this.name = name;
		this.type = type;
		this.isList = isList;
        this.mandatory = mandatory;
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

    public Boolean isMandatory() {
        return mandatory;
    }
}
