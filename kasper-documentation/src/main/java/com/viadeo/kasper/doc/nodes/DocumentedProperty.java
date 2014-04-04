// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import java.util.HashSet;
import java.util.Set;

public class DocumentedProperty {

	private final String name;
	private final String type;
    private final String defaultValues;
    private final Boolean isList;
    private final Boolean isLinkedConcept;
    private final Boolean isQueryResult;
    private final Set<DocumentedConstraint> constraints;

    private String elemType;
    private Boolean mandatory = false; /** javax.validation.constraints.NotNull */
	
	// ------------------------------------------------------------------------

    public DocumentedProperty(
            final String name,
            final String type,
            final Boolean isList,
            final Boolean isLinkedConcept,
            final Boolean isQueryResult,
            HashSet<DocumentedConstraint> constraints
    ) {
        this(name, type, null, isList, isQueryResult, isLinkedConcept, constraints);
    }

    // ------------------------------------------------------------------------

	public DocumentedProperty(
            final String name,
            final String type,
            final String defaultValues,
            final Boolean isList,
            final Boolean isLinkedConcept,
            final Boolean isQueryResult,
            HashSet<DocumentedConstraint> constraints
    ) {
		this.name = name;
		this.type = type;
        this.defaultValues = defaultValues;
        this.isList = isList;
        this.isLinkedConcept = isLinkedConcept;
        this.isQueryResult = isQueryResult;
        this.constraints = constraints;
    }
	
	// ------------------------------------------------------------------------

	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}

    public String getDefaultValues() {
        return defaultValues;
    }

    public Boolean isList() {
		return isList;
	}

    public Boolean getLinkedConcept() {
        return isLinkedConcept;
    }

    public Boolean isQueryResult() {
        return isQueryResult;
    }

    public Boolean isMandatory() {
        return mandatory;
    }

    public Set<DocumentedConstraint> getConstraints() {
        return constraints;
    }

    public String getElemType() {
        return elemType;
    }

    // ------------------------------------------------------------------------

    public void setMandatory(final Boolean mandatory) {
        this.mandatory = mandatory;
    }


    public void appendConstraint(final String type, final String constraint) {
        constraints.add(new DocumentedConstraint(type, constraint));
    }

    public void setElemType(final String elemType){
        this.elemType = elemType;
    }

}
