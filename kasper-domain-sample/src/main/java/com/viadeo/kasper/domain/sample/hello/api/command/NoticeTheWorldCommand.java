// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.command;

import com.viadeo.kasper.api.annotation.XKasperCommand;
import com.viadeo.kasper.api.component.command.Command;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.viadeo.kasper.domain.sample.hello.api.HelloDomain.*;

/**
 * A simple command
 */
@XKasperCommand(description = "Send a response to an existing hello message")
public class NoticeTheWorldCommand implements Command {

    @NotNull( message = NOT_PROVIDED_HELLO_MSG )
    @Length( min = MIN_HELLO_LENGTH, max = MAX_HELLO_LENGTH, message = BAD_LENGTH_HELLO_MSG )
    @Pattern( regexp = REGEX_HELLO, message = INVALID_HELLO_MSG )
    private final String notice;

    // ------------------------------------------------------------------------

    public NoticeTheWorldCommand(final String notice) {
        this.notice = notice;
    }

    public String getNotice() {
        return this.notice;
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

        final NoticeTheWorldCommand other = (NoticeTheWorldCommand) obj;
        return com.google.common.base.Objects.equal(this.notice, other.notice);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(this.notice);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(this.notice)
                .toString();
    }

}
