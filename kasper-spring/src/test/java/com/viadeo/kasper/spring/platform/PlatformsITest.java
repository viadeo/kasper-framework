// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.platform;

import com.viadeo.kasper.platform.Platform;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PlatformsITest {

    @Test
    public void new_spring_platform_is_ready_to_use() {
        Platform platform = Platforms.newSpringPlatformBuilder().build();

        assertNotNull(platform);
        assertNotNull(platform.getCommandGateway());
        assertNotNull(platform.getQueryGateway());
        assertNotNull(platform.getEventBus());
        assertNotNull(platform.getMeta());
        assertNotNull(platform.getMetricRegistry());
    }
}
