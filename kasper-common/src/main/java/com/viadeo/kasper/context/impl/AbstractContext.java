// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.context.IContext;

/**
 * Simple implementation of a IContext
 *
 */
public abstract class AbstractContext implements IContext {
	private static final long serialVersionUID = 1887660968377933167L;
	
	private Map<String, Serializable> properties;
	
	// ------------------------------------------------------------------------
	
	protected AbstractContext() {
		
	}
	
	@Override
	public void setProperty(final String key, final Serializable value) {
		if (null == this.properties) {
			this.properties = Maps.newHashMap();
		}
		this.properties.put(key, value);
	}

	@Override
	public Optional<Serializable> getProperty(final String key) {
		if (null == this.properties) {
			return Optional.absent();
		}
		return Optional.fromNullable(this.properties.get(key));
	}

	@Override
	public boolean hasProperty(String key) {
		if (null == this.properties) {
			return false;
		}
		return this.properties.containsKey(key);
	}

	@Override
	public Map<String, Serializable> getProperties() {
		final Map<String, Serializable> retMap;
		
		if (null == this.properties) {
			retMap = Maps.newLinkedHashMap();
		} else {
			retMap = Collections.unmodifiableMap(this.properties);
		}
		
		return retMap;
	}

}
