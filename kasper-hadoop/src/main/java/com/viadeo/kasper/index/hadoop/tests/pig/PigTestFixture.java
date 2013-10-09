// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.index.hadoop.tests.pig;

import com.google.common.collect.Lists;
import com.viadeo.kasper.index.hadoop.common.HadoopDependencies;
import com.viadeo.kasper.index.hadoop.tests.HadoopTestFixture;
import org.apache.hadoop.fs.Path;
import org.apache.pig.pigunit.Cluster;
import org.apache.pig.pigunit.PigTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.apache.pig.pigunit.PigTest.getCluster;

public class PigTestFixture extends HadoopTestFixture {
    private static final Logger LOGGER = LoggerFactory.getLogger(PigTestFixture.class);

    // ------------------------------------------------------------------------

    private PigTestFixture(final File scriptFile) {
        super(scriptFile);
    }

    public static final PigTestFixture forScript(final File scriptFile) {
        final PigTestFixture pigTestFixture = new PigTestFixture(scriptFile);
        return pigTestFixture;
    }

    // ------------------------------------------------------------------------

    @Override
    protected File _runForOutput() throws Throwable {
        final Cluster pigCluster = getCluster();

        /* Create a temporary directory */
        final File outputAvro = new File(outputDir(), outputFileName());

        /* Initialize Pig args */
        final List<String> pigArgs = Lists.newArrayList();
        pigArgs.add("n=3");
        pigArgs.add("reducers=1");

        /* Push avro files and dependencies to virtual Hadoop and set Pig parameters */
        final HadoopDependencies hadoopDeps = new HadoopDependencies(
                new Path("file://" + outputAvro.getAbsolutePath()));

        if (null != avroDependencies()) {
            hadoopDeps.addAvroDependencies(avroDependencies());
        }

        if (null != outputSchemaName()) {
            hadoopDeps.addSchema(outputSchemaName());
        }

        for (final HadoopDependencies.Dependency dep : hadoopDeps.getDependencies()) {
            pigCluster.update(
                    dep.getLocalAsPath(),
                    dep.getRemote()
            );
        }

        for (final Map.Entry<String, String> property : hadoopDeps.getProperties().entrySet()) {
            final String argumentString = String.format("%s=%s", property.getKey(), property.getValue());
            LOGGER.info("Added Pig argument : " + argumentString);
            pigArgs.add(argumentString);
        }

        /* Execute Pig script */
        LOGGER.info(String.format("PIG ARGUMENTS :: \n%s", Arrays.toString(pigArgs.toArray())));
        final String[] args = new String[pigArgs.size()];
        final PigTest pigTest = new PigTest(scriptFile().getAbsolutePath(), pigArgs.toArray(args));
        pigTest.unoverride("STORE");
        pigTest.runScript();

        return outputAvro;
    }

}
