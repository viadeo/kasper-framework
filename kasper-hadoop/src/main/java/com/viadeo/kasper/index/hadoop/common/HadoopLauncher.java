// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.index.hadoop.common;

import java.util.Collection;

public interface HadoopLauncher {

    Collection<String> getJobs();

    boolean hasJob(String jobName);

    void executeJob(HadoopJobConfiguration pc) throws Exception;

}
