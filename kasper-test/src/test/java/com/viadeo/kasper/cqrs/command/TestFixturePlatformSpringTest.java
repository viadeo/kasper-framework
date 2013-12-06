// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command;

import com.viadeo.kasper.test.platform.KasperPlatformFixture;
import org.junit.Before;

public class TestFixturePlatformSpringTest extends TestFixturePlatformTest {

    @Before
    @Override
    public void resetFixture() {
        this.fixture = KasperPlatformFixture
                .fromSpring(
                        FixtureUseCaseSpringConfiguration.class,
                        this.getClass().getPackage().getName()
                )
                .withStrictSpringInstanceStrategy();
    }

}
