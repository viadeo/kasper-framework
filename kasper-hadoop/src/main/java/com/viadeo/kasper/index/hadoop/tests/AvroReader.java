package com.viadeo.kasper.index.hadoop.tests;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

/*
 * Read an AVRO hadoop-style directory
 */
public class AvroReader {
    private static Logger LOGGER = LoggerFactory.getLogger(AvroReader.class);

    private static final String AVRO_MAGIC = "Obj";

    private final File inputFile;
    private LinkedList<File> resultFiles = Lists.newLinkedList();
    private Boolean readerInitialized = false;
    private DataFileReader<GenericRecord> dataFileReader;
    private GenericRecord genericRecord;

    // ------------------------------------------------------------------------

    public AvroReader(final File inputDirectory) {
        this.inputFile = inputDirectory;
    }

    // ------------------------------------------------------------------------

    /* Reset the reader */
    public void reset() {
        readerInitialized = false;
        resultFiles = Lists.newLinkedList();
        dataFileReader = null;
        genericRecord = null;
    }

    /* Read an hadoop directory of splitted AVRO files, returns the next available record */
    public boolean hasNext() throws IOException {

        /* Store all available AVRO files */
        if (!readerInitialized) {

            File[] files = null;
            if (inputFile.isDirectory()) {
                LOGGER.info("Analyze generated output AVRO files in " + inputFile.getAbsolutePath());
                files = inputFile.listFiles();
            } else if (inputFile.exists()) {
                LOGGER.info("Analyze generated output AVRO file " + inputFile.getAbsolutePath());
                files = new File[] { this.inputFile};
            } else {
                LOGGER.error("Try to read unexistent or unmanageable file : " + inputFile);
            }

            if (null != files) {
                for (final File file : files) { /* Search for valid AVRO files */
                    InputStream fileStream = null;
                    try {
                        fileStream = new FileInputStream(file);
                        final byte[] chunkMagic = new byte[AVRO_MAGIC.length()];
                        final int chunkRead = fileStream.read(chunkMagic);
                        if ((AVRO_MAGIC.length() == chunkRead) &&
                                new String(chunkMagic).contentEquals(AVRO_MAGIC)) {
                            LOGGER.info("Output result has file " + file.getAbsolutePath());
                            resultFiles.add(file);
                        }
                    } finally {
                        if (null != fileStream) {
                            fileStream.close();
                        }
                    }
                }
            }

            readerInitialized = true;
        }

        if ((null == dataFileReader) || !dataFileReader.hasNext()) {
            if (!resultFiles.isEmpty()) {

                final File nextAvro = resultFiles.poll();
                LOGGER.info("Now read output AVRO file " + nextAvro.getAbsolutePath());

                final DatumReader<GenericRecord> datumReader = new GenericDatumReader<GenericRecord>();
                dataFileReader = new DataFileReader<GenericRecord>(nextAvro, datumReader);
                genericRecord = new GenericData.Record(dataFileReader.getSchema());

            } else {
                return false;
            }
        }

        return dataFileReader.hasNext();
    }

    public Optional<GenericRecord> next() throws IOException {
        if (this.hasNext()) {
            return Optional.of(dataFileReader.next(genericRecord));
        } else {
            return Optional.absent();
        }
    }

}
