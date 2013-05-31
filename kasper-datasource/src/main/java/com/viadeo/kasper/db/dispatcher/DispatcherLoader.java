package com.viadeo.kasper.db.dispatcher;

import com.viadeo.kasper.JSONConfigurationLoader;

import java.io.File;
import java.io.IOException;


public class DispatcherLoader {


    /**
     * Read existing dispatcher configuration in Json Format
     * @param configFile
     * @return
     */
    public static DispatcherSettings read(String configFile) throws IOException {
        File config = JSONConfigurationLoader.getFile(configFile);
        DispatcherSettings settings = JSONConfigurationLoader.load(config, DispatcherSettings.class);
        return settings;
    }


}
