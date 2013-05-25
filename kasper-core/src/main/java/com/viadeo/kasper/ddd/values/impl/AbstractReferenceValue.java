// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.values.impl;

import com.viadeo.kasper.ddd.values.IReferenceValue;

import java.io.Serializable;

/**
 *
 * @param <PAYLOAD> Reference value payload
 * 
 */
public abstract class AbstractReferenceValue<PAYLOAD extends Serializable> 
		extends AbstractEnclosingValue<PAYLOAD> 
		implements IReferenceValue<PAYLOAD> {
	
	private static final long serialVersionUID = -2912518894544850152L;
	
	// ------------------------------------------------------------------------
	
	public AbstractReferenceValue(final PAYLOAD value) {
		super(value);
		// TODO: manage with an additional id
	}
	
}
