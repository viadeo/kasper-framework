// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.component.event;

import com.viadeo.kasper.api.Immutable;

import java.io.Serializable;

/**
 *
 * The Kasper event
 *
 */
public interface Event extends Serializable, Immutable { }