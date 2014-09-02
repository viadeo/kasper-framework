// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.annotation.Immutable;

import java.io.Serializable;

/**
 *
 * Base marker for Kasper commands
 *
 */
public interface Command extends Serializable, Immutable {

}
