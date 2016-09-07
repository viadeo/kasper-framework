// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
package com.viadeo.kasper.core.component.event.eventbus;

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
