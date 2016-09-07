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
package com.viadeo.kasper.client;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import static com.google.common.base.Preconditions.checkNotNull;

public class ManifestReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManifestReader.class);

    public static final String KASPER_VERSION_NAME = "Kasper-Version";

    private final URL url;
    private Optional<Manifest> manifest;

    // ------------------------------------------------------------------------

    public ManifestReader(final Class<?> clazz) {
        this(toUrl(toPath(checkNotNull(clazz))));
    }

    public ManifestReader(final URL url) {
        this.url = checkNotNull(url, "undefined url");
        this.manifest = Optional.absent();
    }

    // ------------------------------------------------------------------------

    protected static String toPath(final Class<?> clazz) {
        checkNotNull(clazz, "unable to define a path from an undefined class");
        return clazz.getName().replaceAll("\\.", "/") + ".class";
    }

    protected static URL toUrl(final String path) {
        checkNotNull(path, "unable to define a path from an undefined class");
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }

    public ManifestReader read() {
        Manifest manifest = null;

        try {
            final JarURLConnection connection = (JarURLConnection) url.openConnection();
            final JarFile jar = connection.getJarFile();
            manifest = jar.getManifest();
        } catch (final IOException e) {
            LOGGER.error("No found jar file from the specified URL '{}'", url, e);
        }

        this.manifest = Optional.fromNullable(manifest);
        return this;
    }

    public Optional<Manifest> getManifest() {
        return manifest;
    }

    public Optional<String> getKasperVersion() {
        if(manifest.isPresent()) {
            final String version = manifest.get().getMainAttributes().getValue(KASPER_VERSION_NAME);
            return Optional.fromNullable(version);
        }
        return Optional.absent();
    }

}
