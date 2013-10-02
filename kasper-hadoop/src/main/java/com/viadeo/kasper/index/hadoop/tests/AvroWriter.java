// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.index.hadoop.tests;

import com.google.common.collect.Lists;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/*
 * Write objects to an AVRO file
 */
public class AvroWriter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AvroWriter.class);

    private final Class<?> clazz;
    private final Schema schema;

    // ------------------------------------------------------------------------

    public AvroWriter(final Class<?> clazz, final Schema schema) {
        this.clazz = clazz;
        this.schema = schema;
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings({"rawtypes", "unchecked"})
    public final File write(final List<?> data, File file) throws IOException {
        LOGGER.info(String.format("Generate AVRO file for class %s in %s", clazz.getSimpleName(), file.getAbsolutePath()));

        if (file.isDirectory()) {
            file = new File(file, clazz.getSimpleName() + ".avro");
        }

        final DatumWriter writer = new SpecificDatumWriter(clazz);
        final DataFileWriter dataFileWriter = new DataFileWriter(writer);

        dataFileWriter.create(schema, file);
        for (final Object object : data) {
            dataFileWriter.append(object);
        }

        dataFileWriter.close();
        return file;
    }

    public final File write(final Object object) throws IOException {
        return this.write(Lists.newArrayList(object));
    }

}
