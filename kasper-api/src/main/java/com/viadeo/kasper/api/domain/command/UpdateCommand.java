// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.domain.command;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;

import javax.validation.constraints.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * Convenient base implementation for id and version management
 * 
 */
public abstract class UpdateCommand implements Command {
    private static final long serialVersionUID = -432287057423281452L;

    @NotNull
	private final KasperID id;

    private final Long version;

	// ------------------------------------------------------------------------

	protected UpdateCommand(final KasperID id) {
		this.id = checkNotNull(id);
        this.version = null;
	}

 	protected UpdateCommand(final KasperID id, final Long version) {
		this.id = checkNotNull(id);
        this.version = version; /* can be null */
	}

	// ------------------------------------------------------------------------

	public KasperID getId() {
		return this.id;
	}

    public Optional<Long> getVersion() {
        return Optional.fromNullable(this.version);
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (null != obj) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UpdateCommand other = (UpdateCommand) obj;

        return com.google.common.base.Objects.equal(this.id, other.id)
                && com.google.common.base.Objects.equal(this.version, other.version);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(this.id, this.version);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(this.id)
                .addValue(this.version)
                .toString();
    }

}
