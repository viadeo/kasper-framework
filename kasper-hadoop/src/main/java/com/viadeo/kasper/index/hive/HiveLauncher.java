package com.viadeo.kasper.index.hive;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.viadeo.kasper.index.common.HadoopDependencies;
import com.viadeo.kasper.index.common.HadoopJobConfiguration;
import com.viadeo.kasper.index.common.HadoopLauncher;
import com.viadeo.kasper.index.common.JavaHelpers;
import org.apache.hadoop.fs.FileSystem;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.index.common.JavaHelpers.cnn;

/*
 * Main Pig launcher
 */
public class HiveLauncher implements HadoopLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(HiveLauncher.class);

    private static final String JDBC_HIVE_DRIVER_NAME = "org.apache.hadoop.hive.jdbc.HiveDriver";

    static {
        try {
            Class.forName(JDBC_HIVE_DRIVER_NAME);
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
            new RuntimeException("HiveJDBC driver cannot be found !", e);
        }
    }

    // ------------------------------------------------------------------------

    public Collection<String> getJobs() {
        final Collection<String> jobs = Lists.newArrayList();

        final Predicate<String> filter = new FilterBuilder().include(".*\\.hive");
        final Reflections reflections = new Reflections(new ConfigurationBuilder()
                .filterInputsBy(filter)
                .setUrls(ClasspathHelper.forClassLoader())
                .setScanners(new ResourcesScanner()));

        final Set<String> resources = reflections.getResources(Pattern.compile(".*"));
        for (final String resource : resources) {
            if (!resource.contains("/")) {
                final String jobName = resource.replace(".hive", "").replaceAll("^.*/", "");
                jobs.add(jobName);
            }
        }

        return jobs;
    }

    public boolean hasJob(final String jobName) {
        return ClassLoader.class.getResource("/" + jobName + ".hive") != null;
    }

    // ------------------------------------------------------------------------

    @Override
    public void executeJob(final HadoopJobConfiguration pc) throws Exception {

        /* Open JDBC connection to hive */
        final String jdbcUri = String.format(
                "jdbc:hive://%s:%d/%s",
                pc.hiveHost,
                pc.hivePort,
                pc.hivePath);

        final Connection con = DriverManager.getConnection(jdbcUri);

        /* Init HDFS client */
        final StringBuffer finalScript = new StringBuffer();

        final FileSystem fs = cnn(pc.fileSystem);
        final HadoopDependencies hDeps = cnn(pc.hadoopDependencies);

        /* Access and parse script */
        final InputStream hiveJobStream = ClassLoader.class.getResourceAsStream("/" + pc.jobName + ".hive");
        if (null == hiveJobStream) {
            throw new RuntimeException("Specified Hive job cannot be found");
        }
        hDeps.recordAvroAndSchemaDependenciesFromTextScript(hiveJobStream);

        /* Set script properties */
        for (final Map.Entry<String, String> property : hDeps.getProperties().entrySet()) {
            finalScript.append(String.format("set %s=%s;\n", property.getKey(), property.getValue()));
        }

        /* Append original script to final one */
        final InputStream hiveJobStream2 = ClassLoader.class.getResourceAsStream("/" + pc.jobName + ".hive");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(hiveJobStream2));
        char[] buffer = new char[64];
        while ( -1 != reader.read(buffer, 0, 64) ) {
            finalScript.append(buffer);
        }
        reader.close();

        /* FIXME: Sets Hadoop environment */
        JavaHelpers.setEnv("HADOOP_HOME", "/usr");
        JavaHelpers.setEnv("HADOOP_MAPRED_HOME", "/usr/lib/hadoop-0.20-mapreduce");

        /* Run the script using HiveRunner */
        final String finalScriptString = finalScript.toString();
        LOGGER.debug(finalScriptString);
        HiveRunner.runScript(con, new ByteArrayInputStream(finalScriptString.getBytes()));
    }

}
