// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.command;

import com.viadeo.kasper.api.annotation.XKasperCommand;
import com.viadeo.kasper.api.component.command.UpdateCommand;
import com.viadeo.kasper.api.id.KasperID;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.viadeo.kasper.domain.sample.hello.api.HelloDomain.*;

/**
 * It's a command designed to update an entity, let's mark it with UpdateCommand
 */
@XKasperCommand(description = "Change the buddy name for an existing hello message")
public class ChangeBuddyForExistingHelloCommand extends UpdateCommand {

    @NotNull( message = NOT_PROVIDED_BUDDY_MSG )
    @Length( min = MIN_BUDDY_LENGTH, max = MAX_BUDDY_LENGTH, message = BAD_LENGTH_BUDDY_MSG )
    @Pattern( regexp = REGEX_BUDDY, message = INVALID_BUDDY_MSG)
    private final String forBuddy;

    // ------------------------------------------------------------------------

    public ChangeBuddyForExistingHelloCommand(final KasperID id, final Long version, final String forBuddy) {
        super(id, version);
        this.forBuddy = forBuddy;
    }

    public String getForBuddy() {
        return this.forBuddy;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {

        if (null == obj) {
            return false;
        }

        if ( ! getClass().equals(obj.getClass())) {
            return false;
        }

        final ChangeBuddyForExistingHelloCommand other = (ChangeBuddyForExistingHelloCommand) obj;
        return super.equals(obj) && com.google.common.base.Objects.equal(this.forBuddy, other.forBuddy);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), this.forBuddy);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(super.toString())
                .addValue(this.forBuddy)
                .toString();
    }

}
