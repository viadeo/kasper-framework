// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.id.KasperRelationID;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.er.annotation.XBidirectional;

/**
 *
 * A aggregate root for Kasper relation
 *
 * @param <S> Source of the relation
 * @param <T> Target of the relation
 * 
 * @see Relation
 * @see com.viadeo.kasper.ddd.AggregateRoot
 */
public class Relation<S extends Concept, T extends Concept>
        extends AggregateRoot<KasperRelationID>
		implements Entity {

    private static final long serialVersionUID = 4719442806097449770L;

    /**
     * The position of the source concept parameter
     */
    public static final Integer SOURCE_PARAMETER_POSITION = 0;

    /**
     * The position of the target concept parameter
     */
    public static final Integer TARGET_PARAMETER_POSITION = 1;

    // ------------------------------------------------------------------------

    public KasperID getSourceIdentifier() {
        return this.getEntityId().getSourceId();
    }

    public KasperID getTargetIdentifier() {
        return this.getEntityId().getTargetId();
    }

    public boolean isBidirectional() {
        return (null != this.getClass().getAnnotation(XBidirectional.class));
    }

}
