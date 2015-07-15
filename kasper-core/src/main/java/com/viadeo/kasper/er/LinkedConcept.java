// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.KasperID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * KasperID decorator used to add semantic meaning to a linked concept id
 *
 * public class Message extends Concept {
 *
 *     private LinkedConcept&lt;Member&gt; sender;
 *     private LinkedConcept&lt;Member&gt; recipient;
 *
 * }
 *
 */
public class LinkedConcept<C extends Concept> implements KasperID {

    public static final int CONCEPT_PARAMETER_POSITION = 0;

    private final KasperID linkedConceptId;

    // ------------------------------------------------------------------------

    public LinkedConcept(final KasperID linkedConceptId) {
        this.linkedConceptId = checkNotNull(linkedConceptId);
    }

    // ------------------------------------------------------------------------

    public KasperID getLinkedConceptId() {
        return this.linkedConceptId;
    }

    @Override
    public Object getId() {
        return this.linkedConceptId.getId();
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return "linked[" + this.linkedConceptId.toString() + "]";
    }

    @Override
    public boolean equals(final Object value) {
        return this.linkedConceptId.equals(checkNotNull(value));
    }

    @Override
    public int hashCode() {
        return this.linkedConceptId.hashCode();
    }

}
