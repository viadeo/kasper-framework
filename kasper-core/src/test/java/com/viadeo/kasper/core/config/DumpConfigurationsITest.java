// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Kasper Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.config;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class DumpConfigurationsITest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DumpConfigurationsITest.class);

    // ------------------------------------------------------------------------

    private String getConfigurationStringForEnv(final String environment) {
        return new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .forcedEnvironment(environment)
        ).getConfiguration().root().render();
    }

    // ------------------------------------------------------------------------

    @Test
    public void dumpConfigurations() throws Exception {

        LOGGER.info("** TEST configuration dump");
        dumpConfToFile("test", getConfigurationStringForEnv("test"));

        LOGGER.info("** DEMO configuration dump");
        dumpConfToFile("demo", getConfigurationStringForEnv("demo"));

        LOGGER.info("** PREPROD configuration dump");
        dumpConfToFile("preprod", getConfigurationStringForEnv("preprod"));

        LOGGER.info("** PROD configuration dump");
        dumpConfToFile("prod", getConfigurationStringForEnv("prod"));

    }

    // ------------------------------------------------------------------------

    private void dumpConfToFile(final String confName, final String confData) throws FileNotFoundException {
        final PrintWriter os = new PrintWriter("build/" + confName + ".conf");
        os.write(confData);
    }

}
