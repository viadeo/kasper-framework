// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga;

import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;

@XKasperUnregistered
@XKasperDomain(
        label = "TestDomain",
        prefix = "sec",
        description = "The Security domain",
        owner = "Emmanuel Camper <ecamper@viadeoteam.com>"
)
public class TestDomain implements Domain {
}