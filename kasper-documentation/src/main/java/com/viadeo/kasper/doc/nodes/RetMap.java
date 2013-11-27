// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.viadeo.kasper.doc.element.WithType;

import java.util.Collection;
import java.util.Map;

public class RetMap extends RetBase {
	private static final long serialVersionUID = -5808650563772119762L;

	private static final String TYPE = "list";
	
	private final Collection<? extends WithType> list;
	
	private String itemType = null;
	
	private Integer count = 0;

	// ------------------------------------------------------------------------

    public RetMap(String itemType, Collection<? extends WithType> list) {
        super(TYPE);
        this.list = list;
        this.itemType = itemType;
        this.count = list.size();
    }

	public RetMap(final Map<String, ? extends WithType> map) {
		super(TYPE);
		this.list = map.values();
		if (map.size() > 0) {
			final String key = (String) map.keySet().toArray()[0];
			itemType = map.get(key).getType();
			count = map.size();
		}
	}

	// ------------------------------------------------------------------------
	
	public Collection<? extends WithType> getList() {
		return this.list;
	}
	
	public Integer getCount() {
		return this.count;
	}
	
	public String getItemType() {
		return this.itemType;
	}
	
}
