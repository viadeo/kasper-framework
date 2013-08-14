// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.configuration;

import org.junit.Test;

public class PlatformFactoryTest {

    @Test
    public void platformShouldBeBuiltByFactoryWithoutError() {
       final PlatformFactory factory = new PlatformFactory();
       factory.getPlatform();
    }

}
