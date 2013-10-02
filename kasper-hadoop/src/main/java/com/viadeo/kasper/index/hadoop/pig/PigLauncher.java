// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.index.hadoop.pig;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.index.hadoop.common.HadoopDependencies;
import com.viadeo.kasper.index.hadoop.common.HadoopJobConfiguration;
import com.viadeo.kasper.index.hadoop.common.HadoopLauncher;
import org.apache.hadoop.fs.FileSystem;
import org.apache.pig.ExecType;
import org.apache.pig.PigServer;
import org.apache.pig.backend.executionengine.ExecException;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.viadeo.kasper.index.hadoop.common.JavaHelpers.cnn;

/*
 * Main Pig launcher
 */
public class PigLauncher implements HadoopLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(PigLauncher.class);

    // ------------------------------------------------------------------------

    public Collection<String> getJobs() {
        final Collection<String> jobs = Lists.newArrayList();

        final Predicate<String> filter = new FilterBuilder().include(".*\\.pig");
        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(filter)
                .setUrls(ClasspathHelper.forClassLoader())
                .setScanners(new ResourcesScanner()));

        final Set<String> resources = reflections.getResources(Pattern.compile(".*"));
        for (final String resource : resources) {
            if (!resource.contains("/")) {
                final String jobName = resource.replace(".pig", "").replaceAll("^.*/", "");
                jobs.add(jobName);
            }
        }

        return jobs;
    }

    public boolean hasJob(final String jobName) {
        return ClassLoader.class.getResource("/" + jobName + ".pig") != null;
    }

    // ------------------------------------------------------------------------

    @Override
    public void executeJob(final HadoopJobConfiguration pc) throws Exception {

        final FileSystem fs = cnn(pc.fileSystem);
        final HadoopDependencies hDeps = cnn(pc.hadoopDependencies);

        /* Access and parse script */
        final InputStream pigJobStream = ClassLoader.class.getResourceAsStream("/" + pc.jobName + ".pig");
        if (null == pigJobStream) {
            throw new RuntimeException("Specified Pig job cannot be found");
        }
        hDeps.recordAvroAndSchemaDependenciesFromTextScript(pigJobStream);

        /* Set script properties */
        final Map<String, String> pigArgs = Maps.newHashMap(hDeps.getProperties());
        LOGGER.info("Register script parameters.. : \n" + pigArgs.toString());

        /* Create the Pig runtime environment */
        final PigServer pigServer;
        try {
            LOGGER.info("Create the Pig runtime environment");
            pigServer = new PigServer(ExecType.MAPREDUCE);
        } catch (final ExecException e) {
            throw new RuntimeException(" -> Unable to initialize Map/Reduce configuration.", e);
        }

        /* Execute the Pig script */
        LOGGER.info("Run Pig job..");
        try {
            final InputStream pigJobStream2 = ClassLoader.class.getResourceAsStream("/" + pc.jobName + ".pig");
            pigServer.registerScript(pigJobStream2, pigArgs);
        } catch (final IOException e) {
            throw new RuntimeException(" -> Unable to open script for the job", e);
        }
    }

}
