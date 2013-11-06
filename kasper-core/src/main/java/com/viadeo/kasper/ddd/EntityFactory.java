// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd;

import com.viadeo.kasper.context.Context;

/**
 * A proposal marker interface for entity factories
 * The builder pattern is a good way to build objects.. :)
 *
 * @param <E> Entity to be built
 *
 * @see com.viadeo.kasper.ddd.UbiquitousLanguageElement
 * @see com.viadeo.kasper.ddd.Domain
 * @see com.viadeo.kasper.ddd.Entity
 */
public interface EntityFactory<E extends Entity> extends UbiquitousLanguageElement {

	/**
	 * Generic parameter position of the Entity
	 */
	int ENTITY_PARAMETER_POSITION = 0;

    /**
     * Finalize the build of the entity
     *
     * @param context the current context to be used to build the entity
     * @return the built entity
     */
    E build(final Context context);

}
