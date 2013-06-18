// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.shard.settings;

import java.io.File;
import java.io.IOException;

public class DataSourcesLoader {

    /**
     * Read existing datasource configuration in Json Format
     * @param configFile
     * @return the datasource settings
     */
    public static DataSourcesSettings read(final String configFile) throws IOException {
        final File config = JSONConfigurationLoader.getFile(configFile);
        final DataSourcesSettings settings = JSONConfigurationLoader.load(config, DataSourcesSettings.class);

        return settings;
    }

}
