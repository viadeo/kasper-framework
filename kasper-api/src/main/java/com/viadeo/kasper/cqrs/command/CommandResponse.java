// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.KasperResponse;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base Kasper command response implementation
 */
public class CommandResponse extends KasperResponse {
    private static final long serialVersionUID = -938831661655150085L;

    private String securityToken;

    // ------------------------------------------------------------------------

    public static CommandResponse error(final KasperReason reason) {
        return new CommandResponse(Status.ERROR, reason);
    }

    public static CommandResponse error(final String code, final String reason) {
        return error(new KasperReason(code, reason));
    }

    public static CommandResponse error(final CoreReasonCode code, final String reason) {
        return error(new KasperReason(checkNotNull(code), reason));
    }

    public static CommandResponse error(final CoreReasonCode code) {
        return error(new KasperReason(checkNotNull(code)));
    }

    // ------------------------------------------------------------------------

    public static CommandResponse refused(final KasperReason reason) {
        return new CommandResponse(Status.REFUSED, reason);
    }

    public static CommandResponse refused(final String code, final String reason) {
        return refused(new KasperReason(code, reason));
    }

    public static CommandResponse refused(final CoreReasonCode code, final String reason) {
        return refused(new KasperReason(code, reason));
    }

    public static CommandResponse refused(final CoreReasonCode code) {
        return refused(new KasperReason(code));
    }

    // ------------------------------------------------------------------------

    public static CommandResponse ok() {
        return new CommandResponse(Status.OK, null);
    }

    // ------------------------------------------------------------------------

    public CommandResponse withSecurityToken(final String securityToken) {
        this.securityToken = checkNotNull(securityToken);
        return this;
    }

    public Optional<String> getSecurityToken() {
        return Optional.fromNullable(this.securityToken);
    }

    // ------------------------------------------------------------------------

    public CommandResponse(final CommandResponse response) {
        super(response);

        if (response.getSecurityToken().isPresent()) {
            this.securityToken = response.getSecurityToken().get();
        }
    }

    public CommandResponse(final Status status, final KasperReason reason) {
        super(status, reason);
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

        final CommandResponse other = (CommandResponse) obj;

        if ( ! super.equals(obj)) {
            return false;
        }

        if (this.getSecurityToken().isPresent() != other.getSecurityToken().isPresent()) {
            return false;
        }

        if ( ! this.getSecurityToken().isPresent()) {
            return true;
        }

        if ( ! this.getSecurityToken().get().equals(other.getSecurityToken().get())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = super.hashCode();
        if (this.getSecurityToken().isPresent()) {
            hashCode += com.google.common.base.Objects.hashCode(this.getSecurityToken().get());
        }
        return hashCode;
    }

    @Override
    public String toString() {
        final Objects.ToStringHelper helper = com.google.common.base.Objects.toStringHelper(this)
                           .addValue(super.toString());
        if (this.getSecurityToken().isPresent()) {
            helper.addValue(this.getSecurityToken().get());
        }

        return helper.toString();
    }

}
