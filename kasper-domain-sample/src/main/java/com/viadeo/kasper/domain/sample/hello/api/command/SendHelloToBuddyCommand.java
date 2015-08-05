// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.command;

import com.viadeo.kasper.api.annotation.XKasperCommand;
import com.viadeo.kasper.api.component.command.CreateCommand;
import com.viadeo.kasper.api.id.KasperID;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.viadeo.kasper.domain.sample.hello.api.HelloDomain.*;

/**
 * It's a creation command, we extends here CreateCommand
 *
 * This will server several interests :
 *   - filter this command as being an entity creator anywhere you can need it in your code
 *   - can be used by auto doc
 *   - provides a standard interface to clients ('idToUse')
 *   - re-enforce client-provided id paradigm
 */
@XKasperCommand(description = "Send an Hello to a buddy name")
public class SendHelloToBuddyCommand extends CreateCommand {

    @NotNull( message = NOT_PROVIDED_HELLO_MSG )
    @Length( min = MIN_HELLO_LENGTH, max = MAX_HELLO_LENGTH, message = BAD_LENGTH_HELLO_MSG )
    @Pattern( regexp = REGEX_HELLO, message = INVALID_HELLO_MSG )
    private final String message;

    @NotNull( message = NOT_PROVIDED_BUDDY_MSG )
    @Length( min = MIN_BUDDY_LENGTH, max = MAX_BUDDY_LENGTH, message = BAD_LENGTH_BUDDY_MSG )
    @Pattern( regexp = REGEX_BUDDY, message = INVALID_BUDDY_MSG)
    private final String forBuddy;

    // ------------------------------------------------------------------------

    public SendHelloToBuddyCommand(final KasperID idToUse, final String message, final String forBuddy) {
        super(idToUse);
        this.message = message;
        this.forBuddy = forBuddy;
    }

    public String getMessage() {
        return this.message;
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

        final SendHelloToBuddyCommand other = (SendHelloToBuddyCommand) obj;

        return  com.google.common.base.Objects.equal(this.message, other.message)
                && com.google.common.base.Objects.equal(this.getIdToUse(), other.getIdToUse())
                && com.google.common.base.Objects.equal(this.forBuddy, other.forBuddy);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(
                super.hashCode(), this.message, this.forBuddy);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(super.toString())
                .addValue(this.message)
                .addValue(this.forBuddy)
                .toString();
    }
}
