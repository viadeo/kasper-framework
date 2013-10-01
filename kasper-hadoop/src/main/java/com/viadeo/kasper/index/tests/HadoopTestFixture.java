package com.viadeo.kasper.index.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public abstract class HadoopTestFixture {
    private static final Logger LOGGER = LoggerFactory.getLogger(HadoopTestFixture.class);

    private File scriptFile = null;

    private File[] avroFiles = null;

    private File outputDir = null;
    private String outputFileName = null;
    private String outputSchemaName = null;

    // ------------------------------------------------------------------------

    protected HadoopTestFixture(final File scriptFile) {
        this.scriptFile = scriptFile;
    }

    protected File scriptFile(){
        return this.scriptFile;
    }

    // ------------------------------------------------------------------------

    public HadoopTestFixture withOutputDir(final File outputDir) {
        this.outputDir = outputDir;
        return this;
    }

    public HadoopTestFixture withOutputFileName(final String outputFileName) {
        this.outputFileName = outputFileName;
        return this;
    }

    public HadoopTestFixture withOutputSchemaName(final String outputSchemaName) {
        this.outputSchemaName = outputSchemaName;
        return this;
    }

    public HadoopTestFixture withAvroDependencies(final File...avroFileDependencies) {
        this.avroFiles = avroFileDependencies;
        return this;
    }

    // ------------------------------------------------------------------------

    protected File outputDir() {
        return this.outputDir;
    }

    protected String outputFileName() {
        return this.outputFileName;
    }

    protected String outputSchemaName() {
        return this.outputSchemaName;
    }

    protected File[] avroDependencies(){
        return this.avroFiles;
    }

    // ------------------------------------------------------------------------

    public File runForOutput() throws Throwable {
        /* Default output dir */
        if (null == this.outputDir()) {
            this.outputDir = new File(
                    System.getProperty("java.io.tmpdir"),
                    Long.toString(System.nanoTime())
            );
        }

        /* default output file name */
        if (null == outputFileName) {
            this.outputFileName = "output.avro";
        }

        return this._runForOutput();
    }

    protected abstract File _runForOutput() throws Throwable;

}
