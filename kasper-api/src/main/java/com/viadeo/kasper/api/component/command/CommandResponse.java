// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.component.command;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base Kasper command response implementation
 */
public class CommandResponse extends KasperResponse {
    private static final long serialVersionUID = -938831661655150085L;

    private String securityToken;
    private String accessToken;
    private Serializable authenticationToken;

    // ------------------------------------------------------------------------

    /**
     * Get a failure response which is an unexpected and can require intervention before the system can resume at the
     * same level of operation. This does not mean that failures are always fatal, rather that some capacity of the
     * system will be reduced following a failure.
     *
     * @param reason a reason
     * @return a failure response
     */
    public static CommandResponse failure(final KasperReason reason) {
        return new CommandResponse(Status.FAILURE, reason);
    }

    // ------------------------------------------------------------------------

    /**
     * Get an error response which is an expected part of normal operations, are dealt with immediately and the system
     * will continue to operate at the same capacity following an error.
     *
     * @param reason a reason
     * @return an error response
     */
    public static CommandResponse error(final KasperReason reason) {
        return new CommandResponse(Status.ERROR, reason);
    }

    /**
     * Get an error response which is an expected part of normal operations, are dealt with immediately and the system
     * will continue to operate at the same capacity following an error.
     *
     * @param code the core reason code
     * @param reason a reason
     * @return an error response
     */
    public static CommandResponse error(final String code, final String reason) {
        return error(new KasperReason(code, reason));
    }

    /**
     * Get an error response which is an expected part of normal operations, are dealt with immediately and the system
     * will continue to operate at the same capacity following an error.
     *
     * @param code the core reason code
     * @param reason a reason
     * @return an error response
     */
    public static CommandResponse error(final CoreReasonCode code, final String reason) {
        return error(new KasperReason(checkNotNull(code), reason));
    }

    /**
     * Get an error response which is an expected part of normal operations, are dealt with immediately and the system
     * will continue to operate at the same capacity following an error.
     *
     * @param code the core reason code
     * @return an error response
     */
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

    public static CommandResponse accepted() {
        return new CommandResponse(Status.ACCEPTED, new KasperReason("Acknowledge"));
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

    public CommandResponse withAccessToken(final String accessToken) {
        this.accessToken = checkNotNull(accessToken);
        return this;
    }

    public Optional<String> getAccessToken() {
        return Optional.fromNullable(this.accessToken);
    }

    public CommandResponse withAuthenticationToken(final Serializable authenticationToken) {
        this.authenticationToken = checkNotNull(authenticationToken);
        return this;
    }

    public <TOKEN extends Serializable> Optional<TOKEN> getAuthenticationToken() {
        return Optional.<TOKEN>fromNullable((TOKEN) this.authenticationToken);
    }

    // ------------------------------------------------------------------------

    public CommandResponse(final KasperResponse response, final String securityToken) {
        super(response);
        this.securityToken = checkNotNull(securityToken);
    }

    public CommandResponse(final KasperResponse response) {
        super(response);
    }

    public CommandResponse(final CommandResponse response) {
        super(response);

        if (response.getSecurityToken().isPresent()) {
            this.securityToken = response.getSecurityToken().get();
        }

        if (response.getAccessToken().isPresent()) {
            this.accessToken = response.getAccessToken().get();
        }

        if (response.getAuthenticationToken().isPresent()) {
            this.authenticationToken = response.getAuthenticationToken().get();
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

        if (this.getAccessToken().isPresent() != other.getAccessToken().isPresent()) {
            return false;
        }

        if ( ! this.getAccessToken().isPresent()) {
            return true;
        }

        if ( ! this.getAccessToken().get().equals(other.getAccessToken().get())) {
            return false;
        }

        if (this.getAuthenticationToken().isPresent() != other.getAuthenticationToken().isPresent()) {
            return false;
        }

        if ( ! this.getAuthenticationToken().isPresent()) {
            return true;
        }

        if ( ! this.getAuthenticationToken().get().equals(other.getAuthenticationToken().get())) {
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
        if (this.getAccessToken().isPresent()) {
            hashCode += com.google.common.base.Objects.hashCode(this.getAccessToken().get());
        }
        if (this.getAuthenticationToken().isPresent()) {
            hashCode += com.google.common.base.Objects.hashCode(this.getAuthenticationToken().get());
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
        if (this.getAccessToken().isPresent()) {
            helper.addValue(this.getAccessToken().get());
        }
        if (this.getAuthenticationToken().isPresent()) {
            helper.addValue(this.getAuthenticationToken().get());
        }

        return helper.toString();
    }

}
