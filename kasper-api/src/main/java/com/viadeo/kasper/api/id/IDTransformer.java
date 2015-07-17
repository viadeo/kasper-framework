// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Defines the requirement for an object responsible for "transforming" an <code>ID</code>
 */
public interface IDTransformer {

    /**
     * Transform the <code>ID</code>s according to the specified <code>Format</code>
     *
     * @param format the targeted format
     * @param ids the list of initial ids
     * @return a map of old vs transformed
     */
    Map<ID,ID> to(Format format, Collection<ID> ids);

    /**
     * Transform the <code>ID</code>s according to the specified <code>Format</code>
     *
     * @param format the targeted format
     * @param firstId the first id
     * @param restIds the rest of ids
     * @return a map of old vs transformed
     */
    Map<ID,ID> to(Format format, ID firstId,  ID... restIds);

    /**
     * Transform the <code>ID</code>s according to the specified <code>Format</code>
     *
     * @param format the targeted format
     * @param ids the list of initial ids
     * @return a list of transformed id
     */
    List<ID> toList(Format format, Collection<ID> ids);

    /**
     * Transform the <code>ID</code> according to the specified <code>Format</code>
     *
     * @param format the targeted format
     * @param id the initial id
     * @return a transformed id
     */
    ID to(Format format, ID id);
}
