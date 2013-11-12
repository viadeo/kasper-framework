// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.KasperID;

/**
 *
 * Can be used for Kasper commands which will create entities
 * The submitted id can then be used by handlers as the created element's id
 *
 */
public interface CreateCommand extends Command {

	/**
	 * @return the (optional) user requested id to be used
	 */
	KasperID getIdToUse();

}
