// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;

/**
 *
 * Can be used for Kasper commands which will create entities
 * The submitted id can then be used by handlers as the created element's id
 *
 */
public interface UpdateCommand extends Command {

    /**
     * @return the entity id to updated
     */
    KasperID getId();

    /**
     * @return the (optional) entity version to be updated
     */
    Optional<Long> getVersion();

}
