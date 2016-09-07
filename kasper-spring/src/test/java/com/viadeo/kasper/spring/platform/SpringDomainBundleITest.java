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
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.spring.platform;

import com.google.common.collect.Lists;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;
import com.viadeo.kasper.platform.bundle.sample.MyCustomDomainBox;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.platform.plugin.PluginAdapter;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SpringDomainBundleITest {

    private static class SpyPlugin extends PluginAdapter {

        final List<DomainDescriptor> domainDescriptors = Lists.newArrayList();

        @Override
        public void onDomainRegistered(DomainDescriptor domainDescriptor) {
            domainDescriptors.add(domainDescriptor);
        }
    }

    // ------------------------------------------------------------------------

    @Test
    public void build_withSpringDomainBundle_usingMyCustomDomainSpringConfiguration_shouldBeOk() {
        // Given
        final SpyPlugin spy = new SpyPlugin();

        final SpringDomainBundle domainBundle = new SpringDomainBundle(
            new MyCustomDomainBox.MyCustomDomain(),
            MyCustomDomainBox.MyCustomDomainSpringConfiguration.class
        );

        final Platform.Builder builder = Platforms.newDefaultBuilder(new KasperPlatformConfiguration())
                .addDomainBundle(domainBundle)
                .addPlugin(spy);

        // When
        final Platform platform = builder.build();

        // Then
        assertNotNull(platform);
        assertEquals(1, spy.domainDescriptors.size());

        final DomainDescriptor domainDescriptor = spy.domainDescriptors.get(0);

        assertEquals(MyCustomDomainBox.MyCustomDomain.class, domainDescriptor.getDomainClass());
        assertEquals(1, domainDescriptor.getCommandHandlerDescriptors().size());
        assertEquals(1, domainDescriptor.getQueryHandlerDescriptors().size());
        assertEquals(1, domainDescriptor.getEventListenerDescriptors().size());
        assertEquals(1, domainDescriptor.getRepositoryDescriptors().size());
    }

}
