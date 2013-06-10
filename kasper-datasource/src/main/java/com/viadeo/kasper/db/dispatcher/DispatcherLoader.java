// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.db.dispatcher;

import com.viadeo.kasper.JSONConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class DispatcherLoader {

    /**
     * Read existing dispatcher configuration in Json Format
     * @param configFile
     * @return the dispatcher settings
     */
    public static DispatcherSettings read(final String configFile) throws IOException {
        final File config = JSONConfigurationLoader.getFile(configFile);
        final DispatcherSettings settings = JSONConfigurationLoader.load(config, DispatcherSettings.class);

        return settings;
    }


}
