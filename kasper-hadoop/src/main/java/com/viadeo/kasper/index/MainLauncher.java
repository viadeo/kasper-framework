package com.viadeo.kasper.index;

import com.google.common.collect.Lists;
import com.viadeo.kasper.index.common.HadoopDependencies;
import com.viadeo.kasper.index.common.HadoopJobConfiguration;
import com.viadeo.kasper.index.common.HadoopLauncher;
import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.viadeo.kasper.index.common.JavaHelpers.addClasspath;
import static com.viadeo.kasper.index.common.JavaHelpers.cnn;

/*
 * Main Pig launcher
 */
public class MainLauncher implements HadoopLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainLauncher.class);

    private static Options options;

    public static final boolean DEFAULT_OVERWRITE_OUTPUT = false;

    // ------------------------------------------------------------------------

    private static Map<String, HadoopLauncher> launchers = new HashMap<String, HadoopLauncher>() {
        {
            final String basePackage = MainLauncher.class.getPackage().getName();

            final Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .filterInputsBy(new FilterBuilder().includePackage(basePackage))
                    .setUrls(ClasspathHelper.forPackage(basePackage))
                    .setScanners(new SubTypesScanner()));

            final Set<Class<? extends HadoopLauncher>> classes = reflections.getSubTypesOf(HadoopLauncher.class);
            for (final Class<? extends HadoopLauncher> clazz : classes) {
                if (!clazz.equals(MainLauncher.class)) {
                    try {
                        this.put(clazz.getSimpleName().replace("Launcher", ""), clazz.newInstance());
                    } catch (final InstantiationException e) {
                        throw new RuntimeException(e);
                    } catch (final IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    };

    // ------------------------------------------------------------------------

    public static void main(final String[] args) throws Exception {
        final CommandLine cline = parseOptions(args);

        /* Prints help */
        if (cline.hasOption("help")) {
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "HadoopLauncher", options );
            return;
        }

        final MainLauncher launcher = new MainLauncher();

        /* Lists jobs */
        if (cline.hasOption("list")) {
            launcher.getJobs();
            return;
        }

        /* Prepare job configuration */
        final HadoopJobConfiguration jobConf = new HadoopJobConfiguration();

        if (!cline.hasOption("jobName")) {
            throw new RuntimeException("Please specify Pig job name (--help)");
        }
        jobConf.jobName = cline.getOptionValue("jobName");

        if (cline.hasOption("remoteLibDir")) {
            jobConf.libDir = cline.getOptionValue("remoteLibDir");
        }

        if (cline.hasOption("remoteSchemaDir")) {
            jobConf.schemaDir = cline.getOptionValue("remoteSchemaDir");
        }

        if (cline.hasOption("remoteAvroDir")) {
            jobConf.avroDir = cline.getOptionValue("remoteAvroDir");
        }

        if (cline.hasOption("hadoopConfDir")) {
            jobConf.hadoopConfDir = cline.getOptionValue("hadoopConfDir");
        }

        jobConf.outputFile = new File(jobConf.avroDir, HadoopJobConfiguration.DEFAULT_OUTPUT_FILENAME).getAbsolutePath();
        if (cline.hasOption("remoteoutputFile")) {
            jobConf.outputFile = cline.getOptionValue("remoteOutputFile");
        }

        if (cline.hasOption("hiveHost")) {
            jobConf.hiveHost = cline.getOptionValue("hiveHost");
        }

        if (cline.hasOption("hivePort")) {
            jobConf.hivePort = Integer.parseInt(cline.getOptionValue("hivePort"));
        }

        if (cline.hasOption("hivePath")) {
            jobConf.hivePath = cline.getOptionValue("hivePath");
        }

        /* Prepare MR configuration and initialize HDFS client */
        addClasspath(cnn(jobConf.hadoopConfDir));
        final Configuration conf = new Configuration();
        final FileSystem fs;
        try {
            LOGGER.info("Try to access HDFS filesystem");
            fs = FileSystem.get(conf);
            LOGGER.info(" -> Connected to " + fs.getUri());
        } catch (final IOException e) {
            throw new RuntimeException(" -> Unable to gain access to the remote HDFS", e);
        }
        jobConf.fileSystem = fs;

         /* Initialize Hadoop dependencies manager */
        final HadoopDependencies hadoopDependencies = new HadoopDependencies(
                fs.makeQualified(new Path(cnn(jobConf.outputFile))),
                fs.makeQualified(new Path(cnn(jobConf.libDir))),
                fs.makeQualified(new Path(cnn(jobConf.avroDir))),
                fs.makeQualified(new Path(cnn(jobConf.schemaDir)))
        );
        jobConf.hadoopDependencies = hadoopDependencies;

        /* Delete output file if required */
        boolean overwriteOutput = DEFAULT_OVERWRITE_OUTPUT;
        if (cline.hasOption("overwriteOutput")) {
            overwriteOutput = true;
        }

        final Path outputPath = fs.makeQualified(new Path(jobConf.outputFile));
        if (!fs.exists(outputPath.getParent())) {
            fs.mkdirs(outputPath.getParent());
        } else if (fs.exists(outputPath) && overwriteOutput) {
            fs.delete(outputPath, true);
        }

        /* Execute Pig job*/
        launcher.executeJob(jobConf);
    }

    // ------------------------------------------------------------------------

    public Collection<String> getJobs() {
        LOGGER.info("List of embedded jobs : ");
        final List<String> allJobs = Lists.newArrayList();
        for (final Map.Entry<String, HadoopLauncher> launcherEntry : launchers.entrySet()) {
            final Collection<String> jobs = launcherEntry.getValue().getJobs();
            LOGGER.info("  => " + launcherEntry.getKey());
            if (!jobs.isEmpty()) {
                for (final String job : jobs) {
                    LOGGER.info("    -> " + job);
                }
                allJobs.addAll(jobs);
            } else {
                LOGGER.info("    -> no jobs found");
            }
        }
        return allJobs;
    }

    public boolean hasJob(final String jobName) {
        for (final HadoopLauncher launcher : launchers.values()) {
            if (launcher.hasJob(jobName)) {
                return true;
            }
        }
        return false;
    }

    public void executeJob(final HadoopJobConfiguration pc) throws Exception {
        for (final Map.Entry<String, HadoopLauncher> launcherEntry : launchers.entrySet()) {
            if (launcherEntry.getValue().hasJob(pc.jobName)) {
                LOGGER.info("==> Launch job implemented using hadoop MR client : " + launcherEntry.getKey());
                launcherEntry.getValue().executeJob(pc);
                return;
            }
        }
        LOGGER.info("==> No implementation found for job : " + pc.jobName);
    }

    //= HELPERS ===============================================================

    @SuppressWarnings("static-access")
    public static CommandLine parseOptions(final String[] args) throws ParseException {
        final CommandLineParser parser = new PosixParser();
        options = new Options();

        options.addOption(new Option( "help", "Print this message" ));
        options.addOption(new Option( "list", "List all available jobs" ));
        options.addOption(OptionBuilder.withArgName("jobName")
                                       .hasArg()
                                       .withDescription("The name of the job to launch")
                                       .create("jobName"));
        options.addOption(OptionBuilder.withArgName("remoteLibDir")
                                       .hasArg()
                                       .withDescription("The path to the remote dependencies directory (/tmp)")
                                       .create("remoteLibDir"));
        options.addOption(OptionBuilder.withArgName("remoteAvroDir")
                                       .hasArg()
                                       .withDescription("The path to the remote AVRO files directory (/tmp)")
                                       .create("remoteAvroDir"));
        options.addOption(OptionBuilder.withArgName("remoteSchemaDir")
                                       .hasArg()
                                       .withDescription("The path to the remote AVRO schema files directory (/tmp)")
                                       .create("remoteSchemaDir"));
        options.addOption(OptionBuilder.withArgName("remoteOutputFile")
                                       .hasArg()
                                       .withDescription("The path to the remote output file to be generated ({remoteAvroDir}/output.avro)")
                                       .create("remoteOutputFile"));
        options.addOption(OptionBuilder.withArgName("hadoopConfDir")
                                       .hasArg()
                                       .withDescription("The path to the local Hadoop configuration (/etc/hadoop/conf)")
                                       .create("hadoopConfDir"));
        options.addOption(OptionBuilder.withArgName("overwriteOutput")
                                       .withDescription("If set, the output file will be overwritten (false)")
                                       .create("overwriteOutput"));

        options.addOption(OptionBuilder.withArgName("hiveHost")
                                       .withDescription("The Hive database host (127.0.0.1)")
                                       .create("hiveHost"));
        options.addOption(OptionBuilder.withArgName("hivePort")
                                       .withDescription("The Hive database port (10000)")
                                       .create("hivePort"));
        options.addOption(OptionBuilder.withArgName("hivePath")
                                       .withDescription("The Hive database path (default)")
                                       .create("hivePath"));

        return parser.parse(options, args);
    }

}
