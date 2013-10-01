package com.viadeo.kasper.index.hadoop.tests;

import com.google.common.base.Optional;
import org.apache.avro.generic.GenericRecord;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class AvroTestComparator {

    private File avroFile = null;
    private Iterator<Map<String, String>> testData = null;

    // ------------------------------------------------------------------------

    private AvroTestComparator() { }

    public static AvroTestComparator forFile(final File avroFile) {
        final AvroTestComparator avroTestComparator = new AvroTestComparator();
        avroTestComparator.avroFile = avroFile;
        return avroTestComparator;
    }

    // ------------------------------------------------------------------------

    public AvroTestComparator withTestData(final Iterator<Map<String, String>> testData) {
        this.testData = testData;
        return this;
    }

    // ------------------------------------------------------------------------

    public void proceed() {
        final AvroReader avroReader = new AvroReader(avroFile);

        /* Compares results */
        int resultNbRows = 0;
        try {
            while (avroReader.hasNext()) {
                Optional<GenericRecord> record = avroReader.next();

                if (!testData.hasNext()) {
                    throw new RuntimeException("Result has more records than expected");
                }

                final Map<String, String> dataRecord = testData.next();

                int row = 1;
                for (final Map.Entry<String, String> dataField : dataRecord.entrySet()) {
                    final Object field = record.get().get(dataField.getKey());
                    if (null == field) {
                        throw new RuntimeException(
                                String.format("Field '%s' does not exists in resulting AVRO",
                                        dataField.getKey()));
                    }
                    final String dataFieldValue = dataField.getValue();
                    if (!dataFieldValue.contentEquals(field.toString())) {
                        throw new RuntimeException(String.format(
                                "In row [%d], field '%s' does not corresponds : '%s' vs '%s'",
                                row,
                                dataField.getKey(),
                                dataFieldValue,
                                field.toString()));
                    }
                    row++;
                }

                resultNbRows++;
            }
        } catch (final IOException e) {
            throw new RuntimeException("Unable to read AVRO file", e);
        }

        if (testData.hasNext()) {
            int expectedNbRows = resultNbRows + 1;
            while (testData.hasNext()) {
                expectedNbRows++;
            }
            throw new RuntimeException(String.format(
                    "Result has not the same number of rows (%d) than expected (%d)",
                    resultNbRows, expectedNbRows));
        }
    }

}
