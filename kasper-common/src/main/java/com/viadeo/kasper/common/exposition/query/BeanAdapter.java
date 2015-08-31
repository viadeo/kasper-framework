// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition.query;

/* I am not sure of the name, bean adapter sounds good but this class
* is responsible of ser/deser complex objects and use (if needed) contextual information
* provided via BeanProperty. If someone has a better name I want to hear it :)
*/
public interface BeanAdapter<T> {

    void adapt(T value, QueryBuilder builder, BeanProperty property) throws Exception;

    T adapt(QueryParser parser, BeanProperty property) throws Exception;

}
