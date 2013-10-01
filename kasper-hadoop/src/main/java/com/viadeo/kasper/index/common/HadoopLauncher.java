package com.viadeo.kasper.index.common;

import java.util.Collection;

public interface HadoopLauncher {

    Collection<String> getJobs();

    boolean hasJob(String jobName);

    void executeJob(HadoopJobConfiguration pc) throws Exception;

}
