// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

public class DocumentedSimpleRelation extends DocumentedNode {
	private static final long serialVersionUID = 280370243066985843L;
	
	private String sourceConceptName = "unknown";
	private String targetConceptName = "unknown";

    // ------------------------------------------------------------------------
	
	public DocumentedSimpleRelation(final DocumentedRelation relation) {
		super(relation);

		this.sourceConceptName = relation.getSourceConceptName();
		this.targetConceptName = relation.getTargetConceptName();
	}
	
	// --
	
	public String getSourceConceptName() {
		return this.sourceConceptName;
	}
	
	// --
	
	public String getTargetConceptName() {
		return this.targetConceptName;
	}	
	
}
