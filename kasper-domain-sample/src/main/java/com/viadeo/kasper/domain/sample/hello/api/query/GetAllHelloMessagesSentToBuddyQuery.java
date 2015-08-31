// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.domain.sample.hello.api.query;

import com.viadeo.kasper.api.annotation.XKasperQuery;
import com.viadeo.kasper.api.component.query.Query;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.domain.sample.hello.api.HelloDomain.*;

@XKasperQuery(description = "Retrieve all submitted Hello messages for a specific buddy")
public class GetAllHelloMessagesSentToBuddyQuery implements Query {

    @NotNull( message = NOT_PROVIDED_BUDDY_MSG )
    @Length( min = MIN_BUDDY_LENGTH, max = MAX_BUDDY_LENGTH, message = BAD_LENGTH_BUDDY_MSG )
    @Pattern( regexp = REGEX_BUDDY, message = INVALID_BUDDY_MSG)
    private final String forBuddy;

    // ------------------------------------------------------------------------

    public GetAllHelloMessagesSentToBuddyQuery(final String forBuddy) {
        this.forBuddy = checkNotNull(forBuddy);
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GetAllHelloMessagesSentToBuddyQuery other = (GetAllHelloMessagesSentToBuddyQuery) obj;
        return com.google.common.base.Objects.equal(this.forBuddy, other.forBuddy);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(this.forBuddy);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(this.forBuddy)
                .toString();
    }

}
