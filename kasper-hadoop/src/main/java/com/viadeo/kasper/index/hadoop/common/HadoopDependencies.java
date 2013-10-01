package com.viadeo.kasper.index.hadoop.common;

import com.google.common.collect.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Provides Hadoop dependencies and properties
 */
public class HadoopDependencies {
    private static final Logger LOGGER = LoggerFactory.getLogger(HadoopDependencies.class);

    private static final String HADOOP_LIB_PIGGYBANK = "piggybank.jar";
    private static final String HADOOP_LIB_ROOT = "build/extlib"; /* Created by gradle */
    private static final String AVRO_SCHEMA_DIR = "src/main/avro";

    private final Path avroDir;
    private final Path libDir;
    private final Path schemaDir;
    private final Path outputDir;

    public static enum DependencyType{
        LIBRARY, AVRODATA, AVROSCHEMA
    }

    public static final class Dependency {

        private final File localPath;
        private final Path remotePath;
        private final DependencyType type;

        public Dependency(final InputStream localStream, final Path remotePath, final DependencyType type) {
            LOGGER.info(String.format("Create %s dependency into %s from input stream", type, remotePath));

            final File tmpFile = new File(
                    System.getProperty("java.io.tmpdir"),
                    Long.toString(System.nanoTime()) + "-" + remotePath.getName()
            );

            try {
                IOUtils.copy(localStream, FileUtils.openOutputStream(tmpFile));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }

            this.localPath = tmpFile;
            this.remotePath = remotePath;
            this.type = type;
        }

        public Dependency(final File localPath, final Path remotePath, final DependencyType type) {
            LOGGER.info(String.format("Create %s dependency into %s from file %s", type, remotePath, localPath));

            if (!localPath.exists()) {
                throw new RuntimeException(String.format("File %s does not exists on local filesystem !", localPath));
            }

            this.localPath = localPath;
            this.remotePath = remotePath;
            this.type = type;
        }

        public File getLocal() {
            return this.localPath;
        }

        public Path getLocalAsPath() {
            return new Path(getLocal().getAbsolutePath());
        }

        public Path getRemote() {
            return this.remotePath;
        }

        public DependencyType getType() {
            return this.type;
        }

    }

    protected final List<Path> requiredDependencies = Lists.newArrayList();
    protected final Map<String, String> properties = Maps.newHashMap();
    protected final Collection<Dependency> libraries = Lists.newArrayList();
    protected final Collection<Dependency> avros = Lists.newArrayList();

    // ------------------------------------------------------------------------

    public HadoopDependencies(final Path outFile) {
        this(outFile, outFile.getParent());
    }

    public HadoopDependencies(final Path outFile, final Path libDir) {
        this(outFile, libDir, libDir);
    }

    public HadoopDependencies(final Path outFile, final Path libDir, final Path avroDir) {
        this(outFile, libDir, avroDir, avroDir);
    }

    public HadoopDependencies(final Path outFile, final Path libDir, final Path avroDir,
                              final Path schemaDir) {

        this.libDir = libDir;
        this.avroDir = avroDir;
        this.schemaDir = schemaDir;
        this.outputDir = outFile.getParent();

        /* PiggyBank */
        final InputStream piggyStream = ClassLoader.getSystemClassLoader().getResourceAsStream(HADOOP_LIB_PIGGYBANK);
        final Path piggyBank = new Path(libDir, HADOOP_LIB_PIGGYBANK);
        libraries.add(new Dependency(piggyStream, piggyBank, DependencyType.LIBRARY));
        properties.put("LIB_PIGGYBANK", piggyBank.toString());

        /* Other dependencies */
        final File[] stdDeps = new File(HADOOP_LIB_ROOT).listFiles();
        if (null != stdDeps) {
            for (final File stdDep : stdDeps) {
                if (stdDep.getName().endsWith(".jar")) {
                    final Path hadoopStdDep = new Path(libDir, stdDep.getName());
                    libraries.add(new Dependency(stdDep, hadoopStdDep, DependencyType.LIBRARY));
                }
            }
        }

        /* Default library root directory */
        properties.put("LIB_ROOT", libDir.toString());
        properties.put("DATA_AVRO_OUTPUT_DIR", outFile.getParent().toString());
        properties.put("DATA_AVRO_OUTPUT_FILE", outFile.toString());
        properties.put("DATA_AVRO_OUTPUT_FILENAME", outFile.getName().replace(".avro", "") + ";\n");
    }

    // ------------------------------------------------------------------------

    public ImmutableMap<String, String> getProperties() {
        return ImmutableMap.copyOf(this.properties);
    }

    public ImmutableCollection<Dependency> getLibraries() { /* TODO: add a filter for libraries */
        return ImmutableList.copyOf(this.libraries);
    }

