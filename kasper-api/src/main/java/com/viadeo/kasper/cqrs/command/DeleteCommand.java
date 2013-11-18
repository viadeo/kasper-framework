// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.KasperID;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Convenient base implementation for id and version management
 * 
 */
public abstract class DeleteCommand implements Command {
    private static final long serialVersionUID = -432287427423281452L;

    @NotNull
	private final KasperID id;

	// ------------------------------------------------------------------------

	public DeleteCommand(final KasperID id) {
		this.id = checkNotNull(id);
	}

	// ------------------------------------------------------------------------

	public KasperID getId() {
		return this.id;
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
        final DeleteCommand other = (DeleteCommand) obj;

        return com.google.common.base.Objects.equal(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(this.id);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(this.id)
                .toString();
    }

}
