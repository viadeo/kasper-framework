// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query;

import com.viadeo.kasper.annotation.Immutable;

import java.io.Serializable;

/**
 * The Kasper query marker interface
 */
public interface Query extends Serializable, Immutable {

}
