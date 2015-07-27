// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition.query;

class PropertyAdapterPair<F extends PropertyAdapter, S> {

    private F firstValue;
    private S secondValue;

    // ------------------------------------------------------------------------

    public PropertyAdapterPair(final F firstValue, final S secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    // ------------------------------------------------------------------------

    public F firstValue() {
        return firstValue;
    }

    public S secondValue() {
        return secondValue;
    }

}
