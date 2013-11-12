// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.CreateCommand;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Convenient base implementation for idToUse simple management
 * 
 */
public abstract class AbstractCreateCommand implements CreateCommand {
    private static final long serialVersionUID = -432287057793281452L;

    @NotNull
	private final KasperID idToUse;

	// ------------------------------------------------------------------------

	public AbstractCreateCommand(final KasperID providedId) {
		this.idToUse = checkNotNull(providedId);
	}

	// ------------------------------------------------------------------------

	@Override
	public KasperID getIdToUse() {
		return this.idToUse;
	}

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AbstractCreateCommand other = (AbstractCreateCommand) obj;

        return com.google.common.base.Objects.equal(this.idToUse, other.idToUse);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(this.idToUse);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(this.idToUse)
                .toString();
    }

}