    // ------------------------------------------------------------------------

    public HadoopDependencies addAvroDependencies(final File... avroFiles) {
        for (final File avro : avroFiles) {
            final String avroName = avro.getName().replace(".avro", "");
            final Path hadoopAvroFile = new Path(libDir, avro.getName());
            this.avros.add(new Dependency(avro, hadoopAvroFile, DependencyType.AVRODATA));
            this.properties.put(String.format("DATA_AVRO_%s", avroName.toUpperCase()),
                    hadoopAvroFile.toString());
            this.addSchema(avroName);
        }
        return this;
    }

    /*
     * Declare a remote file dependency which will not be uploaded
     */
    public HadoopDependencies addRequiredAvroDependencies(final String... avroNames) {
         for (String avro : avroNames) {
            avro = avro.replace(".avro", "");
            final Path hadoopAvroFile = new Path(avroDir, avro + ".avro");
            this.properties.put(String.format("DATA_AVRO_%s", avro.toUpperCase()), hadoopAvroFile.toString());
            this.addSchema(avro);
            this.requiredDependencies.add(hadoopAvroFile);
        }
        return this;
    }

    public HadoopDependencies addSchema(final String... schemas) {
        for (String schema : schemas) {
            schema = schema.replace(".avsc", "");
            final File avroSchema = new File(AVRO_SCHEMA_DIR, schema + ".avsc");
            final Path hadoopAvroSchema = new Path(schemaDir, schema + ".avsc");
            libraries.add(new Dependency(avroSchema, hadoopAvroSchema, DependencyType.AVROSCHEMA));
            properties.put(String.format("SCHEMA_AVRO_%s",
                    avroSchema.getName().replace(".avsc", "").toUpperCase()),
                    hadoopAvroSchema.toString());
        }
        return this;
    }

    public ImmutableCollection<Dependency> getAvros() { /* TODO: add a filter for avros */
        return ImmutableList.copyOf(this.avros);
    }

    // ------------------------------------------------------------------------

    public ImmutableCollection<Dependency> getDependencies() {
        final List<Dependency> deps = Lists.newArrayList(this.avros);
        deps.addAll(this.libraries);
        return ImmutableList.copyOf(deps);
    }

    public ImmutableCollection<Path> getRequiredAvroDependencies() {
        return ImmutableList.copyOf(this.requiredDependencies);
    }

    // ------------------------------------------------------------------------

    /*
     * Parse any text script (Pig or Hive) searching for patterns :
     *
     * -- REQUIRE AVRO <AvroName>
     * -- REQUIRE SCHEMA <AvroSchemaName>
     *
     * and add them to this Hadoop dependency manager
     *
     * AVRO files will not be upload, their presence is expected on remote HDFS
     * AVRO schemas will be uploaded in the specified remote schema directory (see <init> method)
     *
     */
    public void recordAvroAndSchemaDependenciesFromTextScript(final InputStream inputTextScriptStream) throws IOException {
        String line;
        final BufferedReader br = new BufferedReader(new InputStreamReader(inputTextScriptStream));
        final Pattern p = Pattern.compile("\\s*--\\s*REQUIRE\\s*(AVRO|SCHEMA)\\s*([a-zA-Z]*)");
        while ((line = br.readLine()) != null) {
            final Matcher matcher = p.matcher(line);
            if (matcher.find()) {
                final String avroType = matcher.group(1);
                final String avroName = matcher.group(2);

                if (avroType.contentEquals("AVRO")) {
                    this.addRequiredAvroDependencies(avroName);
                } else if (avroType.contentEquals("SCHEMA")) {
                    this.addSchema(avroName);
                }
            }
        }
        br.close();
    }

    // ------------------------------------------------------------------------

    public void checkHadoopDependencies(final FileSystem fs) throws IOException {

        /* Check required AVRO dependencies */
        for (final Path avroPath : this.requiredDependencies) {
            LOGGER.info("Ensure required AVRO is present on remote : " + avroPath);
            if (!fs.exists(avroPath)) {
                throw new RuntimeException(String.format("AVRO file %s is not present on remote !", avroPath));
            }
        }

        /* Check lib dependencies */
        for (final HadoopDependencies.Dependency library : this.libraries) {
            LOGGER.info(String.format("Ensure %s dependency is present on remote : %s",
                    library.getType(), library.getRemote()));
            try {
                if (!fs.exists(library.getRemote())) {
                    LOGGER.info(" -> Upload dependency from " + library.getLocal());
                    fs.mkdirs(library.getRemote().getParent());
                    fs.copyFromLocalFile(library.getLocalAsPath(), library.getRemote());
                }
            } catch (final IOException e) {
                throw new RuntimeException("Error when trying to communicate with remote HDFS", e);
            }
        }

    }

}
