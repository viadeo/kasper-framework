package com.viadeo.kasper.index.hive;

import com.google.common.collect.Lists;
import org.apache.hadoop.hive.service.HiveInterface;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class HiveRunnerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(HiveRunnerTest.class);

    private final String script = "" +
            "SET A = 'b';\n" +
            "CREATE EXTERNAL TABLE Member\n" +
            "    ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe'\n" +
            "    STORED AS\n" +
            "    INPUTFORMAT  'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat'\n" +
            "    OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat'\n" +
            "    LOCATION '/user/mglcel/examples/input/'\n" +
            "    TBLPROPERTIES ( 'avro.schema.url'='${hiveconf:SCHEMA_AVRO_MEMBER}' );";

    private final String[] expectedCommands = new String[] {
            "SET A = 'b'",
            "CREATE EXTERNAL TABLE Member " +
                    "ROW FORMAT SERDE 'org.apache.hadoop.hive.serde2.avro.AvroSerDe' " +
                    "STORED AS INPUTFORMAT  'org.apache.hadoop.hive.ql.io.avro.AvroContainerInputFormat' " +
                    "OUTPUTFORMAT 'org.apache.hadoop.hive.ql.io.avro.AvroContainerOutputFormat' " +
                    "LOCATION '/user/mglcel/examples/input/' " +
                    "TBLPROPERTIES ( 'avro.schema.url'='${hiveconf:SCHEMA_AVRO_MEMBER}' )"
    };

    @Test
    public void testsRunScript() throws Exception {
        // Given
        final List<String> commands = Lists.newArrayList();

        final HiveInterface client = mock(HiveInterface.class, new Answer() {
            private Method execute = HiveInterface.class.getMethod("execute", String.class);
            @Override
            public Object answer(final InvocationOnMock invocation) throws NoSuchMethodException {
                 if (invocation.getMethod().equals(execute)) {
                     final String command = (String) invocation.getArguments()[0];
                     commands.add(command);
                 }
                return null;
            }
        });

        // When
        HiveRunner.runScript(client, new ByteArrayInputStream(script.getBytes()));

        // Then
        LOGGER.info(commands.toString());
        assertEquals(expectedCommands.length, commands.size());
        int i = 0;
        for (final String command : commands) {
            assertEquals(expectedCommands[i++], command);
        }
    }

}
