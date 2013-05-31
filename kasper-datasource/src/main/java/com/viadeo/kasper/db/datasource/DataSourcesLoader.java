package com.viadeo.kasper.db.datasource;

import com.viadeo.kasper.JSONConfigurationLoader;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: kris
 * Date: 5/28/13
 * Time: 4:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataSourcesLoader {


    /**
     * Read existing datasource configuration in Json Format
     * @param configFile
     * @return
     */
    public static DataSourcesSettings read(String configFile) throws IOException {
        File config = JSONConfigurationLoader.getFile(configFile);
        DataSourcesSettings settings = JSONConfigurationLoader.load(config, DataSourcesSettings.class);
        return settings;
    }

}
