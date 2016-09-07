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
package com.viadeo.kasper.core.component.event.eventbus.spring;

import com.google.common.collect.ImmutableMap;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RabbitMQConfigurationUTest {

    private RabbitMQConfiguration configuration;

    @Before
    public void setup () {
        configuration = new RabbitMQConfiguration();
    }

    @Test
    public void getAddresses_withSingleHost_isOk() {
        // Given
        Config config = ConfigFactory.parseMap(
                ImmutableMap.<String, Object>builder()
                        .put("infrastructure.rabbitmq.hosts", "miaou")
                        .put("infrastructure.rabbitmq.port", "5672")
                        .build()
        );

        // When
        String addresses = configuration.getAddresses(config);

        // Then
        assertNotNull(addresses);
        assertEquals("miaou:5672", addresses);
    }

    @Test
    public void getAddresses_withMultiHosts_isOk() {
        // Given
        Config config = ConfigFactory.parseMap(
                ImmutableMap.<String, Object>builder()
                        .put("infrastructure.rabbitmq.hosts", "miaou1, miaou2")
                        .put("infrastructure.rabbitmq.port", "5672")
                        .build()
        );

        // When
        String addresses = configuration.getAddresses(config);

        // Then
        assertNotNull(addresses);
        assertEquals("miaou1:5672,miaou2:5672", addresses);
    }
}
