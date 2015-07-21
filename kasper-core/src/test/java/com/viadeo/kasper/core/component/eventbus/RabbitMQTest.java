package com.viadeo.kasper.core.component.eventbus;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

// /!\ This util test seems not working well with `flashTest` usage
public class RabbitMQTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQTest.class);

    public static void stopRabbitMQ() throws URISyntaxException, IOException {
//        execute(AMQPTopology.class.getClassLoader().getResourceAsStream("amqp/stop.sh"));

        URL resource = RabbitMQTest.class.getClassLoader().getResource("amqp/stop.sh");
        LOGGER.info("stopRabbitMQ: " + resource.getPath());
        execute(new String[]{new File(resource.toURI()).getAbsolutePath()});
    }

    public static void startRabbitMQ() throws URISyntaxException, IOException {
//        execute(AMQPTopology.class.getClassLoader().getResourceAsStream("amqp/start.sh"));

        URL resource = RabbitMQTest.class.getClassLoader().getResource("amqp/start.sh");
        LOGGER.info("startRabbitMQ: " + resource.getPath());
        execute(new String[]{new File(resource.toURI()).getAbsolutePath()});
    }

    public static void execute(InputStream inputStream) throws IOException {
        InputStream resourceAsStream = RabbitMQTest.class.getClassLoader().getResourceAsStream("amqp/start.sh");

        List<String> commands = Lists.newArrayList();
        Scanner scanner = new Scanner(new InputStreamReader(resourceAsStream));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if ( ! line.trim().startsWith("#")) {
                commands.add(line);
            }
        }

        execute(commands.toArray(new String[commands.size()]));
    }

    public static void execute(String[] commands) throws IOException {
        Process process = Runtime.getRuntime().exec(commands);

        Scanner scanner = new Scanner(new InputStreamReader(process.getInputStream()));
        while (scanner.hasNextLine()) {
            LOGGER.info(scanner.nextLine());
        }

        scanner = new Scanner(new InputStreamReader(process.getErrorStream()));
        while (scanner.hasNextLine()) {
            LOGGER.info(scanner.nextLine());
        }
    }
}
