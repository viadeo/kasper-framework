// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.configuration;

import com.viadeo.kasper.platform.Platform;
import org.junit.Test;

public class PlatformFactoryTest {

    @Test
    public void platformShouldBeBuiltByFactoryWithoutError() {
       final PlatformFactory factory = new PlatformFactory();
       final Platform platform = factory.getPlatform();
    }

}
