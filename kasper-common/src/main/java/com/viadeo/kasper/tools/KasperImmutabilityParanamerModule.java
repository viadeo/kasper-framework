// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.tools;

import com.fasterxml.jackson.module.paranamer.ParanamerModule;
import com.fasterxml.jackson.module.paranamer.shaded.CachingParanamer;

public class KasperImmutabilityParanamerModule extends ParanamerModule {

    private static final long serialVersionUID = 2291876538816364449L;

    public KasperImmutabilityParanamerModule() {
        super(new CachingParanamer(new KasperParanamer()));
    }
}
