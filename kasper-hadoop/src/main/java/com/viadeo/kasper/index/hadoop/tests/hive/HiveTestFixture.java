// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.index.hadoop.tests.hive;

import com.google.common.collect.Maps;
import com.jointhegrid.hive_test.EnvironmentHack;
import com.jointhegrid.hive_test.HiveTestService;
import com.viadeo.kasper.index.hadoop.common.HadoopDependencies;
import com.viadeo.kasper.index.hadoop.hive.HiveRunner;
import com.viadeo.kasper.index.hadoop.tests.HadoopTestFixture;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

public class HiveTestFixture extends HadoopTestFixture {
    private static final Logger LOGGER = LoggerFactory.getLogger(HiveTestFixture.class);

    // ------------------------------------------------------------------------

    private HiveTestFixture(final File scriptFile) {
        super(scriptFile);
    }

    public static final HiveTestFixture forScript(final File scriptFile) {
        final HiveTestFixture hiveTestFixture = new HiveTestFixture(scriptFile);
        return hiveTestFixture;
    }

    // ------------------------------------------------------------------------

    @Override
    protected File _runForOutput() throws Throwable {

        /* Prepare paths */
        final File tmpFile = new File(outputDir(), outputFileName());
        final Path tmpFilePath = new Path("file://" + tmpFile.getAbsolutePath());

        /* Configure dependencies */
        final HadoopDependencies hadoopDeps = new HadoopDependencies(tmpFilePath);

        if (null != avroDependencies()) {
            hadoopDeps.addAvroDependencies(avroDependencies());
        }

        if (null != outputSchemaName()) {
            hadoopDeps.addSchema(outputSchemaName());
        }

        for (final HadoopDependencies.Dependency dep : hadoopDeps.getDependencies()) {
            LOGGER.info("Copy " + dep.getRemote() + " from " + dep.getLocal());
            Files.copy(
                    new FileInputStream(dep.getLocal()),
                    new File(dep.getRemote().toUri()).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        /* FIXME: find another solution without dependency on local system */
        final Map<String, String> env = Maps.newHashMap();
        env.putAll(System.getenv());
        env.put("HADOOP_HOME", "/usr");
        env.put("HADOOP_MAPRED_HOME", "/usr/lib/hadoop-0.20-mapreduce");
        EnvironmentHack.setEnv(env);

        /* Init embedded Hive */
        final HiveTest hiveTest = new HiveTest(scriptFile(), hadoopDeps);

        /* Launch test */
        hiveTest.setUp();
        hiveTest.testExecute();
        hiveTest.tearDown();

        return tmpFile;
    }

    // ------------------------------------------------------------------------

    /*
     * Hive test using MiniCluster facilities from hive_test
     * https://github.com/edwardcapriolo/hive_test
     */
    private final static class HiveTest extends HiveTestService {

        private final File scriptFile;
        private HadoopDependencies hadoopDeps;

        public HiveTest(final File scriptFile, final HadoopDependencies hadoopDeps) throws IOException {
            super();
            this.scriptFile = scriptFile;
            this.hadoopDeps = hadoopDeps;
        }

        public void testExecute() throws Exception {
            final StringBuffer hiveScript = new StringBuffer();

            /* Add properties to script */
            for (final Map.Entry<String, String> property : hadoopDeps.getProperties().entrySet()) {
                hiveScript.append(String.format("set %s=%s;\n", property.getKey(), property.getValue()));
            }

            /* Append original script */
            hiveScript.append(FileUtils.readFileToString(this.scriptFile));

            /* Execute script in test environment */
            HiveRunner.runScript(client, new ByteArrayInputStream(hiveScript.toString().getBytes()));
        }
    }

}
