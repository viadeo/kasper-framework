// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.element;

import com.google.common.collect.Lists;

import java.util.List;

import static com.viadeo.kasper.api.domain.annotation.XKasperAuthz.CombinesWith;

public class AuthorizationElement {

    private List<String> value = Lists.newArrayList();

    private String manager = null;

    private CombinesWith combinesWith = CombinesWith.AND;

    public List<String> getValue() {
        return value;
    }

    public void setValue(final List<String> value) {
        this.value = value;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(final String manager) {
        this.manager = manager;
    }

    public CombinesWith getCombinesWith() {
        return combinesWith;
    }

    public void setCombinesWith(final CombinesWith combinesWith) {
        this.combinesWith = combinesWith;
    }
}
