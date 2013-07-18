// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.doc.nodes;

import java.util.Collection;
import java.util.Map;

public class RetMap extends RetBase {
	private static final long serialVersionUID = -5808650563772119762L;

	private static final String TYPE = "list";
	
	private final Collection<? extends DocumentedNode> list;
	
	private String itemType = null;
	
	private Integer count = 0;

	// ------------------------------------------------------------------------
	
	public RetMap(final Map<String, ? extends DocumentedNode> map) {
		super(TYPE);
		this.list = map.values();
		if (map.size() > 0) {
			final String key = (String) map.keySet().toArray()[0];
			itemType = map.get(key).getType();
			count = map.size();
		}
	}

	// ------------------------------------------------------------------------
	
	public Collection<? extends DocumentedNode> getList() {
		return this.list;
	}
	
	public Integer getCount() {
		return this.count;
	}
	
	public String getItemType() {
		return this.itemType;
	}
	
}
